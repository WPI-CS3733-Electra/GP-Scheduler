package com.amazonaws.lambda.demo;

public class CreateScheduleResponse {
	String response; //make public for testing
	int httpcode; //make public for testing
	String secretCode;
	String releaseCode;
	String scheduleId;
	
	public CreateScheduleResponse (String s, int code) {
		this.response = s;
		this.httpcode = code;
	}
	
	// 200 means success
	public CreateScheduleResponse (String s, String sc, String rc, String id) {
		this.response = s;
		this.secretCode = sc;
		this.releaseCode = rc;
		this.scheduleId = id;
		this.httpcode = 200;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}

	public String getSecretCode() {
		return secretCode;
	}

	public String getReleaseCode() {
		return releaseCode;
	}

	public String getScheduleId() {
		return scheduleId;
	}

	public String getResponse() {
		return response;
	}

	public int getHttpcode() {
		return httpcode;
	}
	
	
}
