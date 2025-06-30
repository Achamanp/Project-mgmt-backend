package com.projectManagementApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projectManagementApp.entities.PlanType;
import com.projectManagementApp.entities.Subscription;
import com.projectManagementApp.entities.User;
import com.projectManagementApp.payloads.ApiResponse;
import com.projectManagementApp.services.SubscriptionService;
import com.projectManagementApp.services.UserService;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Subscription>> createSubscription() {
        try {
            User user = userService.findUserProfileByJwt();
            Subscription subscription = subscriptionService.createSubscripton(user);

            ApiResponse<Subscription> response = new ApiResponse<>();
            response.setMessage("Subscription created successfully.");
            response.setSuccess(true);
            response.setData(subscription);

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            ApiResponse<Subscription> response = new ApiResponse<>();
            response.setMessage("Failed to create subscription: " + ex.getMessage());
            response.setSuccess(false);
            response.setData(null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Subscription>> getUserSubscription() {
        try {
            User user = userService.findUserProfileByJwt();
            Subscription subscription = subscriptionService.getUserSubscription(user.getUserId());
            System.out.println("printing Subscription data" + subscription.toString());
            ApiResponse<Subscription> response = new ApiResponse<>();
            response.setMessage("Subscription retrieved successfully.");
            response.setSuccess(true);
            response.setData(subscription);

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            ApiResponse<Subscription> response = new ApiResponse<>();
            response.setMessage("Failed to retrieve subscription: " + ex.getMessage());
            response.setSuccess(false);
            response.setData(null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/upgrade")
    public ResponseEntity<ApiResponse<Subscription>> upgradeSubscription(
            @RequestParam PlanType planType) {
       try {
            User user = userService.findUserProfileByJwt();
            Subscription subscription = subscriptionService.upgradeSubscription(user.getUserId(), planType);

            ApiResponse<Subscription> response = new ApiResponse<>();
            response.setMessage("Subscription upgraded to " + planType + ".");
            response.setSuccess(true);
            response.setData(subscription);

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            ApiResponse<Subscription> response = new ApiResponse<>();
            response.setMessage("Failed to upgrade subscription: " + ex.getMessage());
            response.setSuccess(false);
            response.setData(null);
            return ResponseEntity.status(500).body(response);
        }
    }
}
