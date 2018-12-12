package demo;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.amazonaws.lambda.demo.CreateMeetingHandler;
import com.amazonaws.lambda.demo.CreateMeetingRequest;
import com.amazonaws.lambda.demo.CreateMeetingResponse;
import com.amazonaws.lambda.demo.CreateScheduleHandler;
import com.amazonaws.lambda.demo.CreateScheduleRequest;
import com.amazonaws.lambda.demo.CreateScheduleResponse;
import com.amazonaws.lambda.demo.DeleteTimeslotsByDayHandler;
import com.amazonaws.lambda.demo.DeleteTimeslotsByDayRequest;
import com.amazonaws.lambda.demo.DeleteTimeslotsByDayResponse;
import com.amazonaws.lambda.demo.OrganizerCancelMeetingHandler;
import com.amazonaws.lambda.demo.OrganizerCancelMeetingRequest;
import com.amazonaws.lambda.demo.OrganizerCancelMeetingResponse;
import com.amazonaws.lambda.demo.PostRequest;
import com.amazonaws.lambda.demo.PostResponse;
import com.amazonaws.lambda.demo.ShowWeekScheduleHandler;
import com.amazonaws.lambda.demo.ShowWeekScheduleRequest;
import com.amazonaws.lambda.demo.ShowWeekScheduleResponse;
import com.amazonaws.model.Day;
import com.amazonaws.model.Timeslot;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;

public class CancelMeetingOrgTest {
	Context createContext(String apiCall) {
		TestContext ctx = new TestContext();
		ctx.setFunctionName(apiCall);
		return ctx;
	}

	@Test
	public void testCancelMeetingOrg() throws IOException {
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
		//------------close-----------------
		DeleteTimeslotsByDayHandler handlerDel = new DeleteTimeslotsByDayHandler();

		Day day = respShow.getSchedule().getDays().get(0); //first day
		String dayId = day.getId();

//		DeleteTimeslotsByDayRequest arDel = new DeleteTimeslotsByDayRequest(dayId);
//
//		ccRequest = new Gson().toJson(arDel);
//		jsonRequest = new Gson().toJson(new PostRequest(ccRequest)); //create and put body to request
//
//		input = new ByteArrayInputStream(jsonRequest.getBytes());
//		output = new ByteArrayOutputStream();
//
//		handlerDel.handleRequest(input, output, createContext("delete"));
//
//		post = new Gson().fromJson(output.toString(), PostResponse.class);
//		DeleteTimeslotsByDayResponse respDel = new Gson().fromJson(post.body, DeleteTimeslotsByDayResponse.class);
//
//		System.out.println(respDel);

		//------------------------------meeting------------------------------------------
		//------------create success--------------

		CreateMeetingHandler handlerCreate = new CreateMeetingHandler();

		String partInfo = "DNE@wpi.com";
		Timeslot ts = day.getTimeslots().get(0); //first day 10:00
		String timeslotId = ts.getId();

		CreateMeetingRequest arCre = new CreateMeetingRequest(partInfo, timeslotId);

		ccRequest = new Gson().toJson(arCre);
		jsonRequest = new Gson().toJson(new PostRequest(ccRequest)); //create and put body to request

		input = new ByteArrayInputStream(jsonRequest.getBytes());
		output = new ByteArrayOutputStream();

		handlerCreate.handleRequest(input, output, createContext("create"));

		post = new Gson().fromJson(output.toString(), PostResponse.class);
		CreateMeetingResponse respCre = new Gson().fromJson(post.body, CreateMeetingResponse.class);

		System.out.println(respCre);

		//---------------show schedule------------------

		ShowWeekScheduleRequest arShow1 = new ShowWeekScheduleRequest(ScheduleID, 1);

		ccRequest = new Gson().toJson(arShow1);
		jsonRequest = new Gson().toJson(new PostRequest(ccRequest));

		input = new ByteArrayInputStream(jsonRequest.getBytes());
		output = new ByteArrayOutputStream();

		handlerShow.handleRequest(input, output, createContext("list"));
		post = new Gson().fromJson(output.toString(), PostResponse.class);

		ShowWeekScheduleResponse respShow1 = new Gson().fromJson(post.body, ShowWeekScheduleResponse.class);
		System.out.println(respShow1);
		
		//------------cancel success--------------

		OrganizerCancelMeetingHandler handlerCancel = new OrganizerCancelMeetingHandler();

		ts = respShow1.getSchedule().getDays().get(0).getTimeslots().get(0); //first day 10:00

		String meetingId = ts.getMeeting().getId();

		OrganizerCancelMeetingRequest ar1 = new OrganizerCancelMeetingRequest(meetingId);

		ccRequest = new Gson().toJson(ar1);
		jsonRequest = new Gson().toJson(new PostRequest(ccRequest)); //create and put body to request

		input = new ByteArrayInputStream(jsonRequest.getBytes());
		output = new ByteArrayOutputStream();

		handlerCancel.handleRequest(input, output, createContext("delete"));

		post = new Gson().fromJson(output.toString(), PostResponse.class);
		OrganizerCancelMeetingResponse resp1 = new Gson().fromJson(post.body, OrganizerCancelMeetingResponse.class);

		System.out.println(resp1);

		msg = resp1.getResponse();
		code = resp1.getHttpcode();
		
		Assert.assertEquals("Success", msg.substring(0, Math.min(msg.length(), 7)));
		Assert.assertEquals(200, code);
	
	}
}
