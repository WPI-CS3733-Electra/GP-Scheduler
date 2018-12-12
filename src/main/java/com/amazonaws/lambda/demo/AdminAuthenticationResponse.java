package com.amazonaws.lambda.demo;

public class AdminAuthenticationResponse {
	String response;
	int httpcode;
	
	public AdminAuthenticationResponse(String response, int httpcode) {
		this.response = response;
		this.httpcode = httpcode;
	}
	
	public AdminAuthenticationResponse(String response) {
		this.response = response;
		this.httpcode = 200;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}
}
