package com.amazonaws.lambda.demo;

import java.util.ArrayList;

import com.amazonaws.model.SearchResult;

public class SearchTimeslotResponse {
	String response;
	int httpcode;
	ArrayList<SearchResult> availableSlotsItems;
	
	
	public SearchTimeslotResponse (String s, int code) {
		this.response = s;
		this.httpcode = code;
	}
	
	public SearchTimeslotResponse (String s, ArrayList<SearchResult> availableSlotsItems) {
		this.response = s;
		this.httpcode = 200;
		this.availableSlotsItems = availableSlotsItems;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}

	public String getResponse() {
		return response;
	}

	public int getHttpcode() {
		return httpcode;
	}

	public ArrayList<SearchResult> getAvailableSlotsItems() {
		return availableSlotsItems;
	}
	
}