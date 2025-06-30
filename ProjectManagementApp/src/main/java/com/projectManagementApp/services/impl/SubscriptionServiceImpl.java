package com.projectManagementApp.services.impl;


import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectManagementApp.entities.PlanType;
import com.projectManagementApp.entities.Subscription;
import com.projectManagementApp.entities.User;
import com.projectManagementApp.repositories.SubscriptionRepository;
import com.projectManagementApp.services.SubscriptionService;

@Service
public class SubscriptionServiceImpl implements SubscriptionService{
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Override
	public Subscription createSubscripton(User user) {
		// TODO Auto-generated method stub
		Subscription subscription = new Subscription();
		subscription.setStartedSubscriptionDate(LocalDate.now());
		subscription.setSubscriptionEndDate(LocalDate.now().plusMonths(12));
		subscription.setValid(true);
		subscription.setPlanType(PlanType.FREE);
		subscription.setUser(user);
		
		
		return this.subscriptionRepository.save(subscription);
	}

	@Override
	public Subscription getUserSubscription(Long userId) {
		// TODO Auto-generated method stub
		
		Subscription subscription = this.subscriptionRepository.findByUserId(userId);
		
		
		
		if(!isValid(subscription)) {
			subscription.setPlanType(PlanType.FREE);
			subscription.setSubscriptionEndDate(LocalDate.now().plusMonths(12));
			subscription.setStartedSubscriptionDate(LocalDate.now());
		}
		return subscriptionRepository.save(subscription);
	}

	@Override
	public Subscription upgradeSubscription(Long userId, PlanType planType) {
		Subscription subscription = this.subscriptionRepository.findByUserId(userId);
		subscription.setPlanType(planType);
		subscription.setStartedSubscriptionDate(LocalDate.now());
		if(planType.equals(PlanType.ANNUALLY)) {
			subscription.setSubscriptionEndDate(LocalDate.now().plusMonths(12));
		}else {
			subscription.setSubscriptionEndDate(LocalDate.now().plusMonths(1));
		}
		return this.subscriptionRepository.save(subscription);
	}

	@Override
	public boolean isValid(Subscription subscription) {
		if(subscription.getPlanType().equals(PlanType.FREE)) {
			return true;
		}
		LocalDate endDate = subscription.getSubscriptionEndDate();
		LocalDate currentDate = LocalDate.now();
		return endDate.isAfter(currentDate) || endDate.isEqual(currentDate);
	}



}
