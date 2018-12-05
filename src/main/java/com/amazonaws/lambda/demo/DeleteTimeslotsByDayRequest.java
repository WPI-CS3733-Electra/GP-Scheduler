package com.amazonaws.lambda.demo;

public class DeleteTimeslotsByDayRequest {
	String dayId;

	public DeleteTimeslotsByDayRequest(String dayId) {
		this.dayId = dayId;
	}
	
	public String toString() {
		return "Delete Timeslots by" + dayId;
	}

}
