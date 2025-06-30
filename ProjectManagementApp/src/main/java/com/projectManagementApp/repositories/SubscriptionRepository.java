package com.projectManagementApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projectManagementApp.entities.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long>{
	
	    @Query("SELECT s FROM Subscription s WHERE s.user.userId = :userId")
	    Subscription findByUserId(@Param("userId") Long userId);
	
}
