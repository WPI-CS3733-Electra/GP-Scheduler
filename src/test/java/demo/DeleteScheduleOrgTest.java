package demo;

import static org.junit.Assert.*;

import org.junit.Test;

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
import com.amazonaws.lambda.demo.DeleteScheduleHandler;
import com.amazonaws.lambda.demo.DeleteScheduleRequest;
import com.amazonaws.lambda.demo.DeleteScheduleResponse;
import com.amazonaws.lambda.demo.PostRequest;
import com.amazonaws.lambda.demo.PostResponse;

public class DeleteScheduleOrgTest {


	Context createContext(String apiCall) {
		TestContext ctx = new TestContext();
		ctx.setFunctionName(apiCall);
		return ctx;
	}

	@Test
	public void testDeleteScheduleOrg() throws IOException {
		CreateScheduleHandler handler = new CreateScheduleHandler();

		//------------create--------------
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
		Assert.assertEquals("Successfully Created Schedule:Alice", msg);
		Assert.assertEquals(200, code);

		//----------------delete-----------------
		DeleteScheduleHandler handlerDel = new DeleteScheduleHandler();

		//----------delete wrong ID------------
		String ScheduleID = "";
        DeleteScheduleRequest ar1 = new DeleteScheduleRequest(ScheduleID);

        ccRequest = new Gson().toJson(ar1);
        jsonRequest = new Gson().toJson(new PostRequest(ccRequest));
        
        input = new ByteArrayInputStream(jsonRequest.getBytes());
        output = new ByteArrayOutputStream();

        handlerDel.handleRequest(input, output, createContext("delete"));
        post = new Gson().fromJson(output.toString(), PostResponse.class);
        
        DeleteScheduleResponse respDel = new Gson().fromJson(post.body, DeleteScheduleResponse.class);
        System.out.println(respDel);
        
        msg = respDel.getResponse();
        code = respDel.getHttpcode();
        
		Assert.assertEquals("Schedule does not exist", msg);
		Assert.assertEquals(405, code);
		
		//----------delete success------------
        ScheduleID = resp.getScheduleId();
        DeleteScheduleRequest ar2 = new DeleteScheduleRequest(ScheduleID);
        
        ccRequest = new Gson().toJson(ar2);
        jsonRequest = new Gson().toJson(new PostRequest(ccRequest));
        
        input = new ByteArrayInputStream(jsonRequest.getBytes());
        output = new ByteArrayOutputStream();

        handlerDel.handleRequest(input, output, createContext("delete"));
        post = new Gson().fromJson(output.toString(), PostResponse.class);
        
        respDel = new Gson().fromJson(post.body, DeleteScheduleResponse.class);
        System.out.println(respDel);
        
        msg = respDel.getResponse();
        code = respDel.getHttpcode();

        Assert.assertEquals("Success", msg.substring(0, Math.min(msg.length(), 7)));
        Assert.assertEquals(200, code);
		
		//----------delete null------------
        DeleteScheduleRequest ar3 = new DeleteScheduleRequest(ScheduleID);
        
        ccRequest = new Gson().toJson(ar3);
        jsonRequest = new Gson().toJson(new PostRequest(ccRequest));
        
        input = new ByteArrayInputStream(jsonRequest.getBytes());
        output = new ByteArrayOutputStream();

        handlerDel.handleRequest(input, output, createContext("delete"));
        post = new Gson().fromJson(output.toString(), PostResponse.class);
        
        respDel = new Gson().fromJson(post.body, DeleteScheduleResponse.class);
        System.out.println(respDel);
        
        msg = respDel.getResponse();
        code = respDel.getHttpcode();

		Assert.assertEquals("Schedule does not exist", msg);
		Assert.assertEquals(405, code);
	}

}
