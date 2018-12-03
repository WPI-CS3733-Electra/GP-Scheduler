package com.amazonaws.lambda.demo;

public class DeleteScheduleRequest {
	String id;

	public DeleteScheduleRequest(String id) {
		this.id = id;
	}
	
	public String toString() {
		return "Delete Schedule by id: "+ id;
	}
	
}
