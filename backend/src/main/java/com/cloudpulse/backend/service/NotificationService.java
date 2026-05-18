package com.cloudpulse.backend.service;

import com.cloudpulse.backend.entity.Incident;
import com.cloudpulse.backend.entity.Notification;
import com.cloudpulse.backend.entity.NotificationChannel;
import com.cloudpulse.backend.repository.NotificationChannelRepository;
import com.cloudpulse.backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationChannelRepository channelRepo;

    @Autowired
    private NotificationRepository notificationRepo;

    @Autowired
    private EmailService emailService;

    @Async
    public void dispatch(Incident incident, String eventType) {
        List<NotificationChannel> channels = channelRepo.findAll();
        if (channels.isEmpty()) {
            return;
        }

        String alertMessage = String.format(
                "[CLOUDPULSE ALERT - %s]\n" +
                "- Incident ID: #%d\n" +
                "- Service Name: %s\n" +
                "- Metric: %s (Value: %.2f)\n" +
                "- Severity: %s\n" +
                "- Description: %s\n" +
                "- Status: %s",
                eventType.toUpperCase(),
                incident.getId(),
                incident.getServiceName(),
                incident.getMetricType(),
                incident.getMetricValue() != null ? incident.getMetricValue() : 0.0,
                incident.getSeverity().name(),
                incident.getDescription(),
                incident.getStatus().name()
        );

        for (NotificationChannel channel : channels) {
            String status = "SENT";
            try {
                if ("EMAIL".equalsIgnoreCase(channel.getType())) {
                    emailService.sendEmail(
                            channel.getConfig(),
                            "CloudPulse Incident Notification - #" + incident.getId(),
                            alertMessage
                    );
                } else if ("SLACK".equalsIgnoreCase(channel.getType()) || "SLACK_WEBHOOK".equalsIgnoreCase(channel.getType())) {
                    System.out.println(">>> Simulated dispatch to Slack Webhook [" + channel.getConfig() + "]: " + alertMessage);
                } else {
                    System.out.println(">>> Simulated dispatch to webhook [" + channel.getConfig() + "]: " + alertMessage);
                }
            } catch (Exception e) {
                status = "FAILED";
                System.err.println("Failed to dispatch alert to channel " + channel.getName() + ": " + e.getMessage());
            }

            Notification notification = Notification.builder()
                    .incident(incident)
                    .message(alertMessage)
                    .status(status)
                    .build();
            notificationRepo.save(notification);
        }
    }
}
