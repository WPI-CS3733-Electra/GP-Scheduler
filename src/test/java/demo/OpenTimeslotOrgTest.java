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
import com.amazonaws.lambda.demo.DeleteTimeslotHandler;
import com.amazonaws.lambda.demo.DeleteTimeslotRequest;
import com.amazonaws.lambda.demo.DeleteTimeslotResponse;
import com.amazonaws.lambda.demo.OpenTimeslotHandler;
import com.amazonaws.lambda.demo.OpenTimeslotRequest;
import com.amazonaws.lambda.demo.OpenTimeslotResponse;
import com.amazonaws.lambda.demo.OrganizerGetScheduleIdHandler;
import com.amazonaws.lambda.demo.OrganizerGetScheduleIdRequest;
import com.amazonaws.lambda.demo.OrganizerGetScheduleIdResponse;
import com.amazonaws.lambda.demo.PostRequest;
import com.amazonaws.lambda.demo.PostResponse;
import com.amazonaws.lambda.demo.ShowWeekScheduleHandler;
import com.amazonaws.lambda.demo.ShowWeekScheduleRequest;
import com.amazonaws.lambda.demo.ShowWeekScheduleResponse;
import com.amazonaws.model.Day;
import com.amazonaws.model.Timeslot;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;

public class OpenTimeslotOrgTest {

	Context createContext(String apiCall) {
		TestContext ctx = new TestContext();
		ctx.setFunctionName(apiCall);
		return ctx;
	}

	@Test
	public void testOpenTimeslotOrg() throws IOException {

		CreateScheduleHandler handler = new CreateScheduleHandler();

		//------------create schedule--------------
		String name = "Alice";
		String author = "James";
		String startDate = "2018-01-01"; //yyyy-MM-dd
		String endDate = "2018-01-07";
		String startTime = "10:00"; //HH:mm
		String endTime = "15:00";
		int timePeriod  = 15;
		CreateScheduleRequest arCreate = new CreateScheduleRequest
				(name, author, startDate, endDate, startTime, endTime, timePeriod);

		String ccRequest = new Gson().toJson(arCreate);
		String jsonRequest = new Gson().toJson(new PostRequest(ccRequest)); //create and put body to request

		InputStream input = new ByteArrayInputStream(jsonRequest.getBytes());
		OutputStream output = new ByteArrayOutputStream();

		handler.handleRequest(input, output, createContext("create"));

		PostResponse post = new Gson().fromJson(output.toString(), PostResponse.class);
		CreateScheduleResponse resp = new Gson().fromJson(post.body, CreateScheduleResponse.class);
		System.out.println(resp);

		String msg = resp.getResponse();
		int code = resp.getHttpcode();

		//---------------show schedule------------------
		ShowWeekScheduleHandler handlerShow = new ShowWeekScheduleHandler();

		String ScheduleID = resp.getScheduleId();
		ShowWeekScheduleRequest arShow = new ShowWeekScheduleRequest(ScheduleID, 1);

		ccRequest = new Gson().toJson(arShow);
		jsonRequest = new Gson().toJson(new PostRequest(ccRequest));

		input = new ByteArrayInputStream(jsonRequest.getBytes());
		output = new ByteArrayOutputStream();

		handlerShow.handleRequest(input, output, createContext("list"));
		post = new Gson().fromJson(output.toString(), PostResponse.class);

		ShowWeekScheduleResponse respShow = new Gson().fromJson(post.body, ShowWeekScheduleResponse.class);
		System.out.println(respShow);

		//------------------------------timeslot------------------------------------------
		DeleteTimeslotHandler handlerDel = new DeleteTimeslotHandler();

		//------------close-----------------
		Timeslot ts = respShow.getSchedule().getDays().get(0).getTimeslots().get(0); //first day 10:00

		String tsId = ts.getId();
		DeleteTimeslotRequest arDel = new DeleteTimeslotRequest(tsId);

		ccRequest = new Gson().toJson(arDel);
		jsonRequest = new Gson().toJson(new PostRequest(ccRequest)); //create and put body to request

		input = new ByteArrayInputStream(jsonRequest.getBytes());
		output = new ByteArrayOutputStream();

		handlerDel.handleRequest(input, output, createContext("delete"));

		post = new Gson().fromJson(output.toString(), PostResponse.class);
		DeleteTimeslotResponse respDel = new Gson().fromJson(post.body, DeleteTimeslotResponse.class);
		//		OpenTimeslotResponse resp1 = new Gson().fromJson(post.body, OpenTimeslotResponse.class);

		System.out.println(respDel);
		
		code = respDel.getHttpcode();
		Assert.assertEquals(200, code);

		//------------open success--------------
		OpenTimeslotHandler handlerCreate = new OpenTimeslotHandler();

		Day day = respShow.getSchedule().getDays().get(0);
		String dayId = day.getId();
		String beginTime = "10:00"; //HH:mm
		OpenTimeslotRequest ar1 = new OpenTimeslotRequest(dayId, beginTime);

		ccRequest = new Gson().toJson(ar1);
		jsonRequest = new Gson().toJson(new PostRequest(ccRequest)); //create and put body to request

		input = new ByteArrayInputStream(jsonRequest.getBytes());
		output = new ByteArrayOutputStream();

		handlerCreate.handleRequest(input, output, createContext("create"));

		post = new Gson().fromJson(output.toString(), PostResponse.class);
		OpenTimeslotResponse resp1 = new Gson().fromJson(post.body, OpenTimeslotResponse.class);

		System.out.println(resp1);

		msg = resp1.getResponse();
		code = resp1.getHttpcode();

		Assert.assertEquals("Successfully Open Timeslot at:10:00", msg);
		Assert.assertEquals(200, code);

		//------------open duplicate--------------
		beginTime = "12:00"; //HH:mm
		OpenTimeslotRequest ar2 = new OpenTimeslotRequest(dayId, beginTime);

		ccRequest = new Gson().toJson(ar2);
		jsonRequest = new Gson().toJson(new PostRequest(ccRequest)); //create and put body to request

		input = new ByteArrayInputStream(jsonRequest.getBytes());
		output = new ByteArrayOutputStream();

		handlerCreate.handleRequest(input, output, createContext("create"));

		post = new Gson().fromJson(output.toString(), PostResponse.class);
		OpenTimeslotResponse resp2 = new Gson().fromJson(post.body, OpenTimeslotResponse.class);

		System.out.println(resp2);

		msg = resp2.getResponse();
		code = resp2.getHttpcode();

		Assert.assertEquals("Unable", msg.substring(0, Math.min(msg.length(), 6)));
		Assert.assertEquals(403, code);
	}
	
	

}
