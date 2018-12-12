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
import com.amazonaws.lambda.demo.CreateScheduleHandler;
import com.amazonaws.lambda.demo.CreateScheduleRequest;
import com.amazonaws.lambda.demo.CreateScheduleResponse;
import com.amazonaws.lambda.demo.OrganizerGetScheduleIdHandler;
import com.amazonaws.lambda.demo.OrganizerGetScheduleIdRequest;
import com.amazonaws.lambda.demo.OrganizerGetScheduleIdResponse;
import com.amazonaws.lambda.demo.PostRequest;
import com.amazonaws.lambda.demo.PostResponse;
import com.amazonaws.model.Schedule;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;

public class CreateMeetingParTest {

	Context createContext(String apiCall) {
		TestContext ctx = new TestContext();
		ctx.setFunctionName(apiCall);
		return ctx;
	}

	@Test
	public void testCreateMeetingPar() throws IOException {

//		CreateScheduleHandler handler = new CreateScheduleHandler();
//		//------------create--------------
//		String name = "Alice";
//		String author = "James";
//		String startDate = "2018-01-01"; //yyyy-MM-dd
//		String endDate = "2018-01-07";
//		String startTime = "10:00"; //HH:mm
//		String endTime = "12:00";
//		int timePeriod  = 15;
//		CreateScheduleRequest ar = new CreateScheduleRequest
//				(name, author, startDate, endDate, startTime, endTime, timePeriod);
//
//		String ccRequest = new Gson().toJson(ar);
//		String jsonRequest = new Gson().toJson(new PostRequest(ccRequest)); //create and put body to request
//
//		InputStream input = new ByteArrayInputStream(jsonRequest.getBytes());
//		OutputStream output = new ByteArrayOutputStream();
//
//		handler.handleRequest(input, output, createContext("create"));
//
//		PostResponse post = new Gson().fromJson(output.toString(), PostResponse.class);
//		CreateScheduleResponse resp = new Gson().fromJson(post.body, CreateScheduleResponse.class);
//		System.out.println(resp);
//
//		String msg = resp.getResponse();
//		int code = resp.getHttpcode();
//
//		String sc = resp.getSecretCode(); //for testing show
//		Assert.assertEquals("Successfully Created Schedule:Alice", msg);
//		Assert.assertEquals(200, code);

		//----------------get ID-----------------
		CreateMeetingHandler handlerCreate = new CreateMeetingHandler();
		//TODO: test when finish schedule DAO
		//----------wrong ID------------
//		String secretCode = "";
//		OrganizerGetScheduleIdRequest ar1 = new OrganizerGetScheduleIdRequest(secretCode);
//
//		ccRequest = new Gson().toJson(ar1);
//		jsonRequest = new Gson().toJson(new PostRequest(ccRequest));
//
//		input = new ByteArrayInputStream(jsonRequest.getBytes());
//		output = new ByteArrayOutputStream();
//
//		handlerCreate.handleRequest(input, output, createContext("list"));
//		post = new Gson().fromJson(output.toString(), PostResponse.class);
//
//		OrganizerGetScheduleIdResponse respShow = 
//				new Gson().fromJson(post.body, OrganizerGetScheduleIdResponse.class);
//		System.out.println(respShow);
//
//		msg = respShow.getResponse();
//		code = respShow.getHttpcode();
//
//		Assert.assertEquals("secretCode does not exist", msg);
//		Assert.assertEquals(405, code);
//		
//		//----------get success------------
//		secretCode = resp.getSecretCode();
//		OrganizerGetScheduleIdRequest ar2 = new OrganizerGetScheduleIdRequest(secretCode);
//
//		ccRequest = new Gson().toJson(ar2);
//		jsonRequest = new Gson().toJson(new PostRequest(ccRequest));
//
//		input = new ByteArrayInputStream(jsonRequest.getBytes());
//		output = new ByteArrayOutputStream();
//
//		handlerCreate.handleRequest(input, output, createContext("list"));
//		post = new Gson().fromJson(output.toString(), PostResponse.class);
//
//		respShow = new Gson().fromJson(post.body, OrganizerGetScheduleIdResponse.class);
//		System.out.println(respShow);
//
//		msg = respShow.getResponse();
//		code = respShow.getHttpcode();
//
//        Assert.assertEquals("Success", msg.substring(0, Math.min(msg.length(), 7)));
//		Assert.assertEquals(200, code);
////
////		Schedule s = respShow.getSchedule();
////		Assert.assertTrue(s.getId().equals(ScheduleID));
	}

}
