package demo;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import com.amazonaws.db.SchedulerDAO;
import com.amazonaws.lambda.demo.CreateScheduleHandler;
import com.amazonaws.lambda.demo.CreateScheduleRequest;
import com.amazonaws.lambda.demo.CreateScheduleResponse;
import com.amazonaws.lambda.demo.PostRequest;
import com.amazonaws.lambda.demo.PostResponse;

public class CreateScheduleOrgTest {
//	public final String secretCode = null;
//	public final String releaseCode = null;
//	public final String scheduleId = null;

	Context createContext(String apiCall) {
        TestContext ctx = new TestContext();
        ctx.setFunctionName(apiCall);
        return ctx;
    }

	@Test
	public void testCreateScheduleOrg() throws IOException {
		CreateScheduleHandler handler = new CreateScheduleHandler();

		//------------Success--------------
		String name = "Alice";
		String author = "James";
		String startDate = "2018-01-01"; //yyyy-MM-dd
		String endDate = "2018-01-07";
		String startTime = "10:00"; //HH:mm
		String endTime = "12:00";
		int timePeriod  = 15;
		CreateScheduleRequest ar = new CreateScheduleRequest
				(name, author, startDate, endDate, startTime, endTime, timePeriod);
        
        String ccRequest = new Gson().toJson(ar);
        String jsonRequest = new Gson().toJson(new PostRequest(ccRequest)); //create and put body to request
        
        InputStream input = new ByteArrayInputStream(jsonRequest.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("create"));

        PostResponse post = new Gson().fromJson(output.toString(), PostResponse.class);
        CreateScheduleResponse resp = new Gson().fromJson(post.body, CreateScheduleResponse.class);
        System.out.println(resp);
        
        String msg = resp.getResponse();
        int code = resp.getHttpcode();
        
//        secretCode
        
//        Assert.assertEquals("Success", r.substring(0, Math.min(r.length(), 7)));
        Assert.assertEquals("Successfully Created Schedule:Alice", msg);
        Assert.assertEquals(200, code);

        //----------test DAO------------
//		SchedulerDAO sd = new SchedulerDAO();
//	    try {
//	    	String secretCode = resp.getSecretCode();
//	    	String suu_id = sd.GetScheduleIdOrg(secretCode);
//	    	System.out.println("schdule id:"+ suu_id);
//	    	 Assert.assertTrue(!suu_id.equals(null));
//	    } catch (Exception e) {
//	    	fail ("didn't work:" + e.getMessage());
//	    }
	    
        //------------Bad Request--------------
        //TODO
        //Assert.assertEquals("Bad Request", r.substring(0, Math.min(r.length(), 11)));


        
        //------------Unable--------------
        String empty = "";
        ar = new CreateScheduleRequest(empty, empty, empty, empty, empty, empty, timePeriod);
        
        ccRequest = new Gson().toJson(ar);
        jsonRequest = new Gson().toJson(new PostRequest(ccRequest));
        
        input = new ByteArrayInputStream(jsonRequest.getBytes());
        output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("create"));

        post = new Gson().fromJson(output.toString(), PostResponse.class);
        resp = new Gson().fromJson(post.body, CreateScheduleResponse.class);
        System.out.println(resp);
        
        msg = resp.getResponse();
        code = resp.getHttpcode();

//        Assert.assertEquals("Unable", msg.substring(0, Math.min(msg.length(), 6)));
        Assert.assertEquals("Unable to create Schedule:(Unparseable date: \"\")", msg);
        Assert.assertEquals(403, code);

        
        //TODO Unable 405
        }

}
