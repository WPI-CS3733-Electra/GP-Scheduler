package com.amazonaws.lambda.demo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.amazonaws.db.SchedulerDAO;
import com.amazonaws.model.Day;
import com.amazonaws.model.Schedule;
import com.amazonaws.model.Timeslot;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class CreateScheduleHandler {
	public LambdaLogger logger = null;

	/** Load from RDS, if it exists
	 * 
	 * @throws Exception 
	 */
	boolean createSchedule(String name, String author, String startDate, String endDate, String startTime, String endTime, int timePeriod) throws Exception {
		if (logger != null) { logger.log("in createSchedule"); }
		SchedulerDAO dao = new SchedulerDAO();
		String sId = this.genUUIDString();
		String sCode = this.genCode(name + author);
		String rCode = this.genCode(author + startDate + endDate + timePeriod );
		int numOfDays = this.daysBetweenDates(startDate, endDate);
		int numOfMins = this.minutesBetweenTimes(startTime, endTime);
		int numOfTs = numOfMins / timePeriod;
		ArrayList<Day> days = new ArrayList<Day>();
		
		
		for(int i = 0; i < numOfDays; i++) {
			String dId = this.genUUIDString();
			ArrayList<Timeslot> ts = new ArrayList<Timeslot>();
			for(int j = 0; j < numOfTs; j ++) {
				
				Timeslot t = new Timeslot()
			}
			
			
			
		}
		
		// check if present
		Schedule exist = dao.getSchedule(sId);
		Schedule Schedule = new Schedule (sId, name, author, sCode, rCode,);
		if (exist == null) {
			return dao.addConstant(constant);
		} else {
			return dao.updateConstant(constant);
		}
	}
	
	String genUUIDString() {
		UUID u = UUID.randomUUID();
		String s = u.toString();
		return s;
	}
	
	String genCode(String s) {
		UUID u = UUID.fromString(s);
		String i = u.toString();
		String f = i.substring(0, 5);
		return f;
	}
	
	int daysBetweenDates(String s1, String s2) throws ParseException{
			Date d1 = new SimpleDateFormat("dd/MM/yyyy").parse(s1);
			Date d2 = new SimpleDateFormat("dd/MM/yyyy").parse(s2);
			return Days.daysBetween(
	                new LocalDate(d1.getTime()), 
	                new LocalDate(d2.getTime())).getDays();

		 }
	
	int minutesBetweenTimes(String s1, String s2) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Date date1 = format.parse(s1);
		Date date2 = format.parse(s2);
		long difference = (date2.getTime() - date1.getTime())/60000;
		return (int)difference;
	}
	
	Date stringToTime(String s) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.parse(s);
	}
	
	LocalTime calBeginTime(String startTime, int timePeriod, int index) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
        LocalTime time = formatter.parseLocalTime(startTime);
        time = time.plusMinutes(index * timePeriod);
        return time;	
	}
	
}
