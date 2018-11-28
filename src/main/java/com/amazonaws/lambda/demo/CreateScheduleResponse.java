package com.amazonaws.lambda.demo;

public class CreateScheduleResponse {
	String response;
	int httpCode;
	String secretCode;
	String releaseCode;
	
	public CreateScheduleResponse (String s, int code) {
		this.response = s;
		this.httpCode = code;
	}
	
	// 200 means success
	public CreateScheduleResponse (String s, String sc, String rc) {
		this.response = s;
		this.secretCode = sc;
		this.releaseCode = rc;
		this.httpCode = 200;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}
}
