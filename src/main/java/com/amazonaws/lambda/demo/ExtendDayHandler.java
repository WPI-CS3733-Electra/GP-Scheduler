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

public class ExtendDayHandler implements RequestStreamHandler{
	public LambdaLogger logger = null;

	//ArrayList<String> dayID = new ArrayList<String>();
	//ArrayList<Timeslot> timeslots = new ArrayList<Timeslot>();
	//String ct;

	/** Load from RDS, if it exists
	 * 
	 * @throws Exception 
	 * 
	 */
	
	Schedule getInfo(String sId) throws Exception {
		SchedulerDAO dao = new SchedulerDAO();
		return dao.getScheduleInfo(sId);
		
	}
	
	boolean ExtendDay(String scheduleId, String startDate, String endDate) throws Exception {
		if (logger != null) { logger.log("in Extend Day"); }
		SchedulerDAO dao = new SchedulerDAO();
		String startTime = getInfo(scheduleId).getStartTime();
		String endTime = getInfo(scheduleId).getEndTime();
		String oldStartDate = getInfo(scheduleId).getStartDate();
		String oldEndDate = getInfo(scheduleId).getEndDate();
		int timePeriod = getInfo(scheduleId).getTimePeriod();
		int numOfMins = this.minutesBetweenTimes(startTime, endTime);
		int numOfTs = numOfMins / timePeriod;
		
		int numOfDaysB;
		int numOfDaysA;
		String newStartDate;
		String newEndDate;
		
		ArrayList<Day> days = new ArrayList<Day>();
		
		if(LocalDate.parse(startDate).isBefore(LocalDate.parse(oldStartDate))) {
			numOfDaysB = daysBetweenDates(startDate,oldStartDate) - 1;
			newStartDate = startDate;
			
			for(int i = 0; i <= numOfDaysB; i++) {
				String dId = this.genUUIDString();
				LocalDate d = this.calDate(newStartDate, i);
				ArrayList<Timeslot> ts = new ArrayList<Timeslot>();
				for(int j = 0; j < numOfTs; j ++) {
					String t = this.calBeginTime(startTime, timePeriod, j).toString();
					Timeslot timeslot = new Timeslot(this.genUUIDString(),t, null, dId);
					ts.add(timeslot);
				}
				Day day = new Day(dId,d.toString(),ts,scheduleId);
				
				if(!(d.getDayOfWeek() == 6 || d.getDayOfWeek() ==7)) {
					days.add(day);
				}
			}
		}
		else if(LocalDate.parse(startDate).isEqual(LocalDate.parse(oldStartDate))) {
			numOfDaysB = 0;
			newStartDate = oldStartDate;
		}
		else {
			return false;
		}
		
		
		if(LocalDate.parse(endDate).isAfter(LocalDate.parse(oldEndDate))) {
			numOfDaysA = daysBetweenDates(oldEndDate,endDate) - 1;
			newEndDate = endDate;
			
			for(int i = 0; i <= numOfDaysA; i++) {
				String dId = this.genUUIDString();
				LocalDate d = this.calDate(LocalDate.parse(oldEndDate).plusDays(1).toString(), i);
				ArrayList<Timeslot> ts = new ArrayList<Timeslot>();
				for(int j = 0; j < numOfTs; j ++) {
					String t = this.calBeginTime(startTime, timePeriod, j).toString();
					Timeslot timeslot = new Timeslot(this.genUUIDString(),t, null, dId);
					ts.add(timeslot);
				}
				Day day = new Day(dId,d.toString(),ts,scheduleId);
				
				if(!(d.getDayOfWeek() == 6 || d.getDayOfWeek() ==7)) {
					days.add(day);
				}
			}
		}
		else if(LocalDate.parse(endDate).isEqual(LocalDate.parse(oldEndDate))) {
			numOfDaysA = 0;
			newEndDate = oldEndDate;
		}
		else {
			return false;
		}
		
		return dao.extendDate(scheduleId, newStartDate, newEndDate, days);
		

	}
	
	/*ID generator*/
	String genUUIDString() {
		UUID u = UUID.randomUUID();
		String s = u.toString();
		return s;
	}
	
	String currentTimeString() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	    Date date = new Date();  
	    return formatter.format(date).toString();  
	}
	
	
	int daysBetweenDates(String s1, String s2) throws ParseException{
			Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse(s1);
			Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse(s2);
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
		logger.log("Loading Java Lambda handler to extend day");

		JSONObject headerJson = new JSONObject();
		headerJson.put("Content-Type",  "application/json");  // not sure if needed anymore?
		headerJson.put("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
	    headerJson.put("Access-Control-Allow-Origin",  "*");
	        
		JSONObject responseJson = new JSONObject();
		responseJson.put("headers", headerJson);

		ExtendDayResponse response = null;
		
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
				response = new ExtendDayResponse("name", 200);  // OPTIONS needs a 200 response
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
			response = new ExtendDayResponse("Bad Request:" + pe.getMessage(), 422);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}

		if (!processed) {
			ExtendDayRequest req = new Gson().fromJson(body, ExtendDayRequest.class);
			logger.log(req.toString());

			ExtendDayResponse resp;
			try {
				if (ExtendDay(req.scheduleId,req.startDate,req.endDate)) {
					resp = new ExtendDayResponse("Successfully extended Schedule:" + req.scheduleId);
				} else {
					resp = new ExtendDayResponse("failed to extend Schedule: " + req.scheduleId, 405);
				}
			} catch (Exception e) {
				resp = new ExtendDayResponse("Unable to extend Schedule:" + "(" + e.getMessage() + ")", 403);
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
