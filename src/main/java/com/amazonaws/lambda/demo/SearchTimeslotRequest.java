package com.amazonaws.lambda.demo;

public class SearchTimeslotRequest {
	String scheduleId;
	int year;
	int month;
	int dayOfWeek;
	int dayOfMonth;
	String beginTime;
	String endTime;
	
	


	public SearchTimeslotRequest(String scheduleId,int year, int month, int dayOfWeek, int dayOfMonth, String beginTime, String endTime) {
		this.scheduleId = scheduleId;
		this.year = year;
		this.month = month;
		this.dayOfWeek = dayOfWeek;
		this.dayOfMonth = dayOfMonth;
		this.beginTime = beginTime;
		this.endTime = endTime;
	}




	public String toString() {
		return "Search Timeslot by year: "+year + "by month: " + month 
				+ "by Day of Month" + dayOfMonth + "by Day of Week" + dayOfWeek 
				+ "with start time: " +beginTime + "end time: "+ endTime;
	} 
	

}
