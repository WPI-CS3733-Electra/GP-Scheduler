package com.amazonaws.lambda.demo;

public class AdminClearDataRequest {
	
	String code;
	int days;
	
	
	public AdminClearDataRequest(String code, int days) {
		this.code = code;
		this.days = days;
	}
	
	public String toString() {
		return "Clear data before: "+ days + "days.";
	}

	

}
