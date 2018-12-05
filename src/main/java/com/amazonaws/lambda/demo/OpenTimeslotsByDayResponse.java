package com.amazonaws.lambda.demo;

public class OpenTimeslotsByDayResponse {
	String response;
	int httpcode;
	
	public OpenTimeslotsByDayResponse(String response, int httpcode) {
		this.response = response;
		this.httpcode = httpcode;
	}
	
	public OpenTimeslotsByDayResponse(String response) {
		this.response = response;
		this.httpcode = 200;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}
}
