package com.amazonaws.lambda.demo;

public class OrganizerGetScheduleIdResponse {

	String response;
	int httpCode;
	String scheduleId;
	
	public OrganizerGetScheduleIdResponse (String s, int code) {
		this.response = s;
		this.httpCode = code;
	}
	
	public OrganizerGetScheduleIdResponse (String s, String scheduleId) {
		this.response = s;
		this.httpCode = 200;
		this.scheduleId = scheduleId;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}
}
