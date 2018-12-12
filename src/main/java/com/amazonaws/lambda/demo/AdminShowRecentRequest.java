package com.amazonaws.lambda.demo;

public class AdminShowRecentRequest {
	
	String code;
	int hours;
	
	public AdminShowRecentRequest(String code, int hours) {
		this.code = code;
		this.hours = hours;
	}
	
	public String toString() {
		return "show recent created Schedule in "+ hours + "hour(s).";
	}
	

}
