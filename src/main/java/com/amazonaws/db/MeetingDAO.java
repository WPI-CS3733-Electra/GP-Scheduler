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
public class MeetingDAO {

	java.sql.Connection conn;

	DateFormat TS_formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // "2016-09-21 13:43:27"
	DateFormat Time_formatter = new SimpleDateFormat("HH:mm"); // "13:43"

	public MeetingDAO() {
		try {
			conn = DatabaseUtil.connect();
		} catch (Exception e) {
			conn = null;
		}
	}

	// ---------------------Liter 2---------------------------
	public boolean addMeeting(Meeting m) throws Exception {

		if (conn == null) {
			return false;
		}

		try {

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Meeting WHERE meetingUUID=?;");
			ps.setString(1, m.getId());
			ResultSet resultSet = ps.executeQuery();

			// already present?
			while (resultSet.next()) {
				ps.close();
				resultSet.close();
				return false;
			}

			ps = conn.prepareStatement(
					"INSERT INTO Meeting(meetingUUID,partInfo,timeslotUUID,secretCode) values(?,?,?,?);");
			ps.setString(1, m.getId());
			ps.setString(2, m.getPartInfo());
			ps.setString(3, m.getTimeslotId());
			ps.setString(4, m.getSecretCode());
			ps.execute();

			ps.close();
			resultSet.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to insert table Meeting: " + e.getMessage());
		}
	}

	public boolean deleteMeeting(String muuid) throws Exception {

		if (conn == null) {
			return false;
		}

		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Meeting WHERE meetingUUID=?;");
			ps.setString(1, muuid);
			ResultSet resultSet = ps.executeQuery();

			// not present?
			if (!resultSet.next()) {
				resultSet.close();
				return false;
			}
			
			resultSet.close();

			ps = conn.prepareStatement("DELETE FROM Meeting WHERE meetingUUID=?;");
			ps.setString(1, muuid);
			ps.execute();

			ps.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to DELETE Meeting(Org): " + e.getMessage());
		}
	}
	
	public boolean deleteMeeting(String muuid, String mSecretCode) throws Exception {

		if (conn == null) {
			return false;
		}

		try {
			String dbSecretCode = "a";
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Meeting WHERE meetingUUID=?;");
			ps.setString(1, muuid);
			ResultSet resultSet = ps.executeQuery();

			// not present?
			while (resultSet.next()) {
				dbSecretCode = resultSet.getString("secretCode");
			}
			resultSet.close();
			
			if(!mSecretCode.equals(dbSecretCode)) {
				return false;
			}
			
			ps = conn.prepareStatement("DELETE FROM Meeting WHERE meetingUUID=?;");
			ps.setString(1, muuid);
			ps.execute();
			ps.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to DELETE Meeting(Par): " + e.getMessage());
		}
	}

}