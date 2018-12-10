package db;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;

import com.amazonaws.db.SchedulerDAO;
import com.amazonaws.model.Schedule;

public class SchedulerDAOTest {

//	@Test
//	public void testFind() {
//		SchedulerDAO sd = new SchedulerDAO();
//	    try {
//	    	String secretCode = "";
//	    	String suu_id = sd.GetScheduleIdOrg(secretCode);
//	    	System.out.println("schdule id:"+ suu_id);
//	    } catch (Exception e) {
//	    	fail ("didn't work:" + e.getMessage());
//	    }
//	}
//	
//	@Test
//	public void testCreate() {
//	    ConstantsDAO cd = new ConstantsDAO();
//	    try {
//	    	// can add it
//	    	String id = UUID.randomUUID().toString().substring(0, 20); // no more than 20 because of DB restrictions...
//	    	Constant constant = new Constant(id, 14);
//	    	boolean b = cd.addConstant(constant);
//	    	System.out.println("add constant: " + b);
//	    	
//	    	// can retrieve it
//	    	Constant c2 = cd.getConstant(constant.name);
//	    	System.out.println("C2:" + c2.name);
//	    	
//	    	// can delete it
//	    	assertTrue (cd.deleteConstant(c2));
//	    } catch (Exception e) {
//	    	fail ("didn't work:" + e.getMessage());
//	    }
//	}

}
