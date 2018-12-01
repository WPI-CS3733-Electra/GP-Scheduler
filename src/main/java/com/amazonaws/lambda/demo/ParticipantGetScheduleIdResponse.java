package com.amazonaws.lambda.demo;

public class ParticipantGetScheduleIdResponse {
	String response;
	int httpCode;
	String scheduleId;
	
	public ParticipantGetScheduleIdResponse (String s, int code) {
		this.response = s;
		this.httpCode = code;
	}
	
	public ParticipantGetScheduleIdResponse (String s, String scheduleId) {
		this.response = s;
		this.httpCode = 200;
		this.scheduleId = scheduleId;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}
}
