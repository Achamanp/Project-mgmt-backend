package com.projectManagementApp.services;

import com.projectManagementApp.entities.User;
import com.projectManagementApp.globalException.InvalidOtpException;
import com.projectManagementApp.globalException.InvalidTokenException;
import com.projectManagementApp.globalException.OtpExpiredException;
import com.projectManagementApp.globalException.ResourceNotFoundException;

public interface UserService {
	
	User findUserProfileByJwt();
	
	User findUserByEmail(String email);
	
	User findUserById(Long userId);
	
	User updateUsersProjectSize(User user , int number);

	String forgotPassword(String email) throws ResourceNotFoundException;

	String resetPassword(String token, Integer otp, String newPassword)
			throws InvalidTokenException, OtpExpiredException, InvalidOtpException;
	
	
	
	
	
}
