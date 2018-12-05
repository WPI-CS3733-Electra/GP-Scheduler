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

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Timeslot WHERE timeslotUUID=?;");
			ps.setString(1, t.getId());
			ResultSet resultSet = ps.executeQuery();

			// already present?
			while (resultSet.next()) {
				ps.close();
				resultSet.close();
				return false;
			}

			ps = conn.prepareStatement("INSERT INTO Timeslot(timeslotUUID,beginTime,dayUUID) values(?,?,?);");
			ps.setString(1, t.getId());
			ps.setTime(2, new Time(Time_formatter.parse(t.getBeginTime()).getTime()));
			ps.setString(3, t.getDayId());
			ps.execute();

			ps.close();
			resultSet.close();
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
	
	public boolean openTimeslot(Timeslot t) throws Exception {

		if (conn == null) {
			return false;
		}

		try {

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Timeslot WHERE timeslotUUID=?;");
			ps.setString(1, t.getId());
			ResultSet resultSet = ps.executeQuery();

			// already present?
			while (resultSet.next()) {
				ps.close();
				resultSet.close();
				return false;
			}

			ps = conn.prepareStatement("INSERT INTO Timeslot(timeslotUUID,beginTime,dayUUID) values(?,?,?);");
			ps.setString(1, t.getId());
			ps.setTime(2, new Time(Time_formatter.parse(t.getBeginTime()).getTime()));
			ps.setString(3, t.getDayId());
			ps.execute();

			ps.close();
			resultSet.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to open 1 Timeslot: " + e.getMessage());
		}
	}

}