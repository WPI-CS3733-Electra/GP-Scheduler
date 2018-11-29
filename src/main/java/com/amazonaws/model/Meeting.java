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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPartInfo() {
		return partInfo;
	}

	public void setPartInfo(String partInfo) {
		this.partInfo = partInfo;
	}

	public String gettId() {
		return tId;
	}

	public void settId(String tId) {
		this.tId = tId;
	}

	public String getSecretCode() {
		return secretCode;
	}

	public void setSecretCode(String secretCode) {
		this.secretCode = secretCode;
	}

}
