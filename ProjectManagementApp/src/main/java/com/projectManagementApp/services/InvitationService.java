package com.projectManagementApp.services;

import com.projectManagementApp.entities.Invitation;
import com.projectManagementApp.globalException.InvalidTokenException;

public interface InvitationService {
	
	public void sendInvitation(String email, Long projectId);
	
	public Invitation acceptInvitation(String token , Long userId) throws InvalidTokenException;
	
	public String getTokenByuserMail(String userEmail);
	
	void deleteToken(String token);
}
