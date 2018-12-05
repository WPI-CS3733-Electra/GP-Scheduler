package com.amazonaws.lambda.demo;

public class OpenTimeslotRequest {
	String dayId;
	String beginTime;
	
	public OpenTimeslotRequest(String dayId, String beginTime) {
		this.dayId = dayId;
		this.beginTime = beginTime;
	}
	
	public String toString() {
		return "Create Meeting by" + dayId + "at" + beginTime;
	}
}
