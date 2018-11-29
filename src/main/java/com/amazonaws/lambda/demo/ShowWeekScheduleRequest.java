package com.amazonaws.lambda.demo;

public class ShowWeekScheduleRequest {
	String id;
	int week;
	
	public ShowWeekScheduleRequest(String id, int week) {
		this.id = id;
		this.week = week;
	}
	
	public String toString() {
		return "Show week" + week;
	}
}
