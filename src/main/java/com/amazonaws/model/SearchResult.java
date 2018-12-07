package com.amazonaws.model;

public class SearchResult {
	
	String date;
	String beginTime;
	String endTime;
	String timeslotId;
	
	public SearchResult(String date, String beginTime, String endTime, String timeslotId) {
		this.date = date;
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.timeslotId = timeslotId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getTimeslotId() {
		return timeslotId;
	}

	public void setTimeslotId(String timeslotId) {
		this.timeslotId = timeslotId;
	}
	
	

}
