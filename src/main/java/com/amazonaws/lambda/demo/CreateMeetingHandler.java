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

import com.amazonaws.db.MeetingDAO;
import com.amazonaws.db.SchedulerDAO;
import com.amazonaws.model.Day;
import com.amazonaws.model.Meeting;
import com.amazonaws.model.Schedule;
import com.amazonaws.model.Timeslot;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

public class CreateMeetingHandler implements RequestStreamHandler{
	public LambdaLogger logger = null;
	String mId;
	String sCode;

	//ArrayList<String> dayID = new ArrayList<String>();
	//ArrayList<Timeslot> timeslots = new ArrayList<Timeslot>();
	//String ct;

	/** Load from RDS, if it exists
	 * 
	 * @throws Exception 
	 */
	boolean createMeeting(String partInfo, String timeslotId) throws Exception {
		if (logger != null) { logger.log("in createSchedule"); }
		MeetingDAO dao = new MeetingDAO();
		String meetingId = this.genUUIDString();
		String seCode = this.genUUIDString();
		mId = meetingId;
		sCode = seCode;
		
		// need to check if the timeslot is existed or occupied
		Meeting meeting = new Meeting (mId, sCode, partInfo, timeslotId);
		return dao.addMeeting(meeting);
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
		} catch (org.json.simple.parser.ParseException pe) {
			logger.log(pe.toString());
			response = new CreateScheduleResponse("Bad Request:" + pe.getMessage(), 422);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}

		if (!processed) {
			CreateMeetingRequest req = new Gson().fromJson(body, CreateMeetingRequest.class);
			logger.log(req.toString());

			CreateMeetingResponse resp;
			try {
				if (createMeeting(req.partInfo, req.timeslotId)) {
					resp = new CreateMeetingResponse("Successfully Created Meeting:",sCode);
				} else {
					resp = new CreateMeetingResponse("timeslot not existed or already been occupied: ", 405);
				}
			} catch (Exception e) {
				resp = new CreateMeetingResponse("Unable to create meeting:" + "(" + e.getMessage() + ")", 403);
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

