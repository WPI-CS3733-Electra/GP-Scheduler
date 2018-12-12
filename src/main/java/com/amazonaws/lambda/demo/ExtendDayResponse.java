package com.amazonaws.lambda.demo;

public class ExtendDayResponse {
	String response;
	int httpcode;
	
	public ExtendDayResponse (String s, int code) {
		this.response = s;
		this.httpcode = code;
	}
	
	// 200 means success
	public ExtendDayResponse (String s) {
		this.response = s;
		this.httpcode = 200;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}
}