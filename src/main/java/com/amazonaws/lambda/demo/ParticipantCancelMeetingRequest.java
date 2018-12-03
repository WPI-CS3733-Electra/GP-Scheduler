package com.amazonaws.lambda.demo;

public class ParticipantCancelMeetingRequest {
	String id;
	String secretCode;
	
	
	public ParticipantCancelMeetingRequest(String id, String secretCode) {
		this.id = id;
		this.secretCode = secretCode;
	}


	public String toString() {
		return "Delete Schedule by id: "+ id + "with SecretCode: " + secretCode;
	}
}
