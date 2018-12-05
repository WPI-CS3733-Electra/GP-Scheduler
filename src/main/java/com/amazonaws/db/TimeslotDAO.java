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

	public Schedule getScheduleTimeInfo(String suuid) throws Exception {

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
			}

			resultSet.close();
			ps.close();

			return schedule;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting ScheduleTimeInfo: " + e.getMessage());
		}
	}

	public boolean openTimeslotByDay(ArrayList<Timeslot> tal) throws Exception {
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
			throw new Exception("Failed to open a DAY of Timeslot: " + e.getMessage());
		}
	}

	public boolean deleteTimeslotByDay(String duuid) throws Exception {

		if (conn == null) {
			return false;
		}

		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Timeslot WHERE dayUUID=?;");
			ps.setString(1, duuid);
			ResultSet resultSet = ps.executeQuery();

			// not present?
			if (!resultSet.next()) {
				resultSet.close();
				return false;
			}

			resultSet.close();

			ps = conn.prepareStatement("DELETE FROM Timeslot WHERE dayUUID=?;");
			ps.setString(1, duuid);
			ps.execute();

			ps.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to DELETE Timeslot By Day: " + e.getMessage());
		}
	}

	public ArrayList<Day> getScheduleDayInfo(String suuid) throws Exception {

		try {
			ArrayList<Day> dal = new ArrayList<Day>();

			PreparedStatement ps = conn.prepareStatement(
					"SELECT DISTINCT d.ScheduleUUID, dayUUID, date FROM Day d INNER JOIN Schedule s ON d.ScheduleUUID = s.ScheduleUUID WHERE d.scheduleUUID=? ORDER BY date;");
			ps.setString(1, suuid);
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

			return dal;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting Day: " + e.getMessage());
		}

	}

}