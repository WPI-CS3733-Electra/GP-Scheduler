package com.amazonaws.model;

import java.util.ArrayList;

import org.joda.time.LocalDate;

public class Day {
	String id;
	LocalDate date;
	ArrayList<Timeslot> t;
	String sId;
	
	public Day(String id, LocalDate date, ArrayList<Timeslot> t, String sId) {
		this.id = id;
		this.date = date;
		this.t = t;
		this.sId = sId;
	}
}
