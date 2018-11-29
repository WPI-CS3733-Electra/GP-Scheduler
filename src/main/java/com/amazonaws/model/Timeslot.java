package com.amazonaws.model;

import java.sql.Time;

import org.joda.time.LocalTime;

public class Timeslot {
	String id;
	String beginTime;
	String dId;
	Meeting m;

	public Timeslot(String id, String beginTime, String dId, Meeting m) {
		this.id = id;
		this.beginTime = beginTime;
		this.dId = dId;
		this.m = m;
	}

	public Timeslot() {
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

	public String getdId() {
		return dId;
	}

	public void setdId(String dId) {
		this.dId = dId;
	}

	public Meeting getM() {
		return m;
	}

	public void setM(Meeting m) {
		this.m = m;
	}

}
