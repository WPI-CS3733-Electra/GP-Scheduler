package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.db.SchedulerDAO;
import com.amazonaws.model.Schedule;
import com.amazonaws.model.Timeslot;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;


public class ShowWeekScheduleHandler implements RequestStreamHandler{
	
	public LambdaLogger logger = null;

	/** Load from RDS, if it exists
	 * 
	 * @throws Exception 
	 */
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
	
	int getStartDay(String sId) throws Exception{
		SchedulerDAO dao = new SchedulerDAO();
		if(dao.getStartDayOfWeek(sId) > 5) {
			return 1;
		}
		else {
			return 0;
		}
	}
	Schedule showSchedule(String sId, int week) throws Exception {
		if (logger != null) { logger.log("get Schedule in week" + week); }
		SchedulerDAO dao = new SchedulerDAO();
		int realWeek = week + getStartDay(sId);
		Schedule origin = dao.showWeek(sId, realWeek);
		String startTime = origin.getStartTime();
		String endTime = origin.getEndTime();
		int timePeriod = origin.getTimePeriod();
		int numOfMins = this.minutesBetweenTimes(startTime, endTime);
		int numOfTs = numOfMins / timePeriod;
		
		for (int i = 0; i < origin.getDays().size(); i++) {
			if(origin.getDays().get(i).getTimeslots() != null) {
			
			
			ArrayList<Timeslot> tslist = origin.getDays().get(i).getTimeslots();
			
			
			if(tslist.isEmpty()) {
				for(int k =0; k < numOfTs; k++) {
					tslist.add(null);
				}
			}
			else {
			for(int j = 0; j < numOfTs; j ++) {
				LocalTime beginTime = calBeginTime(startTime, timePeriod, j);
				if(j >= tslist.size()) {
					tslist.add(null);
				}
				else {
				if(stringToTime(tslist.get(j).getBeginTime()).equals(beginTime)) {}
				else {
					tslist.add(j, null);
				}
				}
			}	
			}
		}
		}
		
		
		return origin;
	}
	
	int minutesBetweenTimes(String s1, String s2) throws java.text.ParseException {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Date date1 = format.parse(s1);
		Date date2 = format.parse(s2);
		long difference = (date2.getTime() - date1.getTime())/60000;
		return (int)difference;
	}
	
	LocalTime calBeginTime(String startTime, int timePeriod, int index) throws ParseException {
        LocalTime time = this.stringToTime(startTime);
        time = time.plusMinutes(index * timePeriod);
        return time;	
	}
	
	LocalTime stringToTime(String s) throws ParseException {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
		return formatter.parseLocalTime(s);
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

		ShowWeekScheduleResponse response = null;
		
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
				response = new ShowWeekScheduleResponse("name", 200);  // OPTIONS needs a 200 response
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
			response = new ShowWeekScheduleResponse("Bad Request:" + pe.getMessage(), 404);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}

		if (!processed) {
			ShowWeekScheduleRequest req = new Gson().fromJson(body, ShowWeekScheduleRequest.class);
			logger.log(req.toString());

			ShowWeekScheduleResponse resp;
			try {
				if (validWeek(req.id, req.week)) {
					resp = new ShowWeekScheduleResponse("Successfully show the week:" + req.week, showSchedule(req.id, req.week));
				} else {
					resp = new ShowWeekScheduleResponse("Invalid Schedule ID or week number", 405);
				}
			} catch (Exception e) {
				resp = new ShowWeekScheduleResponse("Unable to show Schedule: " + req.id + "(" + e.getMessage() + ")", 403);
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
