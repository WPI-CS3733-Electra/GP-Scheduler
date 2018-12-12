package com.amazonaws.model;

public class BriefScheduleInfo {

	String sId;
	String name;
	String author;
	String createdDate;
	String startDate;
	String endDate;
	int meetings;
	int timeslots;

	public BriefScheduleInfo() {
	}

	public BriefScheduleInfo(String sId, String name, String author, String createdDate, String startDate,
			String endDate, int meetings, int timeslots) {
		super();
		this.sId = sId;
		this.name = name;
		this.author = author;
		this.createdDate = createdDate;
		this.startDate = startDate;
		this.endDate = endDate;
		this.meetings = meetings;
		this.timeslots = timeslots;
	}

	public String getsId() {
		return sId;
	}

	public void setsId(String sId) {
		this.sId = sId;
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

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public int getMeetings() {
		return meetings;
	}

	public void setMeetings(int meetings) {
		this.meetings = meetings;
	}

	public int getTimeslots() {
		return timeslots;
	}

	public void setTimeslots(int timeslots) {
		this.timeslots = timeslots;
	}
	
	

}
