package com.projectManagementApp.controllers;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.projectManagementApp.entities.PlanType;
import com.projectManagementApp.entities.User;
import com.projectManagementApp.payloads.PaymentLinkResponse;
import com.projectManagementApp.services.UserService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    
    @Value("${razorpay.api.key}")
    private String apiKey;
    
    @Value("${razorpay.api.secret}")
    private String apiSecret;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/{planType}")
    @CrossOrigin("https://spontaneous-chaja-2084a3.netlify.app/","https://splendorous-zuccutto-0b577e.netlify.app/")
    public ResponseEntity<PaymentLinkResponse> createPaymentLink(
            @PathVariable PlanType planType,
            @RequestHeader("Authorization") String jwt) {
        
        try {
            // Get user from JWT
            User user = this.userService.findUserProfileByJwt();
            
            // Calculate amount in paise (Razorpay uses paise, not rupees)
            int amount = 799 * 100; // 799 rupees = 79900 paise
            
            if (planType.equals(PlanType.ANNUALLY)) {
                amount = amount * 12; // 12 months
                amount = (int)(amount * 0.7); // 30% discount
            }
            
            // Create Razorpay client
            RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);
            
            // Create payment link request
            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", amount);
            paymentLinkRequest.put("currency", "INR");
            
            // Add customer details
            JSONObject customer = new JSONObject();
            customer.put("name", user.getUsername());
            customer.put("email", user.getEmail());
            paymentLinkRequest.put("customer", customer);
            
            // Add notification settings
            JSONObject notify = new JSONObject();
            notify.put("email", true);
            notify.put("sms", false);
            paymentLinkRequest.put("notify", notify);
            
            // Add callback URL with proper query parameter
            paymentLinkRequest.put("callback_url", 
                "http://localhost:5173/upgrade_plan/success?planType=" + planType);
            
            // Add description
            paymentLinkRequest.put("description", "Subscription for " + planType + " plan");
            
            // Create payment link
            PaymentLink payment = razorpayClient.paymentLink.create(paymentLinkRequest);
            
            String paymentLinkId = payment.get("id");
            String paymentLinkUrl = payment.get("short_url");
            
            // Create response
            PaymentLinkResponse paymentLinkResponse = new PaymentLinkResponse();
            paymentLinkResponse.setPayment_link_id(paymentLinkId);
            paymentLinkResponse.setPayment_link_url(paymentLinkUrl);
            
            return new ResponseEntity<>(paymentLinkResponse, HttpStatus.CREATED);
            
        } catch (Exception e) {
            e.printStackTrace(); // Log the actual error for debugging
            
            PaymentLinkResponse errorResponse = new PaymentLinkResponse();
            errorResponse.setPayment_link_url("Unable to create payment link at the moment. Please try again later.");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
}
