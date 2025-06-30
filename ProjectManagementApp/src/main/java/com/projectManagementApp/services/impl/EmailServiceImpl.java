package com.projectManagementApp.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.projectManagementApp.services.EmailService;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendEmailWithTokken(String userEmail, String link) {
        try {
            MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String subject = "Join Project Team Invitation";
            
            // HTML email template
            String htmlText = """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                        <h2 style="color: #2c3e50; text-align: center;">Project Team Invitation</h2>
                        
                        <p>Hello,</p>
                        
                        <p>You have been invited to join a project team. Click the button below to accept the invitation:</p>
                        
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background-color: #3498db; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;">
                                Join Project Team
                            </a>
                        </div>
                        
                        <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 5px; padding: 15px; margin: 20px 0;">
                            <p style="margin: 0; color: #856404;">
                                <strong>⏰ Important:</strong> This invitation link is valid for <strong>10 minutes only</strong>. 
                                After 10 minutes, the link will expire and you will need to request a new invitation.
                            </p>
                        </div>
                        
                        <p>If the button doesn't work, you can copy and paste this link into your browser:</p>
                        <p style="word-break: break-all; background-color: #f8f9fa; padding: 10px; border-radius: 5px; font-family: monospace;">
                            %s
                        </p>
                        
                        <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0;">
                        
                        <p style="font-size: 12px; color: #666; text-align: center;">
                            If you didn't expect this invitation, you can safely ignore this email.
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(link, link);

            helper.setTo(userEmail);
            helper.setSubject(subject);
            helper.setText(htmlText, true); // true = HTML content

            this.javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new MailSendException("Failed to send mail: " + e.getMessage());
        }
    }
}

