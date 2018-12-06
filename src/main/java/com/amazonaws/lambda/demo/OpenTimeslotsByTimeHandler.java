package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.db.TimeslotDAO;
import com.amazonaws.model.Day;
import com.amazonaws.model.Schedule;
import com.amazonaws.model.Timeslot;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

public class OpenTimeslotsByTimeHandler implements RequestStreamHandler {
	public LambdaLogger logger = null;

	//ArrayList<String> dayID = new ArrayList<String>();
	//ArrayList<Timeslot> timeslots = new ArrayList<Timeslot>();
	//String ct;

	/** Load from RDS, if it exists
	 * 
	 * @throws Exception 
	 */
	
	ArrayList<Day> getScheduleInfo(String scheduleId, String beginTime) throws Exception {
		TimeslotDAO dao = new TimeslotDAO();
		return dao.getScheduleDayInfo(scheduleId, beginTime);
		
	}
	boolean openTimeslotsByTime(String scheduleId, String beginTime) throws Exception {
		if (logger != null) { logger.log("open timeslots in by timePeriod"); }
		
		TimeslotDAO dao = new TimeslotDAO();
		
		ArrayList<Day> days = getScheduleInfo(scheduleId, beginTime);

		
		ArrayList<Timeslot> tslist = new ArrayList<Timeslot>();

	
		for(int i = 0; i < days.size(); i++) {
			String dayId = days.get(i).getId();
			String tId = genUUIDString();
			Timeslot ts = new Timeslot(tId,beginTime,null,dayId);	
			tslist.add(ts);
		}
		
		return dao.openTimeslots(tslist);
	}
		
	String genUUIDString() {
		UUID u = UUID.randomUUID();
		String s = u.toString();
		return s;
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

		OpenTimeslotsByTimeResponse response = null;
		
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
				response = new OpenTimeslotsByTimeResponse("name", 200);  // OPTIONS needs a 200 response
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
			response = new OpenTimeslotsByTimeResponse("Bad Request:" + pe.getMessage(), 422);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}

		if (!processed) {
			OpenTimeslotsByTimeRequest req = new Gson().fromJson(body, OpenTimeslotsByTimeRequest.class);
			logger.log(req.toString());

			OpenTimeslotsByTimeResponse resp;
			try {
				if (openTimeslotsByTime(req.scheduleId, req.beginTime)) {
					resp = new OpenTimeslotsByTimeResponse("Successfully Created Timeslots Start at: " + req.beginTime);
				} else {
					resp = new OpenTimeslotsByTimeResponse("timeslots spaces already been occupied: ", 405);
				}
			} catch (Exception e) {
				resp = new OpenTimeslotsByTimeResponse("Unable to create timeslots:" + "(" + e.getMessage() + ")", 403);
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
