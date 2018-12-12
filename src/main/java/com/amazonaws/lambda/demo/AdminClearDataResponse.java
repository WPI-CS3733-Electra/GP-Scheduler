package com.amazonaws.lambda.demo;

public class AdminClearDataResponse {
	int number;
	String response;
	int httpcode;
	
	public AdminClearDataResponse (String s, int code) {
		this.response = s;
		this.httpcode = code;
	}
	
	public AdminClearDataResponse (String s, int code, int number) {
		this.response = s;
		this.httpcode = code;
		this.number = number;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}

	

}
