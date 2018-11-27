package com.amazonaws.model;

public class Meeting {
	String id;
	String partInfo;
	String tId;
	String secretCode;
	
	public Meeting(String id, String partInfo, String tId, String secretCode ) {
		this.id = id;
		this.partInfo = partInfo;
		this.tId = tId;
		this.secretCode = secretCode;
	}

}
