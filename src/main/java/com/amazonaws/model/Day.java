package com.amazonaws.model;

import java.sql.Date;
import java.util.ArrayList;

public class Day {
	String id;
	Date date;
	ArrayList<Timeslot> t;
	String sId;
	
	public Day(String id, Date date, ArrayList<Timeslot> t, String sId) {
		this.id = id;
		this.date = date;
		this.t = t;
		this.sId = sId;
	}
}
