package demo;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.amazonaws.lambda.demo.CreateScheduleHandler;
import com.amazonaws.lambda.demo.CreateScheduleRequest;
import com.amazonaws.lambda.demo.CreateScheduleResponse;
import com.amazonaws.lambda.demo.DeleteScheduleHandler;
import com.amazonaws.lambda.demo.DeleteScheduleRequest;
import com.amazonaws.lambda.demo.DeleteScheduleResponse;
import com.amazonaws.lambda.demo.PostRequest;
import com.amazonaws.lambda.demo.PostResponse;
import com.amazonaws.lambda.demo.ShowWeekScheduleHandler;
import com.amazonaws.lambda.demo.ShowWeekScheduleRequest;
import com.amazonaws.lambda.demo.ShowWeekScheduleResponse;
import com.amazonaws.model.Schedule;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;

public class ShowWeekScheduleTest {

	Context createContext(String apiCall) {
		TestContext ctx = new TestContext();
		ctx.setFunctionName(apiCall);
		return ctx;
	}

	@Test
	public void testShowWeekSchedule() throws IOException {
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

		String sc = resp.getSecretCode(); //for testing show
		Assert.assertEquals("Successfully Created Schedule:Alice", msg);
		Assert.assertEquals(200, code);

		//----------------show-----------------
		ShowWeekScheduleHandler handlerShow = new ShowWeekScheduleHandler();

		//----------show wrong ID------------
		String ScheduleID = "";
		ShowWeekScheduleRequest ar1 = new ShowWeekScheduleRequest(ScheduleID, 1);

		ccRequest = new Gson().toJson(ar1);
		jsonRequest = new Gson().toJson(new PostRequest(ccRequest));

		input = new ByteArrayInputStream(jsonRequest.getBytes());
		output = new ByteArrayOutputStream();

		handlerShow.handleRequest(input, output, createContext("list"));
		post = new Gson().fromJson(output.toString(), PostResponse.class);

		ShowWeekScheduleResponse respShow = 
				new Gson().fromJson(post.body, ShowWeekScheduleResponse.class);
		System.out.println(respShow);

		msg = respShow.getResponse();
		code = respShow.getHttpcode();

		Assert.assertEquals("Invalid Schedule ID or week number", msg);
		Assert.assertEquals(405, code);

		//----------show success------------
		ScheduleID = resp.getScheduleId();
		ShowWeekScheduleRequest ar2 = new ShowWeekScheduleRequest(ScheduleID, 1);

		ccRequest = new Gson().toJson(ar2);
		jsonRequest = new Gson().toJson(new PostRequest(ccRequest));

		input = new ByteArrayInputStream(jsonRequest.getBytes());
		output = new ByteArrayOutputStream();

		handlerShow.handleRequest(input, output, createContext("list"));
		post = new Gson().fromJson(output.toString(), PostResponse.class);

		respShow = new Gson().fromJson(post.body, ShowWeekScheduleResponse.class);
		System.out.println(respShow);

		msg = respShow.getResponse();
		code = respShow.getHttpcode();

		Assert.assertEquals("Successfully show the week:1", msg);
		Assert.assertEquals(200, code);

		Schedule s = respShow.getSchedule();
		Assert.assertTrue(s.getId().equals(ScheduleID));
		
		//----------show week 0------------
		ScheduleID = resp.getScheduleId();
		ShowWeekScheduleRequest ar3 = new ShowWeekScheduleRequest(ScheduleID, 0);

		ccRequest = new Gson().toJson(ar3);
		jsonRequest = new Gson().toJson(new PostRequest(ccRequest));

		input = new ByteArrayInputStream(jsonRequest.getBytes());
		output = new ByteArrayOutputStream();

		handlerShow.handleRequest(input, output, createContext("list"));
		post = new Gson().fromJson(output.toString(), PostResponse.class);

		respShow = new Gson().fromJson(post.body, ShowWeekScheduleResponse.class);
		System.out.println(respShow);

		msg = respShow.getResponse();
		code = respShow.getHttpcode();

		Assert.assertEquals("Invalid Schedule ID or week number", msg);
		Assert.assertEquals(405, code);

		
		//----------show week 100------------
		ScheduleID = resp.getScheduleId();
		ShowWeekScheduleRequest ar4 = new ShowWeekScheduleRequest(ScheduleID, 100);

		ccRequest = new Gson().toJson(ar4);
		jsonRequest = new Gson().toJson(new PostRequest(ccRequest));

		input = new ByteArrayInputStream(jsonRequest.getBytes());
		output = new ByteArrayOutputStream();

		handlerShow.handleRequest(input, output, createContext("list"));
		post = new Gson().fromJson(output.toString(), PostResponse.class);

		respShow = new Gson().fromJson(post.body, ShowWeekScheduleResponse.class);
		System.out.println(respShow);

		msg = respShow.getResponse();
		code = respShow.getHttpcode();

		Assert.assertEquals("Invalid Schedule ID or week number", msg);
		Assert.assertEquals(405, code);
	}

}
