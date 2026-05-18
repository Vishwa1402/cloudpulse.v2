package com.cloudpulse.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendBudgetAlert(String toEmail, String provider, double currentCost, double budgetLimit) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("alerts@cloudpulse.com");
            message.setTo(toEmail);
            message.setSubject("URGENT: CloudPulse Budget Alert for " + provider);
            message.setText("Hello,\n\nYour cloud spending for " + provider + " has exceeded the alert threshold.\n\n" +
                    "Current Cost: $" + currentCost + "\n" +
                    "Budget Limit: $" + budgetLimit + "\n\n" +
                    "Please review your resources to avoid unexpected charges.\n\n" +
                    "Thanks,\nCloudPulse Team");

            javaMailSender.send(message);
            log.info("Budget alert email sent to " + toEmail + " for " + provider);
        } catch (Exception e) {
            log.error("Failed to send budget alert email to " + toEmail + ". Ensure SMTP is configured. Outputting mock email instead:", e);
            log.info("MOCK EMAIL SENT -> To: " + toEmail + " | Subject: URGENT Budget Alert | Cost: $" + currentCost);
        }
    }

    public void sendEmail(String toEmail, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("alerts@cloudpulse.com");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);

            javaMailSender.send(message);
            log.info("Incident notification email successfully dispatched to " + toEmail);
        } catch (Exception e) {
            log.error("Failed to dispatch real SMTP incident notification email to " + toEmail + ". Falling back to console logging: ", e);
            log.info("MOCK EMAIL NOTIFICATION SENT -> To: " + toEmail + " | Subject: " + subject + " | Text: " + text);
        }
    }
}

