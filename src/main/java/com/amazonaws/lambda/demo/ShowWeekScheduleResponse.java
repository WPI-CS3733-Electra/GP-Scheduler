package com.amazonaws.lambda.demo;

import com.amazonaws.model.Schedule;

public class ShowWeekScheduleResponse {
	String response;
	int httpcode;
	Schedule schedule;
	
	public ShowWeekScheduleResponse (String s, int code) {
		this.response = s;
		this.httpcode = code;
	}
	
	// 200 means success
	public ShowWeekScheduleResponse (String s, Schedule schedule) {
		this.response = s;
		this.schedule = schedule;
		this.httpcode = 200;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}

	public String getResponse() {
		return response;
	}

	public int getHttpcode() {
		return httpcode;
	}

	public Schedule getSchedule() {
		return schedule;
	}
	
	
}
