package com.projectManagementApp.globalException;

public class InvalidTokenException extends Exception {
	public InvalidTokenException(String message) {
		// TODO Auto-generated constructor stub
		super(message);
	}

	public InvalidTokenException() {
		super("You have provided an invalid token please provide a valid token");
	}
}
