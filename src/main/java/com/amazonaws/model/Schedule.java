package com.amazownaws.model;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

public class Schedule {
	String id;
	String name;
	String author;
	String secretCode;
	String releaseCode;
	ArrayList<Day> d;
	Date createdDate;
	int timePeriod;
	Time startTime;
	Time endTime;
	
	public Schedule(String id, String name, String author, String secretCode, String releaseCode,
			ArrayList<Day> d, Date createdDate, int timePeriod, Time startTime, Time endTime) {
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
