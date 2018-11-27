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
		String sId = this.genUUIDString();
		String sCode = this.genCode(name + author);
		String rCode = this.genCode(author + startDate + endDate + timePeriod );
		
		
		// check if present
		Schedule exist = dao.getSchedule(sId);
		Schedule Schedule = new Schedule (sId, name, author, sCode, rCode,);
		if (exist == null) {
			return dao.addConstant(constant);
		} else {
			return dao.updateConstant(constant);
		}
	}
	
	String genUUIDString() {
		UUID u = UUID.randomUUID();
		String s = u.toString();
		return s;
	}
	
	String genCode(String s) {
		UUID u = UUID.fromString(s);
		String i = u.toString();
		String f = i.substring(0, 5);
		return f;
	}
	
}
