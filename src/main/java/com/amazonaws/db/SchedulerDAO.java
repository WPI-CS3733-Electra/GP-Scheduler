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

	public Schedule showWeek(String suuid, int week) throws Exception {

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
				schedule.setCreatedDate(resultSet.getTimestamp("createdDate").toString().substring(0, 19));
				schedule.setTimePeriod(resultSet.getInt("timePeriod"));
				schedule.setStartTime(resultSet.getTime("startTime").toString().substring(0, 5));
				schedule.setEndTime(resultSet.getTime("endTime").toString().substring(0, 5));
				schedule.setStartDate(resultSet.getDate("startDate").toString());
				schedule.setEndDate(resultSet.getDate("endDate").toString());
			}

			int startWeekDay = LocalDate.parse(schedule.getStartDate()).getDayOfWeek().getValue();
			LocalDate localWeekStart = LocalDate.parse(schedule.getStartDate())
					.plusDays(1 + (7 - startWeekDay) + 7 * (week - 2));
			LocalDate localWeekEnd = localWeekStart.plusDays(4);

			resultSet.close();
			ps.close();

			schedule.setD(retrieveDAL(suuid, localWeekStart, localWeekEnd));

			return schedule;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting schedule: " + e.getMessage());
		}
	}

	public boolean checkWeek(String suuid, int week) throws Exception {
		if (week < 1) {
			return false;
		}
		try {
			Schedule schedule = new Schedule();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Schedule WHERE scheduleUUID=?;");
			ps.setString(1, suuid);
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				schedule.setId(suuid);
				schedule.setStartDate(resultSet.getDate("startDate").toString());
				schedule.setEndDate(resultSet.getDate("endDate").toString());
			}

			LocalDate scheduleEndDate = LocalDate.parse(schedule.getEndDate());
			int startWeekDay = LocalDate.parse(schedule.getStartDate()).getDayOfWeek().getValue();
			LocalDate calWeekStart = LocalDate.parse(schedule.getStartDate())
					.plusDays(1 + (7 - startWeekDay) + 7 * (week - 2));

			if (calWeekStart.compareTo(scheduleEndDate) > 0) {
				return false;
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting schedule: " + e.getMessage());
		}

	}

	// Retrieve an ArrayList<Day> for 5 Days.
	public ArrayList<Day> retrieveDAL(String suuid, LocalDate startDate, LocalDate endDate) throws Exception {

		try {
			ArrayList<Day> dal = new ArrayList<Day>();

			PreparedStatement ps = conn.prepareStatement(
					"SELECT DISTINCT d.ScheduleUUID, dayUUID, date FROM Day d INNER JOIN Schedule s ON d.ScheduleUUID = s.ScheduleUUID WHERE (d.scheduleUUID=?) AND (d.date>=?) AND (d.date<=?) ORDER BY date;");
			ps.setString(1, suuid);
			ps.setDate(2, java.sql.Date.valueOf(startDate));
			ps.setDate(3, java.sql.Date.valueOf(endDate));
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				Day tempD = new Day();
				tempD.setId(resultSet.getString("dayUUID"));
				tempD.setDate(resultSet.getDate("date").toString());
				tempD.setsId(suuid);
				dal.add(tempD);
			}

			resultSet.close();
			ps.close();

			for (Day d : dal) {
				d.setT(retrieveTAL(d.getId()));
			}
			return dal;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting Day: " + e.getMessage());
		}

	}

	public ArrayList<Timeslot> retrieveTAL(String duuid) throws Exception {
		try {
			ArrayList<Timeslot> tal = new ArrayList<Timeslot>();

			PreparedStatement ps = conn.prepareStatement(
					"SELECT DISTINCT t.dayUUID, timeslotUUID, beginTime FROM Timeslot t INNER JOIN Day d ON t.dayUUID = d.dayUUID WHERE t.dayUUID=? ORDER BY beginTime;");
			ps.setString(1, duuid);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				Timeslot tempT = new Timeslot();
				tempT.setId(resultSet.getString("timeslotUUID"));
				tempT.setBeginTime(resultSet.getTime("beginTime").toString().substring(0, 5));
				tempT.setdId(duuid);
				tal.add(tempT);
			}

			resultSet.close();
			ps.close();

			for (Timeslot t : tal) {
				t.setM(retrieveMeeting(t.getId()));
			}
			return tal;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting Timeslot: " + e.getMessage());
		}

	}

	public Meeting retrieveMeeting(String tuuid) throws Exception {
		try {
			Meeting m = new Meeting();

			PreparedStatement ps = conn.prepareStatement(
					"SELECT DISTINCT m.timeslotUUID, meetingUUID, partInfo, secretCode FROM Meeting m INNER JOIN Timeslot t ON m.timeslotUUID = t.timeslotUUID WHERE m.timeslotUUID=?;");
			ps.setString(1, tuuid);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				m.setId(resultSet.getString("meetingUUID"));
				m.setPartInfo(resultSet.getString("partInfo"));
				m.setSecretCode(resultSet.getString("secretCode"));
				m.setId(tuuid);
			}

			resultSet.close();
			ps.close();
			return m;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting Timeslot: " + e.getMessage());
		}

	}

	// ---------------------ADD DATA TO DB---------------------------

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
					"INSERT INTO Schedule(scheduleUUID,name,author,secretCode,releaseCode,createdDate,timePeriod,startTime,endTime,startDate,endDate) values(?,?,?,?,?,?,?,?,?,?,?);");
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
			ps.close();

			addDayfromAL(given.getId(), given.getD());

			return true;

		} catch (Exception e) {
			throw new Exception("Failed to insert table Schedule: " + e.getMessage());
		}
	}

	public boolean addDayfromAL(String scheduleUUID, ArrayList<Day> dal) throws Exception {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Schedule");

			for (Day d : dal) {
				ps = conn.prepareStatement("INSERT INTO Day(dayUUID,date,scheduleUUID) values(?,?,?);");
				ps.setString(1, d.getId());
				ps.setDate(2, java.sql.Date.valueOf(d.getDate()));
				ps.setString(3, scheduleUUID);
				ps.execute();

				addTimeslotfromAL(d.getId(), d.getT());
			}
			ps.close();
			return true;

		} catch (Exception e) {
			throw new Exception("Failed to insert table Day: " + e.getMessage());
		}
	}

	public boolean addTimeslotfromAL(String dayUUID, ArrayList<Timeslot> tal) throws Exception {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Schedule");

			for (Timeslot t : tal) {
				ps = conn.prepareStatement("INSERT INTO Timeslot(timeslotUUID,beginTime,dayUUID) values(?,?,?);");
				ps.setString(1, t.getId());
				ps.setTime(2, new Time(Time_formatter.parse(t.getBeginTime()).getTime()));
				ps.setString(3, dayUUID);
				ps.execute();
				if (t.getM() != null) {
					addMeeting(t.getId(), t.getM());
				}

			}
			ps.close();
			return true;

		} catch (Exception e) {
			throw new Exception("Failed to insert table Timeslot: " + e.getMessage());
		}
	}

	public boolean addMeeting(String timeslotUUID, Meeting m) throws Exception {
		try {
			PreparedStatement ps = conn.prepareStatement(
					"INSERT INTO Meeting(meetingUUID,partInfo,timeslotUUID,secretCode) values(?,?,?,?);");
			ps.setString(1, m.getId());
			ps.setString(2, m.getPartInfo());
			ps.setString(3, timeslotUUID);
			ps.setString(4, m.getSecretCode());
			ps.execute();

			ps.close();
			return true;

		} catch (Exception e) {
			throw new Exception("Failed to insert table Meeting: " + e.getMessage());
		}
	}

}