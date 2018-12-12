package com.amazonaws.lambda.demo;

public class ExtendDayRequest {
	String scheduleId;
	String startDate;
	String endDate;
	
	
	public ExtendDayRequest(String scheduleId, String startDate, String endDate) {
		this.scheduleId = scheduleId;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public String toString() {
		return "Extend schedule: " +scheduleId + "by " + startDate + "and" + endDate;
	}
}
