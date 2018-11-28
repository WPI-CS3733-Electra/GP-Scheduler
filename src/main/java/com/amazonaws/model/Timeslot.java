package com.amazonaws.model;

import java.sql.Time;

import org.joda.time.LocalTime;

public class Timeslot {
	String id;
	LocalTime beginTime;
	String dId;
	Meeting m;
	 
	public Timeslot(String id, LocalTime beginTime, String dId, Meeting m) {
		this.id = id;
		this.beginTime = beginTime;
		this.dId = dId;
		this.m = m;
	}
	
}
