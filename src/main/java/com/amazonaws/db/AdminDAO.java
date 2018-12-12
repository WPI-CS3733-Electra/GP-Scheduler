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

import com.amazonaws.model.BriefScheduleInfo;
import com.amazonaws.model.Day;
import com.amazonaws.model.Meeting;
import com.amazonaws.model.Schedule;
import com.amazonaws.model.SearchResult;
import com.amazonaws.model.Timeslot;

/**
 * @author Ziqian Zeng
 *
 */
public class AdminDAO {

	java.sql.Connection conn;

	DateFormat TS_formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // "2016-09-21 13:43:27"
	DateFormat Time_formatter = new SimpleDateFormat("HH:mm"); // "13:43"

	public AdminDAO() {
		try {
			conn = DatabaseUtil.connect();
		} catch (Exception e) {
			conn = null;
		}
	}

	// ---------------------Liter 3---------------------------

	public boolean Oath(String oathCode) throws Exception {

		if (conn == null || oathCode == null) {
			return false;
		}

		try {
			String dbOath = null;
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Admin WHERE oathCode=?");
			ps.setString(1, oathCode);
			
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				dbOath = resultSet.getString("oathCode");
			}
			
			ps.close();
			resultSet.close();
			
			if (dbOath != null) {
				if(oathCode.equals(dbOath)) {
					return true;
				}
			}

			return false;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in Admin Authentication: " + e.getMessage());
		}
	}

	public ArrayList<BriefScheduleInfo> reviewRecent(String recentDate) throws Exception {
		try {
			ArrayList<BriefScheduleInfo> bal = new ArrayList<BriefScheduleInfo>();

			PreparedStatement ps = conn.prepareStatement(
					"SELECT s.scheduleUUID, name, author, createdDate, startDate, endDate, count(t.timeslotUUID) AS countTimeslot, count(m.meetingUUID) AS countMeeting "
							+ "FROM Schedule s INNER JOIN Day d ON s.scheduleUUID = d.scheduleUUID "
							+ "INNER JOIN Timeslot t ON d.dayUUID = t.dayUUID "
							+ "LEFT JOIN Meeting m ON t.timeslotUUID = m.timeslotUUID " + "WHERE s.createdDate>=? "
							+ "GROUP BY s.scheduleUUID " + "ORDER BY s.createdDate;");
			ps.setTimestamp(1, new Timestamp(TS_formatter.parse(recentDate).getTime()));
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				BriefScheduleInfo tempB = new BriefScheduleInfo();
				tempB.setsId(resultSet.getString("scheduleUUID"));
				tempB.setName(resultSet.getString("name"));
				tempB.setAuthor(resultSet.getString("author"));
				tempB.setCreatedDate(resultSet.getTimestamp("createdDate").toString().substring(0, 19));
				tempB.setStartDate(resultSet.getDate("startDate").toString());
				tempB.setEndDate(resultSet.getDate("endDate").toString());
				tempB.setTimeslots(resultSet.getInt("countTimeslot"));
				tempB.setMeetings(resultSet.getInt("countMeeting"));
				bal.add(tempB);
			}

			resultSet.close();
			ps.close();

			return bal;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to REVIEW recent schedule: " + e.getMessage());
		}

	}

	public int deleteOld(String oldDate) throws Exception {
		try {
			int scheduleDeleted = 0;

			PreparedStatement ps = conn
					.prepareStatement("SELECT count(scheduleUUID) AS totalSchedule FROM Schedule WHERE createdDate<=?");
			ps.setTimestamp(1, new Timestamp(TS_formatter.parse(oldDate).getTime()));
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				scheduleDeleted = resultSet.getInt("totalSchedule");
			}
			resultSet.close();

			if (scheduleDeleted == 0) {
				ps.close();
				return scheduleDeleted;
			}

			ps = conn.prepareStatement("DELETE FROM Schedule WHERE createdDate<=?");
			ps.setTimestamp(1, new Timestamp(TS_formatter.parse(oldDate).getTime()));
			ps.execute();

			ps.close();

			return scheduleDeleted;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to DELETE old schedule: " + e.getMessage());
		}

	}

}