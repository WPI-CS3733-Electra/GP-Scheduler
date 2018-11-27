package com.amazonaws.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SchedulerDAO {

	java.sql.Connection conn;

    public SchedulerDAO() {
    	try  {
    		conn = DatabaseUtil.connect();
    	} catch (Exception e) {
    		conn = null;
    	}
    }

  
}