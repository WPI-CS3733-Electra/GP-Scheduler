package com.amazonaws.lambda.demo;

public class ShowWeekScheduleRequest {
	String sId;
	int week;
	
	public ShowWeekScheduleRequest(String sId, int week) {
		this.sId = sId;
		this.week = week;
	}
	
	public String toString() {
		return "Show week" + week;
	}
}
