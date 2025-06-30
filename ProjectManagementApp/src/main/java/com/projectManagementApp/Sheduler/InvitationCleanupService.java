package com.projectManagementApp.Sheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.projectManagementApp.entities.Invitation;
import com.projectManagementApp.entities.PasswordResetToken;
import com.projectManagementApp.repositories.InvitationRepository;
import com.projectManagementApp.repositories.PasswordResetTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class InvitationCleanupService {
    
    private static final Logger logger = LoggerFactory.getLogger(InvitationCleanupService.class);
    
    @Autowired
    private InvitationRepository invitationRepository;
    
    
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    /**
     * Single method: Mark expired and delete immediately
     * Runs every minute
     */
	@Scheduled(fixedRate = 60000) 
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // Step 1: Mark expired tokens
            int markedCount = invitationRepository.markExpiredTokens(now);
            
            // Step 2: Delete all expired tokens immediately
            int deletedCount = invitationRepository.deleteAllExpiredTokens();
            
            if (markedCount > 0 || deletedCount > 0) {
                logger.info("Cleanup completed - Marked: {}, Deleted: {} invitation tokens", 
                           markedCount, deletedCount);
            }
        } catch (Exception e) {
            logger.error("Error occurred during token cleanup", e);
        }
    }
    

    
    /**
     * Alternative method: Find and process expired tokens individually
     * This method is disabled - use cleanupExpiredTokens() instead
     */
    // @Scheduled(fixedDelay = 60000) // Commented out to avoid duplicate processing
    @Transactional
    public void processExpiredTokensIndividually() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Invitation> expiredTokens = invitationRepository.findExpiredTokens(now);
            
            for (Invitation invitation : expiredTokens) {
                // Mark as expired
                invitation.setExpired(true);
                invitationRepository.save(invitation);
                
                // You can add additional logic here if needed
                // For example: send notification, log specific details, etc.
                logger.debug("Marked invitation token {} as expired for email: {}", 
                           invitation.getToken(), invitation.getEmail());
            }
            
            if (!expiredTokens.isEmpty()) {
                logger.info("Processed {} expired invitation tokens", expiredTokens.size());
            }
        } catch (Exception e) {
            logger.error("Error occurred while processing expired tokens individually", e);
        }
    }
    
    @Scheduled(fixedRate = 60000) 
	public void deleteExpiredOtpsForForgottedPassword() {
		LocalDateTime now = LocalDateTime.now();
		List<PasswordResetToken> expiredOtps = this.passwordResetTokenRepository.findAllExpiredTokens(now);
		if(expiredOtps!=null) {
			this.passwordResetTokenRepository.deleteAll(expiredOtps);
    		System.out.println("Deleted expired OTP tokens at: " + now);
		}
	}
	
}
