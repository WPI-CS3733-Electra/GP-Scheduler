package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.db.AdminDAO;
import com.amazonaws.model.BriefScheduleInfo;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

public class AdminShowRecentHandler implements RequestStreamHandler{
	
		public LambdaLogger logger = null;
		String d;

		/** Load from RDS, if it exists
		 * 
		 * @throws Exception 
		 */
		boolean Oath(String code) throws Exception {
			if (logger != null) { logger.log("Oath"); }
			AdminDAO dao = new AdminDAO();
			return dao.Oath(code);
		}
		
		ArrayList<BriefScheduleInfo> showRecent(int hours) throws Exception {
			if (logger != null) { logger.log("Show Recent"); }
		    AdminDAO dao = new AdminDAO();
		    
		    		//.minusHours(hours);
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm:ss");
			//String time = formatterTime.format(origin).toString();
			
		    Date date = new Date(System.currentTimeMillis() - 3600 * 1000 * hours);
		    String recentDate = formatter.format(date).toString();
			//LocalDateTime dateTime = new LocalDateTime().minusHours(hours);
			//String date = dateTime.toString().substring(0, 9);
			//String time = dateTime.toString().substring(11, 18);
			//String recentDate =  date + " " + time;
			d = recentDate;
		    
		    return dao.reviewRecent(recentDate);
		}
		@Override
		public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
			logger = context.getLogger();
			logger.log("Loading Java Lambda handler to show recent created schedules");

			JSONObject headerJson = new JSONObject();
			headerJson.put("Content-Type",  "application/json");  // not sure if needed anymore?
			headerJson.put("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
		    headerJson.put("Access-Control-Allow-Origin",  "*");
		        
			JSONObject responseJson = new JSONObject();
			responseJson.put("headers", headerJson);

			AdminShowRecentResponse response = null;
			
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
					response = new AdminShowRecentResponse("name", 200);  // OPTIONS needs a 200 response
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
				response = new AdminShowRecentResponse("Bad Request:" + pe.getMessage(), 422);  // unable to process input
		        responseJson.put("body", new Gson().toJson(response));
		        processed = true;
		        body = null;
			}

			if (!processed) {
				AdminShowRecentRequest req = new Gson().fromJson(body, AdminShowRecentRequest.class);
				logger.log(req.toString());

				AdminShowRecentResponse resp;
				try { 
					if (Oath(req.code)) {
						resp = new AdminShowRecentResponse("Show recent schedules Succeed",showRecent(req.hours));
					} else {
						resp = new AdminShowRecentResponse("Authentication Failed: ", 405);
					}
				} catch (Exception e) {
					resp = new AdminShowRecentResponse("Unable to show recent schedules:" + "(" + e.getMessage() + ")", 403);
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
