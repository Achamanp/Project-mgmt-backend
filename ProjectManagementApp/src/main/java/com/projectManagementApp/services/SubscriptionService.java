package com.projectManagementApp.services;


import com.projectManagementApp.entities.PlanType;
import com.projectManagementApp.entities.Subscription;
import com.projectManagementApp.entities.User;

public interface SubscriptionService {
	Subscription createSubscripton(User user);
	
	Subscription getUserSubscription(Long userId);
	
	Subscription upgradeSubscription(Long userId, PlanType planType);
	
	boolean isValid(Subscription subscription);

}
