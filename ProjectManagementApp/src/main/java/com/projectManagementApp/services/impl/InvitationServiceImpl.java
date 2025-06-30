package com.projectManagementApp.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.projectManagementApp.entities.Invitation;
import com.projectManagementApp.entities.Project;
import com.projectManagementApp.entities.User;
import com.projectManagementApp.globalException.InvalidTokenException;
import com.projectManagementApp.globalException.ResourceNotFoundException;
import com.projectManagementApp.repositories.InvitationRepository;
import com.projectManagementApp.repositories.ProjectRepository;
import com.projectManagementApp.services.EmailService;
import com.projectManagementApp.services.InvitationService;
import com.projectManagementApp.services.UserService;

@Service
public class InvitationServiceImpl implements InvitationService{
	
	private static final Logger logger = LoggerFactory.getLogger(InvitationServiceImpl.class);
	
	
	@Autowired
	private InvitationRepository invitationRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	
	@Autowired
	private JavaMailSender javaMailSender;

	@Override
	public void sendInvitation(String email, Long projectId) {
		
		// TODO Auto-generated method stub
		
		// Check if project exists and get project details
	    Project project = projectRepository.findById(projectId)
	        .orElseThrow(() -> new IllegalArgumentException("Project with ID " + projectId + " does not exist"));
	    
	    if (isUserAlreadyMember(email, project)) {
	        logger.warn("User with email: {} is already a member of project: {}", email, projectId);
	        throw new IllegalArgumentException("User is already a member of this project");
	    }
	    
	    
	    Optional<Invitation> existingInvitation = invitationRepository
	            .findByEmailAndProjectIdAndIsExpiredFalseAndIsAcceptedFalse(email, projectId);
	    
	    
	    if (existingInvitation.isPresent()) {
	        logger.warn("Pending invitation already exists for email: {} and project: {}", email, projectId);
	        throw new IllegalArgumentException("A pending invitation already exists for this user");
	    }
		String token = UUID.randomUUID().toString();
		Invitation invitation = new Invitation();
		invitation.setEmail(email);
		invitation.setToken(token);
		invitation.setProjectId(projectId);
		invitation.setTokenExpiration(LocalDateTime.now().plusMinutes(10));
		invitation.setExpired(false);
		this.invitationRepository.save(invitation);
		
		
		String invitationLink = "http://localhost:5173/accept_invitation?token="+token;
		emailService.sendEmailWithTokken(email, invitationLink);
		
	}

	private boolean isUserAlreadyMember(String email, Project project) {
	    // Check if user is the project owner
	    if (project.getOwner() != null && email.equals(project.getOwner().getEmail())) {
	        return true;
	    }
	    
	    // Check if user is in the project team
	    return project.getTeam().stream()
	        .anyMatch(user -> email.equals(user.getEmail()));
	}
	@Override
	public Invitation acceptInvitation(String token, Long userId) throws InvalidTokenException {
	    Invitation invitation = this.invitationRepository.findByToken(token);
	    
	    if (invitation == null) {
	        throw new InvalidTokenException("Invalid token. Please provide a valid token.");
	    }
	    
	    // Check if invitation is already accepted
	    if (invitation.isAccepted()) {
	        throw new InvalidTokenException("Invitation has already been accepted.");
	    }
	    
	    // Check if invitation has expired
	    if (invitation.getTokenExpiration() != null && 
	            invitation.getTokenExpiration().isBefore(LocalDateTime.now())) {
	            throw new InvalidTokenException("Invitation has expired.");
	     }
	    User user = this.userService.findUserByEmail(invitation.getEmail());
	    
	        
	    
	    // Ensure only the invited user can accept the invitation
	    if (!user.getUserId().equals(userId)) {
	        throw new InvalidTokenException("You are not authorized to accept this invitation.");
	    }
	    
	    // Mark invitation as accepted
	    invitation.setAccepted(true);
	    invitation.setAcceptedDate(LocalDate.now());
	    
	    // Save the updated invitation
	    Invitation savedInvitation = invitationRepository.save(invitation);
	    
	    // Send confirmation email to the user
	    try {
	        sendAcceptanceConfirmationEmail(savedInvitation);
	    } catch (Exception e) {
	        // Log the error but don't fail the invitation acceptance
	        logger.error("Failed to send acceptance confirmation email for invitation: " + 
	                    savedInvitation.getId(), e);
	    }
	    
	    return savedInvitation;
	}

	private void sendAcceptanceConfirmationEmail(Invitation invitation) {
	    // Get user details
	    User user = this.userService.findUserByEmail(invitation.getEmail());
	    if(user==null) throw new ResourceNotFoundException("User not found with " + invitation.getEmail());
	    // Create email content
	    String subject = "Invitation Accepted Successfully";
	    String body = "Dear " + user.getUsername() + ",\n\n" +
	            "Your invitation has been successfully accepted.\n\n" +
	            "Invitation Details:\n" +
	            "- Accepted on: " + invitation.getAcceptedDate() + "\n" +
	            "Welcome aboard!\n\n" +
	            "Best regards,\n" +
	            "Your Team";
	    
	    SimpleMailMessage message = new SimpleMailMessage();
	    
	    // Send email
	    message.setSubject(subject);
	    message.setTo(invitation.getEmail());
	    message.setText(body);
	    this.javaMailSender.send(message);
	}
	@Override
	public String getTokenByuserMail(String userEmail) {
		// TODO Auto-generated method stub
		Invitation invitation = this.invitationRepository.findByEmail(userEmail);
		if(invitation==null) {
			throw new ResourceNotFoundException("User Not foubd with provided email");
		}
		return invitation.getToken();
	}

	@Override
	public void deleteToken(String token) {
		this.invitationRepository.deleteByToken(token);
		
	}

}
