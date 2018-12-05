package com.amazonaws.lambda.demo;

public class OpenTimeslotResponse {
	String response;
	int httpcode;
	
	public OpenTimeslotResponse(String response, int httpcode) {
		this.response = response;
		this.httpcode = httpcode;
	}
	
	public OpenTimeslotResponse(String response) {
		this.response = response;
		this.httpcode = 200;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}
	
}
