package com.amazonaws.lambda.demo;

public class OrganizerGetScheduleIdResponse {

	String response;
	int httpcode;
	String scheduleId;
	
	public OrganizerGetScheduleIdResponse (String s, int code) {
		this.response = s;
		this.httpcode = code;
	}
	
	public OrganizerGetScheduleIdResponse (String s, String scheduleId) {
		this.response = s;
		this.httpcode = 200;
		this.scheduleId = scheduleId;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}

	public String getResponse() {
		return response;
	}

	public int getHttpcode() {
		return httpcode;
	}

	public String getScheduleId() {
		return scheduleId;
	}
	
	
}
