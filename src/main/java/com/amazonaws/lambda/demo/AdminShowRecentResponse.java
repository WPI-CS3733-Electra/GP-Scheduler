package com.amazonaws.lambda.demo;

import java.util.ArrayList;

import com.amazonaws.model.BriefScheduleInfo;

public class AdminShowRecentResponse {
	
	ArrayList<BriefScheduleInfo> info;
	String response;
	int httpcode;
	
	public AdminShowRecentResponse (String s, int code) {
		this.response = s;
		this.httpcode = code;
	}
	
	public AdminShowRecentResponse (String s, ArrayList<BriefScheduleInfo> info) {
		this.response = s;
		this.httpcode = 200;
		this.info = info;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}

	public ArrayList<BriefScheduleInfo> getInfo() {
		return info;
	}

	public String getResponse() {
		return response;
	}

	public int getHttpcode() {
		return httpcode;
	}

	

}


