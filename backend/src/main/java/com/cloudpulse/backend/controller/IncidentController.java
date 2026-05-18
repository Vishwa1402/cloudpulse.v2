package com.cloudpulse.backend.controller;

import com.cloudpulse.backend.entity.Incident;
import com.cloudpulse.backend.entity.IncidentStatus;
import com.cloudpulse.backend.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class IncidentController {

    private final IncidentRepository incidentRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final com.cloudpulse.backend.service.AuditLogService auditLogService;
    private final com.cloudpulse.backend.service.NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Incident>> getAllIncidents() {
        log.info("Fetching all logged incidents");
        List<Incident> incidents = incidentRepository.findAllByOrderByDetectedAtDesc();
        return ResponseEntity.ok(incidents);
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<?> resolveIncident(@PathVariable Long id) {
        log.info("Manually triggering resolution of incident={}", id);
        
        return incidentRepository.findById(id).map(incident -> {
            if (incident.getStatus() == IncidentStatus.RESOLVED) {
                return ResponseEntity.badRequest().body("Incident is already resolved.");
            }
            
            incident.setStatus(IncidentStatus.RESOLVED);
            incident.setResolvedAt(LocalDateTime.now());
            incidentRepository.save(incident);
            
            // Log to Audit Log
            try {
                auditLogService.log(
                    "INCIDENT_RESOLVE",
                    "OPERATOR",
                    "Incident #" + id + " on service " + incident.getServiceName() + " was manually marked as RESOLVED."
                );
            } catch (Exception e) {
                log.error("Failed to write manual resolution audit log: {}", e.getMessage());
            }

            // Dispatch alert notification
            try {
                notificationService.dispatch(incident, "RESOLVED");
            } catch (Exception e) {
                log.error("Failed to dispatch resolution notification: {}", e.getMessage());
            }

            // Broadcast the refresh
            try {
                messagingTemplate.convertAndSend("/topic/incidents", "REFRESH");
                log.info("Successfully broadcasted manually resolved incident refresh.");
            } catch (Exception e) {
                log.error("Failed to broadcast incident manual resolution websocket update: {}", e.getMessage());
            }
            
            return ResponseEntity.ok(incident);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

