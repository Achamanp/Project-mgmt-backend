package com.projectManagementApp.payloads;

public class ResetPasswordRequest {
    
    private Integer otp;
    private String newPassword;
    
    public ResetPasswordRequest() {}
    
    public ResetPasswordRequest(Integer otp, String newPassword) {
        
        this.otp = otp;
        this.newPassword = newPassword;
    }
    
   
    public Integer getOtp() { 
        return otp; 
    }
    
    public void setOtp(Integer otp) { 
        this.otp = otp; 
    }
    
    public String getNewPassword() { 
        return newPassword; 
    }
    
    public void setNewPassword(String newPassword) { 
        this.newPassword = newPassword; 
    }
}