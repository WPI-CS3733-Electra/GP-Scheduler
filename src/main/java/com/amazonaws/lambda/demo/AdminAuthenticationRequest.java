package com.amazonaws.lambda.demo;

public class AdminAuthenticationRequest {
	String secretCode;
	
	
	public AdminAuthenticationRequest(String secretCode) {
		this.secretCode = secretCode;
	}

	public String toString() {
		return "Retrieve schedule by"+ secretCode;
	}
}
