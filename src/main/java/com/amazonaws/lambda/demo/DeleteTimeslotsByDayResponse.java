package com.amazonaws.lambda.demo;

public class DeleteTimeslotsByDayResponse {
	String response;
	int httpcode;
	
	public DeleteTimeslotsByDayResponse (String s, int code) {
		this.response = s;
		this.httpcode = code;
	}
	
	// 200 means success
	public DeleteTimeslotsByDayResponse (String s) {
		this.response = s;
		this.httpcode = 200;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}
}
