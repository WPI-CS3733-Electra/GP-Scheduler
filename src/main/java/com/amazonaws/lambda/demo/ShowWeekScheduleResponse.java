package com.amazonaws.lambda.demo;

import com.amazonaws.model.Schedule;

public class ShowWeekScheduleResponse {
	String response;
	int httpCode;
	Schedule schedule;
	
	public ShowWeekScheduleResponse (String s, int code) {
		this.response = s;
		this.httpCode = code;
	}
	
	// 200 means success
	public ShowWeekScheduleResponse (String s, Schedule schedule) {
		this.response = s;
		this.schedule = schedule;
		this.httpCode = 200;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}
}
