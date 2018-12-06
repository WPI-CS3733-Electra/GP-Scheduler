package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.db.TimeslotDAO;
import com.amazonaws.model.Day;
import com.amazonaws.model.Timeslot;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

public class DeleteTimeslotsByTimeHandler implements RequestStreamHandler{
	public LambdaLogger logger = null;

	//ArrayList<String> dayID = new ArrayList<String>();
	//ArrayList<Timeslot> timeslots = new ArrayList<Timeslot>();
	//String ct;

	/** Load from RDS, if it exists
	 * 
	 * @throws Exception 
	 */

		
	boolean DeleteTimeslotsByTime(String scheduleId, String beginTime) throws Exception {
		if (logger != null) { logger.log("open timeslots in by timePeriod"); }
		
		TimeslotDAO dao = new TimeslotDAO();
		
		return dao.deleteTimeslotByTime(scheduleId, beginTime);
	}
	
	
	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler to create constant");

		JSONObject headerJson = new JSONObject();
		headerJson.put("Content-Type",  "application/json");  // not sure if needed anymore?
		headerJson.put("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
	    headerJson.put("Access-Control-Allow-Origin",  "*");
	        
		JSONObject responseJson = new JSONObject();
		responseJson.put("headers", headerJson);

		DeleteTimeslotsByTimeResponse response = null;
		
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
				response = new DeleteTimeslotsByTimeResponse("name", 200);  // OPTIONS needs a 200 response
		        responseJson.put("body", new Gson().toJson(response));
		        processed = true;
		        body = null;
			} else {
				body = (String)event.get("body");
				if (body == null) {
					body = event.toJSONString();  // this is only here to make testing easier
				}
			}
		} catch (org.json.simple.parser.ParseException pe) {
			logger.log(pe.toString());
			response = new DeleteTimeslotsByTimeResponse("Bad Request:" + pe.getMessage(), 422);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}

		if (!processed) {
			DeleteTimeslotsByTimeRequest req = new Gson().fromJson(body, DeleteTimeslotsByTimeRequest.class);
			logger.log(req.toString());

			DeleteTimeslotsByTimeResponse resp;
			try {
				if (DeleteTimeslotsByTime(req.scheduleId, req.beginTime)) {
					resp = new DeleteTimeslotsByTimeResponse("Successfully Delete Timeslots Start at: " + req.beginTime);
				} else {
					resp = new DeleteTimeslotsByTimeResponse("timeslots do not exist: ", 405);
				}
			} catch (Exception e) {
				resp = new DeleteTimeslotsByTimeResponse("Unable to create timeslots:" + "(" + e.getMessage() + ")", 403);
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
