package com.amazonaws.model;

import java.sql.Time;

public class Timeslot {
	String id;
	Time beginTime;
	String dId;
	 
	public Timeslot(String id, Time beginTime, String dId) {
		this.id = id;
		this.beginTime = beginTime;
		this.dId = dId;
	}
	
}
