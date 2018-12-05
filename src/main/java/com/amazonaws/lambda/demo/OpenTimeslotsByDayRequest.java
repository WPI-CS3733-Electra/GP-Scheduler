package com.amazonaws.lambda.demo;

public class OpenTimeslotsByDayRequest {
	String dayId;
	String scheduleId;
	
	
	public OpenTimeslotsByDayRequest(String dayId, String scheduleId) {
		this.dayId = dayId;
		this.scheduleId = scheduleId;
	}
	
	public String toString() {
		return "Open Timeslots in day " + dayId;
	}
	
	
	
	
	
	
}
