package com.amazonaws.model;


import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class Schedule {
	String id;
	String name;
	String author;
	String secretCode;
	String releaseCode;
	ArrayList<Day> d;
	String createdDate;
	int timePeriod;
	String startTime;
	String endTime;
	
	public Schedule(String id, String name, String author, String secretCode, String releaseCode,
			ArrayList<Day> d, String createdDate, int timePeriod, String startTime, String endTime) {
		this.id = id;
		this.name = name;
		this.author = author;
		this.secretCode = secretCode;
		this.releaseCode = releaseCode;
		this.d = d;
		this.createdDate = createdDate;
		this.timePeriod = timePeriod;
		this.startTime = startTime;
		this.endTime = endTime;
	}
}