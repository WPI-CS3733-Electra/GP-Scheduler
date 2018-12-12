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

	/**
	 * @param suuid
	 * @param week
	 * @return Schedule Object with ArrayList<Day> contains 5 Days
	 * @throws Exception
	 */
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
				// schedule.setSecretCode(resultSet.getString("secretCode"));
				schedule.setReleaseCode(resultSet.getString("releaseCode"));
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

			schedule.setDays(retrieveDALBySchedule(suuid, localWeekStart, localWeekEnd));

			return schedule;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting schedule: " + e.getMessage());
		}
	}

	public int getStartDayOfWeek(String suuid) throws Exception {

		try {
			int startDay = 0;
			PreparedStatement ps = conn
					.prepareStatement("SELECT DISTINCT scheduleUUID, startDate FROM Schedule WHERE scheduleUUID=?;");
			ps.setString(1, suuid);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				startDay = LocalDate.parse(resultSet.getString("startDate")).getDayOfWeek().getValue();
			}

			resultSet.close();
			ps.close();

			return startDay;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed ON getStartDayOfWeek: " + e.getMessage());
		}
	}

	/**
	 * @param suuid
	 * @param week
	 * @return boolean identifies if the week number is valid
	 * @throws Exception
	 */
	public boolean checkWeek(String suuid, int week) throws Exception {
		if (week < 1) {
			return false;
		}
		try {
			Schedule schedule = new Schedule();
			boolean hasSchedule = false;
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Schedule WHERE scheduleUUID=?;");
			ps.setString(1, suuid);
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				schedule.setId(suuid);
				schedule.setStartDate(resultSet.getDate("startDate").toString());
				schedule.setEndDate(resultSet.getDate("endDate").toString());
				hasSchedule = true;
			}

			if (!hasSchedule) {
				return false;
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
	/**
	 * @param suuid
	 * @param startDate
	 * @param endDate
	 * @return ArrayList<Day> contains 5 Days
	 * @throws Exception
	 */
	public ArrayList<Day> retrieveDALBySchedule(String suuid, LocalDate startDate, LocalDate endDate) throws Exception {

		try {
			ArrayList<Day> dal = new ArrayList<Day>();

			PreparedStatement ps = conn.prepareStatement(
					"SELECT * FROM Day WHERE (scheduleUUID=?) AND (date>=?) AND (date<=?) ORDER BY date;");
			ps.setString(1, suuid);
			ps.setDate(2, java.sql.Date.valueOf(startDate));
			ps.setDate(3, java.sql.Date.valueOf(endDate));
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				Day tempD = new Day();
				tempD.setId(resultSet.getString("dayUUID"));
				tempD.setDate(resultSet.getDate("date").toString());
				tempD.setScheduleId(suuid);
				dal.add(tempD);
			}

			resultSet.close();
			ps.close();

			for (Day d : dal) {
				d.setTimeslots(retrieveTALByDay(d.getId()));
			}

			int dalSize = dal.size();
			if (dalSize != 5 && dalSize != 0) {
				LocalDate listStartDate = LocalDate.parse(dal.get(0).getDate());
				int startDayofWeek = listStartDate.getDayOfWeek().getValue();
				LocalDate listEndDate = LocalDate.parse(dal.get(dal.size() - 1).getDate());
				int endDayofWeek = listEndDate.getDayOfWeek().getValue();

				if (listStartDate.compareTo(startDate) > 0) {
					for (int i = 1; i <= (startDayofWeek - 1); i++) {
						Day tempD = new Day();
						tempD.setDate(listStartDate.minusDays(i).toString());
						dal.add(0, tempD);
					}
				}
				if (listEndDate.compareTo(endDate) < 0) {
					for (int i = 1; i <= (5 - endDayofWeek); i++) {
						Day tempD = new Day();
						tempD.setDate(listEndDate.plusDays(i).toString());
						dal.add(tempD);
					}
				}

			}

			if (dalSize == 0) {
				for (int i = 1; i <= 5; i++) {
					Day tempD = new Day();
					tempD.setDate(startDate.minusDays(i).toString());
					dal.add(0, tempD);
				}
			}

			return dal;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting Day: " + e.getMessage());
		}

	}

	/**
	 * @param duuid
	 * @return ArrayList<Timeslot>
	 * @throws Exception
	 */
	public ArrayList<Timeslot> retrieveTALByDay(String duuid) throws Exception {
		try {
			ArrayList<Timeslot> tal = new ArrayList<Timeslot>();

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Timeslot WHERE dayUUID=? ORDER BY beginTime;");
			ps.setString(1, duuid);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				Timeslot tempT = new Timeslot();
				tempT.setId(resultSet.getString("timeslotUUID"));
				tempT.setBeginTime(resultSet.getTime("beginTime").toString().substring(0, 5));
				tempT.setDayId(duuid);
				tal.add(tempT);
			}

			resultSet.close();
			ps.close();

			for (Timeslot t : tal) {
				t.setMeeting(retrieveMeetingByTimeslot(t.getId()));
			}
			return tal;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting Timeslot: " + e.getMessage());
		}

	}

	/**
	 * @param tuuid
	 * @return Meeting Object (null if no meeting retrieve
	 * @throws Exception
	 */
	public Meeting retrieveMeetingByTimeslot(String tuuid) throws Exception {
		try {
			Meeting m = new Meeting();
			boolean hasMeeting = false;

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Meeting WHERE timeslotUUID=?;");
			ps.setString(1, tuuid);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				m.setId(resultSet.getString("meetingUUID"));
				m.setPartInfo(resultSet.getString("partInfo"));
				// m.setSecretCode(resultSet.getString("secretCode"));
				m.setTimeslotId(tuuid);
				hasMeeting = true;
			}

			resultSet.close();
			ps.close();

			if (hasMeeting) {
				return m;
			}

			return null;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting Timeslot: " + e.getMessage());
		}

	}

	// ---------------------ADD DATA TO DB---------------------------

	/**
	 * @param given
	 * @return boolean indicates if Schedule successfully add into DB FALSE
	 *         Condition: conn Null Schedule is already present in DB Violation of
	 *         DB rules
	 * @throws Exception
	 */
	public boolean addSchedule(Schedule given) throws Exception {
		if (conn == null) {
			return false;
		}

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

			addDayfromAL(given.getId(), given.getDays());

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("DBCONNECTED but Failed to insert table Schedule: " + e.getMessage());

			/*
			 * throw new Exception("Failed to insert table Schedule: " + given.getId() +
			 * "<>" + given.getName() + "<>" + given.getAuthor() + "<>" +
			 * given.getSecretCode() + "<>" + given.getReleaseCode() + "<>" +
			 * given.getCreatedDate() + "<>" + given.getTimePeriod() + "<>" +
			 * given.getStartTime() + "<>" + given.getEndTime() + "<>" +
			 * given.getStartDate() + "<>" + given.getEndDate() + "<>" + given.getD() + "<>"
			 * + e.getMessage());
			 */
		}
	}

	/**
	 * @param scheduleUUID
	 * @param dal
	 * @return boolean indicates if Day successfully add into DB
	 * @throws Exception
	 */
	public boolean addDayfromAL(String scheduleUUID, ArrayList<Day> dal) throws Exception {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Schedule");

			for (Day d : dal) {
				ps = conn.prepareStatement("INSERT INTO Day(dayUUID,date,scheduleUUID) values(?,?,?);");
				ps.setString(1, d.getId());
				ps.setDate(2, java.sql.Date.valueOf(d.getDate()));
				ps.setString(3, scheduleUUID);
				ps.execute();

				addTimeslotfromAL(d.getId(), d.getTimeslots());
			}
			ps.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to insert table Day: " + e.getMessage());
		}
	}

	/**
	 * @param dayUUID
	 * @param tal
	 * @return boolean indicates if Timeslot successfully add into DB
	 * @throws Exception
	 */
	public boolean addTimeslotfromAL(String dayUUID, ArrayList<Timeslot> tal) throws Exception {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Schedule");

			for (Timeslot t : tal) {
				ps = conn.prepareStatement("INSERT INTO Timeslot(timeslotUUID,beginTime,dayUUID) values(?,?,?);");
				ps.setString(1, t.getId());
				ps.setTime(2, new Time(Time_formatter.parse(t.getBeginTime()).getTime()));
				ps.setString(3, dayUUID);
				ps.execute();
				if (t.getMeeting() != null) {
					addMeeting(t.getId(), t.getMeeting());
				}

			}
			ps.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to insert table Timeslot: " + e.getMessage());
		}
	}

	/**
	 * @param timeslotUUID
	 * @param m
	 * @return boolean indicates if Meeting successfully add into DB
	 * @throws Exception
	 */
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
			e.printStackTrace();
			throw new Exception("Failed to insert table Meeting(with timeslotUUID): " + e.getMessage());
		}
	}

	// ---------------------Liter 2---------------------------
	public boolean checkBySCode(String secretCode) throws Exception {

		try {

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Schedule WHERE secretCode=?;");
			ps.setString(1, secretCode);
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				resultSet.close();
				ps.close();
				return true;
			}

			resultSet.close();
			ps.close();
			return false;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed checking table Schedule by Secret Code: " + e.getMessage());
		}

	}

	public boolean checkByRCode(String releaseCode) throws Exception {

		try {

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Schedule WHERE releaseCode=?;");
			ps.setString(1, releaseCode);
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				resultSet.close();
				ps.close();
				return true;
			}

			resultSet.close();
			ps.close();
			return false;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed checking table Schedule by Release Code: " + e.getMessage());
		}

	}

	public String GetScheduleIdOrg(String secretCode) throws Exception {
		try {

			String result_suuid = "";

			PreparedStatement ps = conn
					.prepareStatement("SELECT DISTINCT scheduleUUID FROM Schedule WHERE secretCode=?;");
			ps.setString(1, secretCode);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				result_suuid = resultSet.getString("scheduleUUID");
			}

			resultSet.close();
			ps.close();

			return result_suuid;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting Schedule UUID by secretCode: " + e.getMessage());
		}

	}

	public String GetScheduleIdPar(String releaseCode) throws Exception {
		try {

			String result_suuid = "";

			PreparedStatement ps = conn
					.prepareStatement("SELECT DISTINCT scheduleUUID FROM Schedule WHERE releaseCode=?;");
			ps.setString(1, releaseCode);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				result_suuid = resultSet.getString("scheduleUUID");
			}

			resultSet.close();
			ps.close();

			return result_suuid;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting Schedule UUID by releaseCode: " + e.getMessage());
		}

	}

	public boolean deleteSchedule(String suuid) throws Exception {

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

			ps = conn.prepareStatement("DELETE FROM Schedule WHERE scheduleUUID=?;");
			ps.setString(1, suuid);
			ps.execute();

			ps.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to DELETE Schedule: " + e.getMessage());
		}
	}

	// ---------------------Liter 3---------------------------
	
	public Schedule getScheduleInfo(String suuid) throws Exception {

		try {
			Schedule schedule = new Schedule();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Schedule WHERE scheduleUUID=?;");
			ps.setString(1, suuid);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				schedule.setId(resultSet.getString("scheduleUUID"));
				schedule.setTimePeriod(resultSet.getInt("timePeriod"));
				schedule.setStartTime(resultSet.getTime("startTime").toString().substring(0, 5));
				schedule.setEndTime(resultSet.getTime("endTime").toString().substring(0, 5));
				schedule.setStartDate(resultSet.getDate("startDate").toString());
				schedule.setEndDate(resultSet.getDate("endDate").toString());
			}

			return schedule;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getScheduleInfo: " + e.getMessage());
		}
	}


	public boolean extendDate(String suuid, String startDate, String endDate, ArrayList<Day> dal) throws Exception {

		if (conn == null) {
			return false;
		}

		try {
			String testID = null;
				
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Schedule WHERE scheduleUUID=?");
			ps.setString(1, suuid);
			ResultSet resultSet = ps.executeQuery();
			
			//test if schedule exists
			while (resultSet.next()) {
				testID = suuid;
			}
			if (testID==null) {
				return false;
			}
			
			//update Schedule
			ps = conn.prepareStatement("UPDATE Schedule SET startDate=?, endDate=? WHERE scheduleUUID=?;");
			ps.setDate(1, java.sql.Date.valueOf(startDate));
			ps.setDate(2, java.sql.Date.valueOf(endDate));
			ps.setString(3, suuid);
			ps.execute();

			//insert Day list
			for (Day d : dal) {
				ps = conn.prepareStatement("INSERT INTO Day(dayUUID,date,scheduleUUID) values(?,?,?);");
				ps.setString(1, d.getId());
				ps.setDate(2, java.sql.Date.valueOf(d.getDate()));
				ps.setString(3, d.getScheduleId());
				ps.execute();
				addTimeslotfromAL(d.getId(), d.getTimeslots());
			}
			
			ps.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to Extend Date: " + e.getMessage());
		}
	}

}