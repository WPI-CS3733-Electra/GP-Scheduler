package model;

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
}
