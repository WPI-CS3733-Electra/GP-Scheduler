package com.amazonaws.model;

public class BriefScheduleInfo {

	String sId;
	String sName;
	String author;
	String createdDate;
	String startDate;
	String endDate;
	int totalMeetings;
	int totalTimeslots;

	public BriefScheduleInfo() {
	}

	public BriefScheduleInfo(String sId, String sName, String author, String createdDate, String startDate,
			String endDate, int totalMeetings, int totalTimeslots) {
		this.sId = sId;
		this.sName = sName;
		this.author = author;
		this.createdDate = createdDate;
		this.startDate = startDate;
		this.endDate = endDate;
		this.totalMeetings = totalMeetings;
		this.totalTimeslots = totalTimeslots;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getsId() {
		return sId;
	}

	public void setsId(String sId) {
		this.sId = sId;
	}

	public String getsName() {
		return sName;
	}

	public void setsName(String sName) {
		this.sName = sName;
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

	public int getTotalMeetings() {
		return totalMeetings;
	}

	public void setTotalMeetings(int totalMeetings) {
		this.totalMeetings = totalMeetings;
	}

	public int getTotalTimeslots() {
		return totalTimeslots;
	}

	public void setTotalTimeslots(int totalTimeslots) {
		this.totalTimeslots = totalTimeslots;
	}

}
