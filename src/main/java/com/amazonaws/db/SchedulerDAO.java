package com.amazonaws.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.model.Schedule;

public class SchedulerDAO {

	java.sql.Connection conn;

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

}