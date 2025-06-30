package com.projectManagementApp.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Subscription {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private LocalDate startedSubscriptionDate;
	private LocalDate subscriptionEndDate;
	
	private PlanType planType;
	
	private boolean isValid;
	
	
	@OneToOne
	private User user;


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public LocalDate getStartedSubscriptionDate() {
		return startedSubscriptionDate;
	}


	public void setStartedSubscriptionDate(LocalDate startedSubscriptionDate) {
		this.startedSubscriptionDate = startedSubscriptionDate;
	}


	public LocalDate getSubscriptionEndDate() {
		return subscriptionEndDate;
	}


	public void setSubscriptionEndDate(LocalDate subscriptionEndDate) {
		this.subscriptionEndDate = subscriptionEndDate;
	}


	public PlanType getPlanType() {
		return planType;
	}


	public void setPlanType(PlanType planType) {
		this.planType = planType;
	}


	public boolean isValid() {
		return isValid;
	}


	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	@Override
	public String toString() {
		return "Subscription [id=" + id + ", startedSubscriptionDate=" + startedSubscriptionDate
				+ ", subscriptionEndDate=" + subscriptionEndDate + ", planType=" + planType + ", isValid=" + isValid
				+ ", user=" + user + "]";
	}
	
	

}
