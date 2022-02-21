package com.mysite.core.utils;

public class ApiResponse {

	private final String responseBody;
	private final int statusCode;

	public ApiResponse(int status, String response) {
		this.statusCode = status;
		this.responseBody = response;
	}

	public String getResponseBody() {
		return this.responseBody;
	}

	public int getStatusCode() {
		return this.statusCode;
	}

}
