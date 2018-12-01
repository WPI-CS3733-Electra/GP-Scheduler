package com.amazonaws.lambda.demo;

public class CreateMeetingRequest {
	String partInfo;
	String timeslotId;
	
	public CreateMeetingRequest(String partInfo, String timeslotId) {
		this.partInfo = partInfo;
		this.timeslotId = timeslotId;
	}
	
	public String toString() {
		return "Create Meeting by" + partInfo;
	}
	
	
}
