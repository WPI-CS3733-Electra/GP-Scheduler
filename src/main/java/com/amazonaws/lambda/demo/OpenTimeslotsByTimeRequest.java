package com.amazonaws.lambda.demo;

public class OpenTimeslotsByTimeRequest {
	String scheduleId;
	String beginTime;
	
	public OpenTimeslotsByTimeRequest(String scheduleId, String beginTime) {
		this.scheduleId = scheduleId;
		this.beginTime = beginTime;
	}
	
	public String toString() {
		return "Open Timeslots by" + scheduleId + "at" + beginTime;
	}
	
	
}
