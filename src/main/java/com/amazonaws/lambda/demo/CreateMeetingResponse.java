package com.amazonaws.lambda.demo;

public class CreateMeetingResponse {
	String response;
	int httpCode;
	String secretCode;
	
	public CreateMeetingResponse (String s, int code) {
		this.response = s;
		this.httpCode = code;
	}
	
	// 200 means success
	public CreateMeetingResponse (String s, String sc) {
		this.response = s;
		this.secretCode = sc;
		this.httpCode = 200;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}
}
