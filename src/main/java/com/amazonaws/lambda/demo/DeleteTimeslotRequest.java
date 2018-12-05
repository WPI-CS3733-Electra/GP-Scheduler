package com.amazonaws.lambda.demo;

public class DeleteTimeslotRequest {

	String id;

	public DeleteTimeslotRequest(String id) {
		this.id = id;
	}
	
	public String toString() {
		return "Close Timeslot by id: "+ id;
	}
}
