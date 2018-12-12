package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.db.SchedulerDAO;
import com.amazonaws.model.Schedule;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

public class ParticipantGetScheduleIdHandler implements RequestStreamHandler {
	public LambdaLogger logger = null;

	/** Load from RDS, if it exists
	 * 
	 * @throws Exception 
	 */
	boolean checkByRCode(String releaseCode) throws Exception {
		if (logger != null) { logger.log("check the existence of Schedule by releaseCode: " + releaseCode); }
		SchedulerDAO dao = new SchedulerDAO();
		return dao.checkByRCode(releaseCode);
	}
	
	String getScheduleIdPar(String releaseCode) throws Exception {
		if (logger != null) { logger.log("get Schedule by releaseCode: " + releaseCode); }
		SchedulerDAO dao = new SchedulerDAO();
		return dao.GetScheduleIdPar(releaseCode);
	}
	
	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler to get scheduleId by schedule's releaseCode");

		JSONObject headerJson = new JSONObject();
		headerJson.put("Content-Type",  "application/json");  // not sure if needed anymore?
		headerJson.put("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
	    headerJson.put("Access-Control-Allow-Origin",  "*");
	        
		JSONObject responseJson = new JSONObject();
		responseJson.put("headers", headerJson);

		ParticipantGetScheduleIdResponse response = null;
		
		// extract body from incoming HTTP POST request. If any error, then return 422 error
		String body;
		boolean processed = false;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			JSONParser parser = new JSONParser();
			JSONObject event = (JSONObject) parser.parse(reader);
			logger.log("event:" + event.toJSONString());
			
			String method = (String) event.get("httpMethod");
			if (method != null && method.equalsIgnoreCase("OPTIONS")) {
				logger.log("Options request");
				response = new ParticipantGetScheduleIdResponse("name", 200);  // OPTIONS needs a 200 response
		        responseJson.put("body", new Gson().toJson(response));
		        processed = true;
		        body = null;
			} else {
				body = (String)event.get("body");
				if (body == null) {
					body = event.toJSONString();  // this is only here to make testing easier
				}
			}
		} catch (ParseException pe) {
			logger.log(pe.toString());
			response = new ParticipantGetScheduleIdResponse("Bad Request:" + pe.getMessage(), 404);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}

		if (!processed) {
			ParticipantGetScheduleIdRequest req = new Gson().fromJson(body, ParticipantGetScheduleIdRequest.class);
			logger.log(req.toString());

			ParticipantGetScheduleIdResponse resp;
			try {
				if (checkByRCode(req.releaseCode)) {
					resp = new ParticipantGetScheduleIdResponse("Successfully get the Schedule Id by releaseCode:" + req.releaseCode, getScheduleIdPar(req.releaseCode));
				} else {
					resp = new ParticipantGetScheduleIdResponse("releaseCode does not exist", 405);
				}
			} catch (Exception e) {
				resp = new ParticipantGetScheduleIdResponse("Unable to get Schedule ID by releaseCode: " + req.releaseCode + "(" + e.getMessage() + ")", 403);
			}

			// compute proper response
	        responseJson.put("body", new Gson().toJson(resp));  
		}
		
        logger.log("end result:" + responseJson.toJSONString());
        logger.log(responseJson.toJSONString());
        OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(responseJson.toJSONString());  
        writer.close();
	}
}
