package com.amazonaws.lambda.demo;

public class ParticipantGetScheduleIdRequest {
	String releaseCode;

	public ParticipantGetScheduleIdRequest(String releaseCode) {
		this.releaseCode = releaseCode;
	}

	public String toString() {
		return "Retrieve schedule by"+ releaseCode;
	}
	
	
}
