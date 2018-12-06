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

import com.amazonaws.db.MeetingDAO;
import com.amazonaws.db.TimeslotDAO;
import com.amazonaws.model.Meeting;
import com.amazonaws.model.Schedule;
import com.amazonaws.model.Timeslot;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

public class OpenTimeslotsByDayHandler implements RequestStreamHandler {
	public LambdaLogger logger = null;

	//ArrayList<String> dayID = new ArrayList<String>();
	//ArrayList<Timeslot> timeslots = new ArrayList<Timeslot>();
	//String ct;

	/** Load from RDS, if it exists
	 * 
	 * @throws Exception 
	 */
	
	Schedule getScheduleInfo(String scheduleId, String dayId) throws Exception {
		TimeslotDAO dao = new TimeslotDAO();
		return dao.getScheduleTimeInfo(scheduleId, dayId);
		
	}
	boolean openTimeslotsByDay(String dayId, String scheduleId) throws Exception {
		if (logger != null) { logger.log("open timeslots by day"); }
		
		TimeslotDAO dao = new TimeslotDAO();
		
		String startTime = getScheduleInfo(scheduleId, dayId).getStartTime();
		String endTime = getScheduleInfo(scheduleId, dayId).getEndTime();
		ArrayList<Timeslot> list = getScheduleInfo(scheduleId, dayId).getDays().get(0).getTimeslots();
		int timePeriod = getScheduleInfo(scheduleId, dayId).getTimePeriod();
		int numOfMins = this.minutesBetweenTimes(startTime, endTime);
		int numOfTs = numOfMins / timePeriod;
		
		ArrayList<Timeslot> tslist = new ArrayList<Timeslot>();

	
		for(int i = 0; i < numOfTs; i++ ) {
			LocalTime beginTime = calBeginTime(startTime,timePeriod,i);
			if(beginTime.equals(stringToTime(list.get(i).getBeginTime()))){}
			else {
				String tsId = this.genUUIDString();
				Timeslot ts = new Timeslot(tsId,beginTime.toString(),null,dayId);
				tslist.add(ts);
				list.add(i, ts);
			}
			
		}
		
		return dao.openTimeslots(tslist);
	}
	
	LocalTime calBeginTime(String startTime, int timePeriod, int index) throws ParseException {
        LocalTime time = this.stringToTime(startTime);
        time = time.plusMinutes(index * timePeriod);
        return time;	
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

		OpenTimeslotsByDayResponse response = null;
		
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
				response = new OpenTimeslotsByDayResponse("name", 200);  // OPTIONS needs a 200 response
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
			response = new OpenTimeslotsByDayResponse("Bad Request:" + pe.getMessage(), 422);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}

		if (!processed) {
			OpenTimeslotsByDayRequest req = new Gson().fromJson(body, OpenTimeslotsByDayRequest.class);
			logger.log(req.toString());

			 OpenTimeslotsByDayResponse resp;
			try {
				if (openTimeslotsByDay(req.dayId, req.scheduleId)) {
					resp = new OpenTimeslotsByDayResponse("Successfully Created Timeslots by day: " + req.dayId);
				} else {
					resp = new OpenTimeslotsByDayResponse("timeslots spaces already been occupied: ", 405);
				}
			} catch (Exception e) {
				resp = new OpenTimeslotsByDayResponse("Unable to create timeslots:" + "(" + e.getMessage() + ")", 403);
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
