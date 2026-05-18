package com.cloudpulse.backend.service;

import com.cloudpulse.backend.entity.Incident;
import com.cloudpulse.backend.entity.IncidentSeverity;
import com.cloudpulse.backend.entity.IncidentStatus;
import com.cloudpulse.backend.repository.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentDetectionServiceTest {

    @Mock
    private PrometheusService prometheusService;

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private AiAnalysisService aiAnalysisService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private NotificationService notificationService;

    private IncidentDetectionService incidentDetectionService;

    @BeforeEach
    void setUp() {
        incidentDetectionService = new IncidentDetectionService(
                prometheusService,
                incidentRepository,
                aiAnalysisService,
                messagingTemplate,
                notificationService
        );
    }

    @Test
    void monitorTelemetryMetrics_CpuSpikes_TriggersNewIncident() {
        when(prometheusService.getCpuUsage()).thenReturn(85.5);
        when(prometheusService.getMemoryUsage()).thenReturn(70.0);
        when(prometheusService.getErrorRate()).thenReturn(0.0);

        when(incidentRepository.findFirstByServiceNameAndMetricTypeAndStatus(
                "cloudpulse-demo-service", "CPU", IncidentStatus.ACTIVE))
                .thenReturn(Optional.empty());

        when(aiAnalysisService.generateIncidentAnalysis("cloudpulse-demo-service", "CPU", 85.5))
                .thenReturn("Fact: CPU is at 85.5%");

        incidentDetectionService.monitorTelemetryMetrics();

        verify(incidentRepository, times(1)).save(any(Incident.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/incidents"), eq("REFRESH"));
    }

    @Test
    void monitorTelemetryMetrics_CpuRecovers_AutoResolvesActiveIncident() {
        when(prometheusService.getCpuUsage()).thenReturn(45.0);
        when(prometheusService.getMemoryUsage()).thenReturn(70.0);
        when(prometheusService.getErrorRate()).thenReturn(0.0);

        Incident activeIncident = Incident.builder()
                .serviceName("cloudpulse-demo-service")
                .metricType("CPU")
                .status(IncidentStatus.ACTIVE)
                .detectedAt(LocalDateTime.now())
                .build();

        when(incidentRepository.findFirstByServiceNameAndMetricTypeAndStatus(
                "cloudpulse-demo-service", "CPU", IncidentStatus.ACTIVE))
                .thenReturn(Optional.of(activeIncident));

        incidentDetectionService.monitorTelemetryMetrics();

        verify(incidentRepository, times(1)).save(argThat(incident -> 
                incident.getStatus() == IncidentStatus.RESOLVED && 
                incident.getResolvedAt() != null
        ));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/incidents"), eq("REFRESH"));
    }
}
