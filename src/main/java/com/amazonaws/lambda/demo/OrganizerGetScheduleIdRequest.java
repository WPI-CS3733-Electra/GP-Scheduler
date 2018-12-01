package com.amazonaws.lambda.demo;

public class OrganizerGetScheduleIdRequest {
	String secretCode;
	
	
	public OrganizerGetScheduleIdRequest(String secretCode) {
		this.secretCode = secretCode;
	}

	public String toString() {
		return "Retrieve schedule by"+ secretCode;
	}
}
