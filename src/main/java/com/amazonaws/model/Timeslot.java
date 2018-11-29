package com.amazonaws.model;

import java.sql.Time;

import org.joda.time.LocalTime;

public class Timeslot {
	String id;
	String beginTime;
	Meeting meeting;
	String dayId;

	public Timeslot() {
	}

	public Timeslot(String id, String beginTime, Meeting meeting, String dayId) {
		this.id = id;
		this.beginTime = beginTime;
		this.meeting = meeting;
		this.dayId = dayId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public Meeting getMeeting() {
		return meeting;
	}

	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}

	public String getDayId() {
		return dayId;
	}

	public void setDayId(String dayId) {
		this.dayId = dayId;
	}
	
	

}
