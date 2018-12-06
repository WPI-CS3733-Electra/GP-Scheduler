package com.amazonaws.lambda.demo;

public class DeleteTimeslotsByTimeResponse {
	String response;
	int httpcode;
	
	public DeleteTimeslotsByTimeResponse(String response, int httpcode) {
		this.response = response;
		this.httpcode = httpcode;
	}
	
	public DeleteTimeslotsByTimeResponse(String response) {
		this.response = response;
		this.httpcode = 200;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}
}
