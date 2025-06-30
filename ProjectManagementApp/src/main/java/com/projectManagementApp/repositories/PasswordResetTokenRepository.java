package com.projectManagementApp.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projectManagementApp.entities.PasswordResetToken;


@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long>{
	PasswordResetToken findByOtp(Integer otp);
	@Query("SELECT e FROM PasswordResetToken e WHERE e.expirationTime < :now")
	List<PasswordResetToken> findAllExpiredTokens(@Param("now") LocalDateTime now);
	
	 void deleteAllByExpirationTimeBefore(LocalDateTime now);
	PasswordResetToken findByToken(String token);
}
