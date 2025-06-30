package com.projectManagementApp.payloads;

import java.util.Date;

public class AuthResponse {
	private String jwt;
	private String message;
	private Date expirationTime;
	public String getJwt() {
		return jwt;
	}
	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getExpirationTime() {
		return expirationTime;
	}
	public void setExpirationTime(Date date) {
		this.expirationTime = date;
	}
	
	

}
