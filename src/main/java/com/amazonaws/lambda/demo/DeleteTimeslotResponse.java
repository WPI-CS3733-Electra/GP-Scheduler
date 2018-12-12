package com.amazonaws.lambda.demo;

public class DeleteTimeslotResponse {
	String response;
	int httpcode;
	
	public DeleteTimeslotResponse(String response, int httpcode) {
		this.response = response;
		this.httpcode = httpcode;
	}
	
	public DeleteTimeslotResponse(String response) {
		this.response = response;
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
	
	
}