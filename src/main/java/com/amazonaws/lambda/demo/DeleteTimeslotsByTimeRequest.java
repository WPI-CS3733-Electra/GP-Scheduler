package com.amazonaws.lambda.demo;

public class DeleteTimeslotsByTimeRequest {
	String scheduleId;
	String beginTime;
	
	
	public DeleteTimeslotsByTimeRequest(String scheduleId, String beginTime) {
		this.scheduleId = scheduleId;
		this.beginTime = beginTime;
	}
	
	public String toString() {
		return "Close Timeslots by" + scheduleId + "at" + beginTime;
	}
}
