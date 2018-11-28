package com.amazonaws.lambda.demo;

public class CreateScheduleRequest {
	String name;
	String author;
	String startDate;
	String endDate;
	String startTime;
	String endTime;
	int timePeriod;
		
		public CreateScheduleRequest(String name, String author, String startDate, String endDate, String startTime, String endTime, int timePeriod) {
			this.name = name;
			this.author = author;
			this.startDate = startDate;
			this.endDate = endDate;
			this.startTime = startTime;
			this.endTime = endTime;
			this.timePeriod = timePeriod;
		}
		
		public String toString() {
			return "Create Schedule" + name + "by" + author;
		}

}
