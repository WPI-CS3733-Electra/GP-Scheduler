package com.amazonaws.db;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.amazonaws.model.Day;
import com.amazonaws.model.Meeting;
import com.amazonaws.model.Schedule;
import com.amazonaws.model.Timeslot;

/**
 * @author Ziqian Zeng
 *
 */
public class TimeslotDAO {

	java.sql.Connection conn;

	DateFormat TS_formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // "2016-09-21 13:43:27"
	DateFormat Time_formatter = new SimpleDateFormat("HH:mm"); // "13:43"

	public TimeslotDAO() {
		try {
			conn = DatabaseUtil.connect();
		} catch (Exception e) {
			conn = null;
		}
	}

	// ---------------------Liter 2---------------------------
	public boolean openTimeslot(Timeslot t) throws Exception {

		if (conn == null) {
			return false;
		}

		try {

			PreparedStatement ps = conn
					.prepareStatement("INSERT INTO Timeslot(timeslotUUID,beginTime,dayUUID) values(?,?,?);");
			ps.setString(1, t.getId());
			ps.setTime(2, new Time(Time_formatter.parse(t.getBeginTime()).getTime()));
			ps.setString(3, t.getDayId());
			ps.execute();

			ps.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to open 1 Timeslot: " + e.getMessage());
		}
	}

	public boolean deleteTimeslot(String tuuid) throws Exception {

		if (conn == null) {
			return false;
		}

		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Timeslot WHERE timeslotUUID=?;");
			ps.setString(1, tuuid);
			ResultSet resultSet = ps.executeQuery();

			// not present?
			if (!resultSet.next()) {
				resultSet.close();
				return false;
			}

			resultSet.close();

			ps = conn.prepareStatement("DELETE FROM Timeslot WHERE timeslotUUID=?;");
			ps.setString(1, tuuid);
			ps.execute();

			ps.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to DELETE 1 Timeslot: " + e.getMessage());
		}
	}

	public Schedule getScheduleTimeInfo(String suuid, String duuid) throws Exception {

		try {
			Schedule schedule = new Schedule();
			ArrayList<Day> dal = new ArrayList<Day>();
			ArrayList<Timeslot> tal = new ArrayList<Timeslot>();
			Day d = new Day();
			
			
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Schedule WHERE scheduleUUID=?;");
			ps.setString(1, suuid);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				schedule.setId(resultSet.getString("scheduleUUID"));
				schedule.setTimePeriod(resultSet.getInt("timePeriod"));
				schedule.setStartTime(resultSet.getTime("startTime").toString().substring(0, 5));
				schedule.setEndTime(resultSet.getTime("endTime").toString().substring(0, 5));
			}
			
			ps = conn.prepareStatement("SELECT * FROM Timeslot WHERE dayUUID=? ORDER BY beginTime;");
			ps.setString(1, duuid);
		    resultSet = ps.executeQuery();
		    
		    while (resultSet.next()) {
		    	Timeslot tempT = new Timeslot();
				tempT.setId(resultSet.getString("timeslotUUID"));
				tempT.setBeginTime(resultSet.getTime("beginTime").toString().substring(0, 5));
				tempT.setDayId(duuid);
				tal.add(tempT);
			}
		    
		    d.setId(duuid);
		    d.setTimeslots(tal);
		    dal.add(d);
		    schedule.setDays(dal);
		    
			resultSet.close();
			ps.close();

			return schedule;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting ScheduleTimeInfo: " + e.getMessage());
		}
	}

	public boolean openTimeslots(ArrayList<Timeslot> tal) throws Exception {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Schedule");

			for (Timeslot t : tal) {
				ps = conn.prepareStatement("INSERT INTO Timeslot(timeslotUUID,beginTime,dayUUID) values(?,?,?);");
				ps.setString(1, t.getId());
				ps.setTime(2, new Time(Time_formatter.parse(t.getBeginTime()).getTime()));
				ps.setString(3, t.getDayId());
				ps.execute();
			}
			ps.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to OPEN Timeslots: " + e.getMessage());
		}
	}

	public boolean deleteTimeslotByDay(String duuid) throws Exception {

		if (conn == null) {
			return false;
		}

		try {
			PreparedStatement ps = conn.prepareStatement("DELETE FROM Timeslot WHERE dayUUID=?;");
			ps.setString(1, duuid);
			ps.execute();

			ps.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to DELETE Timeslot By Day: " + e.getMessage());
		}
	}

	public ArrayList<Day> getScheduleDayInfo(String suuid, String beginTime) throws Exception {

		try {
			ArrayList<Day> dal = new ArrayList<Day>();

			PreparedStatement ps = conn.prepareStatement(
					"SELECT * FROM Day WHERE scheduleUUID=? ORDER BY date;");
			ps.setString(1, suuid);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				Day tempD = new Day();
				tempD.setId(resultSet.getString("dayUUID"));
				dal.add(tempD);
			}
			
			for (int i=0; i<dal.size(); i++) {
				dal.get(i).setTimeslots(retrieveTALByDay(dal.get(i).getId(), beginTime));
				if(!dal.get(i).getTimeslots().isEmpty()) {
					dal.remove(i);
				}
			}

			resultSet.close();
			ps.close();

			return dal;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting ScheduleDayInfo: " + e.getMessage());
		}

	}
	
	public ArrayList<Timeslot> retrieveTALByDay(String duuid, String beginTime) throws Exception {
		try {
			ArrayList<Timeslot> tal = new ArrayList<Timeslot>();

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Timeslot WHERE (dayUUID=?) AND (beginTime=?);");
			ps.setString(1, duuid);
			ps.setTime(2, new Time(Time_formatter.parse(beginTime).getTime()));
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				Timeslot tempT = new Timeslot();
				tempT.setId(resultSet.getString("timeslotUUID"));
				tempT.setBeginTime(resultSet.getTime("beginTime").toString().substring(0, 5));
				tal.add(tempT);
			}

			resultSet.close();
			ps.close();
			
			return tal;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting Timeslot(getScheduleDayInfo): " + e.getMessage());
		}

	}

	public boolean deleteTimeslotByTime(String suuid, String beginTime) throws Exception {

		if (conn == null) {
			return false;
		}

		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Schedule WHERE scheduleUUID=?;");
			ps.setString(1, suuid);
			ResultSet resultSet = ps.executeQuery();

			// not present?
			if (!resultSet.next()) {
				resultSet.close();
				return false;
			}

			resultSet.close();

			ps = conn.prepareStatement(
					"DELETE t FROM Timeslot t INNER JOIN Day d ON t.dayUUID = d.dayUUID INNER JOIN Schedule s ON d.scheduleUUID = s.scheduleUUID WHERE (s.scheduleUUID=?) AND (t.beginTime=?);");
			ps.setString(1, suuid);
			ps.setTime(2, new Time(Time_formatter.parse(beginTime).getTime()));
			ps.execute();

			ps.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to DELETE Timeslot By Time: " + e.getMessage());
		}
	}

}