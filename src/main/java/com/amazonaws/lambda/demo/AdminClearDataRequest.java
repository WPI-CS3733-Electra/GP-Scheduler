package com.amazonaws.lambda.demo;

public class AdminClearDataRequest {
	
	String code;
	int days;
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getDays() {
		return days;
	}
	public void setDays(int days) {
		this.days = days;
	}
	

}
