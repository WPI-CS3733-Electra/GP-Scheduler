package com.amazonaws.lambda.demo;

public class OrganizerCancelMeetingRequest {
	String id;

	public OrganizerCancelMeetingRequest(String id) {
		this.id = id;
	}
	
	public String toString() {
		return "Delete Schedule by id: "+ id;
	}
	
	
}
