package com.amazonaws.db;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.amazonaws.model.Day;
import com.amazonaws.model.Schedule;

public class SchedulerDAO {

	java.sql.Connection conn;

	DateFormat TS_formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // "2016-09-21 13:43:27"
	DateFormat Time_formatter = new SimpleDateFormat("HH:mm"); // "13:43"

	public SchedulerDAO() {
		try {
			conn = DatabaseUtil.connect();
		} catch (Exception e) {
			conn = null;
		}
	}

	public Schedule getSchedule(String suuid) throws Exception {

		try {
			Schedule schedule = new Schedule();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Schedule WHERE scheduleUUID=?;");
			ps.setString(1, suuid);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				schedule.setId(resultSet.getString("scheduleUUID"));
				schedule.setName(resultSet.getString("name"));
				schedule.setAuthor(resultSet.getString("author"));
				schedule.setSecretCode(resultSet.getString("secretCode"));
				schedule.setCreatedDate(resultSet.getDate("createdDate").toString());
				schedule.setTimePeriod(resultSet.getInt("timePeriod"));
				schedule.setStartTime(resultSet.getTime("startTime").toString());
				schedule.setEndTime(resultSet.getTime("endTime").toString());
			}
			resultSet.close();
			ps.close();

			return schedule;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting schedule: " + e.getMessage());
		}
	}

	public boolean addSchedule(Schedule given) throws Exception {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Schedule WHERE scheduleUUID=?;");
			ps.setString(1, given.getId());
			ResultSet resultSet = ps.executeQuery();

			// already present?
			while (resultSet.next()) {
				resultSet.close();
				return false;
			}

			ps = conn.prepareStatement(
					"INSERT INTO Schedule (scheduleUUID,name,author,secretCode,releaseCode,createdDate,timePeriod,startTime,endTime,startDate,endDate) values(?,?,?,?,?,?,?,?,?,?,?);");
			ps.setString(1, given.getId());
			ps.setString(2, given.getName());
			ps.setString(3, given.getAuthor());
			ps.setString(4, given.getSecretCode());
			ps.setString(5, given.getReleaseCode());
			ps.setTimestamp(6, new Timestamp(TS_formatter.parse(given.getCreatedDate()).getTime()));
			ps.setInt(7, given.getTimePeriod());
			ps.setTime(8, new Time(Time_formatter.parse(given.getStartTime()).getTime()));
			ps.setTime(9, new Time(Time_formatter.parse(given.getEndTime()).getTime()));
			ps.setDate(10, java.sql.Date.valueOf(given.getStartDate()));
			ps.setDate(11, java.sql.Date.valueOf(given.getEndDate()));
			ps.execute();
			initAddDayAL(given.getId(), given.getD());
			return true;

		} catch (Exception e) {
			throw new Exception("Failed to insert table Schedule: " + e.getMessage());
		}
	}
	
	public boolean initAddDayAL(String scheduleUUID, ArrayList<Day> dal) throws Exception {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM DayAL WHERE scheduleUUID=?;");
			ps.setString(1, scheduleUUID);
			ResultSet resultSet = ps.executeQuery();

			// already present?
			while (resultSet.next()) {
				resultSet.close();
				return false;
			}

			for(Day d: dal) {
				ps = conn.prepareStatement("INSERT INTO DayAL (scheduleUUID,dayUUID) values(?,?);");
				ps.setString(1, scheduleUUID);
				ps.setString(2, d.getId());
				ps.execute();
			}
			
			return true;

		} catch (Exception e) {
			throw new Exception("Failed to insert table Schedule: " + e.getMessage());
		}
	}

}