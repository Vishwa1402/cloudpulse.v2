package com.cloudpulse.backend.controller;

import com.cloudpulse.backend.entity.Incident;
import com.cloudpulse.backend.entity.IncidentStatus;
import com.cloudpulse.backend.repository.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentControllerTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private com.cloudpulse.backend.service.AuditLogService auditLogService;

    @Mock
    private com.cloudpulse.backend.service.NotificationService notificationService;

    private IncidentController incidentController;

    @BeforeEach
    void setUp() {
        incidentController = new IncidentController(incidentRepository, messagingTemplate, auditLogService, notificationService);
    }

    @Test
    void getAllIncidents_ReturnsListOfIncidents() {
        List<Incident> sampleList = List.of(
                Incident.builder().serviceName("cloudpulse-demo-service").build()
        );
        when(incidentRepository.findAllByOrderByDetectedAtDesc()).thenReturn(sampleList);

        ResponseEntity<List<Incident>> response = incidentController.getAllIncidents();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void resolveIncident_ActiveIncident_ResolvesAndBroadcasts() {
        Incident activeIncident = Incident.builder()
                .id(1L)
                .serviceName("cloudpulse-demo-service")
                .status(IncidentStatus.ACTIVE)
                .build();
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(activeIncident));

        ResponseEntity<?> response = incidentController.resolveIncident(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(IncidentStatus.RESOLVED, activeIncident.getStatus());
        verify(incidentRepository, times(1)).save(activeIncident);
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/incidents"), eq("REFRESH"));
    }

    @Test
    void resolveIncident_NonExistent_ReturnsNotFound() {
        when(incidentRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = incidentController.resolveIncident(99L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
