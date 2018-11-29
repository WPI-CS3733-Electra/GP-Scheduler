package com.amazonaws.model;

import java.util.ArrayList;

public class Day {

	String id;
	String date;
	ArrayList<Timeslot> timeslots;
	String scheduleId;

	public Day() {
	}

	public Day(String id, String date, ArrayList<Timeslot> timeslots, String scheduleId) {
		this.id = id;
		this.date = date;
		this.timeslots = timeslots;
		this.scheduleId = scheduleId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public ArrayList<Timeslot> getTimeslots() {
		return timeslots;
	}

	public void setTimeslots(ArrayList<Timeslot> timeslots) {
		this.timeslots = timeslots;
	}

	public String getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}

}
