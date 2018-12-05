package com.amazonaws.lambda.demo;

public class ParticipantGetScheduleIdResponse {
	String response;
	int httpcode;
	String scheduleId;
	
	public ParticipantGetScheduleIdResponse (String s, int code) {
		this.response = s;
		this.httpcode = code;
	}
	
	public ParticipantGetScheduleIdResponse (String s, String scheduleId) {
		this.response = s;
		this.httpcode = 200;
		this.scheduleId = scheduleId;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}
}
