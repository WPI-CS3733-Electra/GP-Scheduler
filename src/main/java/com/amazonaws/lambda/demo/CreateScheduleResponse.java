package com.amazonaws.lambda.demo;

public class CreateScheduleResponse {
	String response;
	int httpcode;
	String secretCode;
	String releaseCode;
	String scheduleId;
	
	public CreateScheduleResponse (String s, int code) {
		this.response = s;
		this.httpcode = code;
	}
	
	// 200 means success
	public CreateScheduleResponse (String s, String sc, String rc, String id) {
		this.response = s;
		this.secretCode = sc;
		this.releaseCode = rc;
		this.scheduleId = id;
		this.httpcode = 200;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}
}
