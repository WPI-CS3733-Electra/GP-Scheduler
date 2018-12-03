package com.amazonaws.lambda.demo;

public class DeleteScheduleResponse {
	String response;
	int httpcode;
	
	public DeleteScheduleResponse(String response, int httpcode) {
		this.response = response;
		this.httpcode = httpcode;
	}
	
	public DeleteScheduleResponse(String response) {
		this.response = response;
		this.httpcode = 200;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}
}
