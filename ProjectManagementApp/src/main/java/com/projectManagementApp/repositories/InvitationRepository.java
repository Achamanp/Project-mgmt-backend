package com.projectManagementApp.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projectManagementApp.entities.Invitation;


@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long>{
	Invitation findByToken(String token);
	
	Invitation findByEmail(String userEmail);

	void deleteByToken(String token);
	
	Optional<Invitation> findByEmailAndProjectIdAndIsExpiredFalseAndIsAcceptedFalse(String email, Long projectId);
	
	    
	 @Query("SELECT i FROM Invitation i WHERE i.tokenExpiration < :currentTime AND i.isExpired = false")
	    List<Invitation> findExpiredTokens(@Param("currentTime") LocalDateTime currentTime);
	    
	    @Modifying
	    @Query("UPDATE Invitation i SET i.isExpired = true WHERE i.tokenExpiration < :currentTime AND i.isExpired = false")
	    int markExpiredTokens(@Param("currentTime") LocalDateTime currentTime);
	    
	    @Modifying
	    @Query("DELETE FROM Invitation i WHERE i.isExpired = true")
	    int deleteAllExpiredTokens();
	    
	    @Modifying
	    @Query("DELETE FROM Invitation i WHERE i.isExpired = true AND i.tokenExpiration < :cutoffTime")
	    int deleteExpiredTokens(@Param("cutoffTime") LocalDateTime cutoffTime);
}
