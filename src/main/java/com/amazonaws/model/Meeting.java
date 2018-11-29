package com.amazonaws.model;

public class Meeting {
	String id;
	String secretCode;
	String partInfo;
	String timeslotId;

	public Meeting(String id, String secretCode, String partInfo, String timeslotId) {
		this.id = id;
		this.secretCode = secretCode;
		this.partInfo = partInfo;
		this.timeslotId = timeslotId;
	}

	public Meeting() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSecretCode() {
		return secretCode;
	}

	public void setSecretCode(String secretCode) {
		this.secretCode = secretCode;
	}

	public String getPartInfo() {
		return partInfo;
	}

	public void setPartInfo(String partInfo) {
		this.partInfo = partInfo;
	}

	public String getTimeslotId() {
		return timeslotId;
	}

	public void setTimeslotId(String timeslotId) {
		this.timeslotId = timeslotId;
	}
	
	

}
