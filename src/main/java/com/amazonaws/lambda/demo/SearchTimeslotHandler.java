package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.db.SchedulerDAO;
import com.amazonaws.db.TimeslotDAO;
import com.amazonaws.model.SearchResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

public class SearchTimeslotHandler implements RequestStreamHandler{
	
	public LambdaLogger logger = null;


	boolean validWeek(String sId, int week) throws Exception {
		if (logger != null) { logger.log("check the week" + week); }
		SchedulerDAO dao = new SchedulerDAO();
		int realWeek = week + getStartDay(sId);
		if(week == 0) {
			return false;
		}
		else {
			return dao.checkWeek(sId, realWeek);	
		}	
	}
	
	ArrayList<SearchResult> SearchTimeslot(String scheduleId,int year, int month, int dayOfWeek, int dayOfMonth, String beginTime, String endTime) throws Exception {
		if (logger != null) { logger.log("Search timeslot by scheduleId: " + scheduleId); }
		TimeslotDAO dao = new TimeslotDAO();
		return dao.filterTimeslot(scheduleId,year,month,dayOfMonth,dayOfWeek,beginTime,endTime);
	}
	
	
	int getStartDay(String sId) throws Exception{
		SchedulerDAO dao = new SchedulerDAO();
		if(dao.getStartDayOfWeek(sId) > 5) {
			return 1;
		}
		else {
			return 0;
		}
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

		SearchTimeslotResponse response = null;
		
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
				response = new SearchTimeslotResponse("name", 200);  // OPTIONS needs a 200 response
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
			response = new SearchTimeslotResponse("Bad Request:" + pe.getMessage(), 404);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}

		if (!processed) {
			SearchTimeslotRequest req = new Gson().fromJson(body, SearchTimeslotRequest.class);
			logger.log(req.toString());

			SearchTimeslotResponse resp;
			try {
				if (validWeek(req.scheduleId, 1)){
					resp = new SearchTimeslotResponse("Successfully get the open Timeslots in schedule:" + req.scheduleId,SearchTimeslot(req.scheduleId,req.year,req.month,req.dayOfWeek,req.dayOfMonth,req.beginTime,req.endTime) );
				} else {
					resp = new SearchTimeslotResponse("Open timslot does not exists under your search conditions", 405);
				}
			} catch (Exception e) {
				resp = new SearchTimeslotResponse("Unable to search timslots " + req.scheduleId + "(" + e.getMessage() + ")", 403);
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
