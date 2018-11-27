package com.amazonaws.lambda.demo;

import java.util.UUID;

import com.amazonaws.db.SchedulerDAO;
import com.amazonaws.model.Schedule;
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
		UUID sId = UUID.randomUUID();
		// check if present
		Schedule exist = dao.getSchedule(sId);
		Schedule constant = new Constant (name, value);
		if (exist == null) {
			return dao.addConstant(constant);
		} else {
			return dao.updateConstant(constant);
		}
	}
}
