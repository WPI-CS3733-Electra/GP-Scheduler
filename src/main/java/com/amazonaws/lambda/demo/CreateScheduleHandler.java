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

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.db.SchedulerDAO;
import com.amazonaws.model.Day;
import com.amazonaws.model.Schedule;
import com.amazonaws.model.Timeslot;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

public class CreateScheduleHandler implements RequestStreamHandler{
	public LambdaLogger logger = null;
	String sId = this.genUUIDString();
	String sCode = this.genUUIDString();
	String rCode = this.genUUIDString();

	/** Load from RDS, if it exists
	 * 
	 * @throws Exception 
	 */
	boolean createSchedule(String name, String author, String startDate, String endDate, String startTime, String endTime, int timePeriod) throws Exception {
		if (logger != null) { logger.log("in createSchedule"); }
		SchedulerDAO dao = new SchedulerDAO();
		int numOfDays = this.daysBetweenDates(startDate, endDate);
		int numOfMins = this.minutesBetweenTimes(startTime, endTime);
		int numOfTs = numOfMins / timePeriod;
		ArrayList<Day> days = new ArrayList<Day>();
		
		// Create the list of days, also the sub-structures.
		for(int i = 0; i < numOfDays; i++) {
			String dId = this.genUUIDString();
			LocalDate d = this.calDate(startDate, i);
			ArrayList<Timeslot> ts = new ArrayList<Timeslot>();
			for(int j = 0; j < numOfTs; j ++) {
				LocalTime t = this.calBeginTime(startTime, timePeriod, j);
				Timeslot timeslot = new Timeslot(this.genUUIDString(),t,dId,null);
				ts.add(timeslot);
			}
			Day day = new Day(dId,d,ts,sId);
			days.add(day);
		}
		
		// check if present
		Schedule exist = dao.getSchedule(sId);
		Schedule Schedule = new Schedule (sId, name, author, sCode, rCode, days, LocalDate.now(), timePeriod, this.stringToTime(startTime), this.stringToTime(endTime));
		if (exist == null) {
			return dao.addSchedule(Schedule);
		} else {
			return false;
		}
	}
	
	String genUUIDString() {
		UUID u = UUID.randomUUID();
		String s = u.toString();
		return s;
	}
	
	
	int daysBetweenDates(String s1, String s2) throws ParseException{
			Date d1 = new SimpleDateFormat("dd/MM/yyyy").parse(s1);
			Date d2 = new SimpleDateFormat("dd/MM/yyyy").parse(s2);
			return Days.daysBetween(
	                new LocalDate(d1.getTime()), 
	                new LocalDate(d2.getTime())).getDays();

		 }
	
	int minutesBetweenTimes(String s1, String s2) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Date date1 = format.parse(s1);
		Date date2 = format.parse(s2);
		long difference = (date2.getTime() - date1.getTime())/60000;
		return (int)difference;
	}
	
	LocalTime stringToTime(String s) throws ParseException {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
		return formatter.parseLocalTime(s);
	}
	
	LocalTime calBeginTime(String startTime, int timePeriod, int index) throws ParseException {
        LocalTime time = this.stringToTime(startTime);
        time = time.plusMinutes(index * timePeriod);
        return time;	
	}
	
	LocalDate calDate(String startDate, int index) throws ParseException {
		return LocalDate.parse(startDate).plusDays(index);
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

		CreateScheduleResponse response = null;
		
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
				response = new CreateScheduleResponse("name", 200);  // OPTIONS needs a 200 response
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
			response = new CreateScheduleResponse("Bad Request:" + pe.getMessage(), 422);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}

		if (!processed) {
			CreateScheduleRequest req = new Gson().fromJson(body, CreateScheduleRequest.class);
			logger.log(req.toString());

			CreateScheduleResponse resp;
			try {
				if (createSchedule(req.name, req.author, req.startDate,req.endDate, req.startTime, req.endTime, req.timePeriod)) {
					resp = new CreateScheduleResponse("Successfully defined constant:" + req.name,sCode,rCode,sId);
				} else {
					resp = new CreateScheduleResponse("Unable to create Schedule: " + req.name, 405);
				}
			} catch (Exception e) {
				resp = new CreateScheduleResponse("Unable to create Schedule: " + req.name + "(" + e.getMessage() + ")", 403);
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

}
