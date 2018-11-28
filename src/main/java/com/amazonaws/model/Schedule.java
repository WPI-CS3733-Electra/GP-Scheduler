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
	String startDate;
	String endDate;
	
	public Schedule() {}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getSecretCode() {
		return secretCode;
	}

	public void setSecretCode(String secretCode) {
		this.secretCode = secretCode;
	}

	public String getReleaseCode() {
		return releaseCode;
	}

	public void setReleaseCode(String releaseCode) {
		this.releaseCode = releaseCode;
	}

	public ArrayList<Day> getD() {
		return d;
	}

	public void setD(ArrayList<Day> d) {
		this.d = d;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public int getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(int timePeriod) {
		this.timePeriod = timePeriod;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Schedule(String id, String name, String author, String secretCode, String releaseCode,
			ArrayList<Day> d, String createdDate, int timePeriod, String startTime, String endTime, String startDate, String endDate) {
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
		this.startDate = startDate;
		this.endDate = endDate;
	}
}
