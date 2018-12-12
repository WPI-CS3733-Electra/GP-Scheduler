package com.amazonaws.lambda.demo;

public class CreateMeetingResponse {
	String response;
	int httpcode;
	String secretCode;
	
	public CreateMeetingResponse (String s, int code) {
		this.response = s;
		this.httpcode = code;
	}
	
	// 200 means success
	public CreateMeetingResponse (String s, String sc) {
		this.response = s;
		this.secretCode = sc;
		this.httpcode = 200;
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

	public String getSecretCode() {
		return secretCode;
	}
	
}
