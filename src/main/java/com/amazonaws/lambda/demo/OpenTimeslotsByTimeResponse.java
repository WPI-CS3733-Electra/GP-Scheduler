package com.amazonaws.lambda.demo;

public class OpenTimeslotsByTimeResponse {
	
	String response;
	int httpcode;
	
	public OpenTimeslotsByTimeResponse(String response, int httpcode) {
		this.response = response;
		this.httpcode = httpcode;
	}
	
	public OpenTimeslotsByTimeResponse(String response) {
		this.response = response;
		this.httpcode = 200;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}
	
}

