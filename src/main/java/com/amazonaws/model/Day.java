package com.amazonaws.model;

import java.util.ArrayList;

public class Day {

	String id;
	String date;
	ArrayList<Timeslot> t;
	String sId;

	public Day(String id, String date, ArrayList<Timeslot> t, String sId) {
		this.id = id;
		this.date = date;
		this.t = t;
		this.sId = sId;
	}
	
	public Day() {
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

	public ArrayList<Timeslot> getT() {
		return t;
	}

	public void setT(ArrayList<Timeslot> t) {
		this.t = t;
	}

	public String getsId() {
		return sId;
	}

	public void setsId(String sId) {
		this.sId = sId;
	}

}
