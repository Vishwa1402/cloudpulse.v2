package com.cloudpulse.backend.service;

import com.cloudpulse.backend.entity.*;
import com.cloudpulse.backend.repository.*;
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

    // Mock new dependencies
    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private EnvironmentRepository environmentRepository;
    @Mock
    private ServiceRepository serviceRepository;
    @Mock
    private AlertRepository alertRepository;
    @Mock
    private AlertRuleRepository alertRuleRepository;
    @Mock
    private ServiceMetricRepository serviceMetricRepository;
    @Mock
    private ServiceHealthRepository serviceHealthRepository;
    @Mock
    private IncidentStatusHistoryRepository incidentStatusHistoryRepository;
    @Mock
    private IncidentEventRepository incidentEventRepository;

    private IncidentDetectionService incidentDetectionService;
    
    private com.cloudpulse.backend.entity.Service mockService;
    private Organization mockOrg;
    private Environment mockEnv;
    private Project mockProject;

    @BeforeEach
    void setUp() {
        incidentDetectionService = new IncidentDetectionService(
                prometheusService,
                incidentRepository,
                aiAnalysisService,
                messagingTemplate,
                notificationService,
                organizationRepository,
                projectRepository,
                environmentRepository,
                serviceRepository,
                alertRepository,
                alertRuleRepository,
                serviceMetricRepository,
                serviceHealthRepository,
                incidentStatusHistoryRepository,
                incidentEventRepository
        );

        // Prepare default mock data for bootstrapping
        mockOrg = Organization.builder().id(1L).name("Default Organization").build();
        mockEnv = Environment.builder().id(1L).name("PRODUCTION").organization(mockOrg).build();
        mockProject = Project.builder().id(1L).name("Default Project").organization(mockOrg).build();
        mockService = com.cloudpulse.backend.entity.Service.builder()
                .id(1L)
                .name("cloudpulse-demo-service")
                .project(mockProject)
                .environment(mockEnv)
                .build();
    }

    private void stubBootstrapper() {
        lenient().when(organizationRepository.findByName("Default Organization")).thenReturn(Optional.of(mockOrg));
        lenient().when(environmentRepository.findByNameAndOrganization("PRODUCTION", mockOrg)).thenReturn(Optional.of(mockEnv));
        lenient().when(projectRepository.findByName("Default Project")).thenReturn(Optional.of(mockProject));
        lenient().when(serviceRepository.findByName("cloudpulse-demo-service")).thenReturn(Optional.of(mockService));
    }

    @Test
    void monitorTelemetryMetrics_CpuSpikes_TriggersNewIncident() {
        stubBootstrapper();

        when(prometheusService.getCpuUsage()).thenReturn(85.5);
        when(prometheusService.getMemoryUsage()).thenReturn(70.0);
        when(prometheusService.getErrorRate()).thenReturn(0.0);

        when(incidentRepository.findFirstByServiceAndAlertMetricTypeAndStatus(
                mockService, "CPU", IncidentStatus.ACTIVE))
                .thenReturn(Optional.empty());

        AlertRule mockRule = AlertRule.builder().id(1L).metric("CPU").threshold(80.0).service(mockService).build();
        when(alertRuleRepository.findByMetricAndActive("CPU", true)).thenReturn(Optional.of(mockRule));

        Alert mockAlert = Alert.builder().id(1L).alertRule(mockRule).service(mockService).metricType("CPU").currentValue(85.5).build();
        when(alertRepository.save(any(Alert.class))).thenReturn(mockAlert);

        when(aiAnalysisService.generateIncidentAnalysis("cloudpulse-demo-service", "CPU", 85.5))
                .thenReturn("Fact: CPU is at 85.5%");

        incidentDetectionService.monitorTelemetryMetrics();

        verify(incidentRepository, times(1)).save(any(Incident.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/incidents"), eq("REFRESH"));
    }

    @Test
    void monitorTelemetryMetrics_CpuRecovers_AutoResolvesActiveIncident() {
        stubBootstrapper();

        when(prometheusService.getCpuUsage()).thenReturn(45.0);
        when(prometheusService.getMemoryUsage()).thenReturn(70.0);
        when(prometheusService.getErrorRate()).thenReturn(0.0);

        AlertRule mockRule = AlertRule.builder().id(1L).metric("CPU").threshold(80.0).service(mockService).build();
        Alert mockAlert = Alert.builder().id(1L).alertRule(mockRule).service(mockService).metricType("CPU").currentValue(85.5).build();

        Incident activeIncident = Incident.builder()
                .service(mockService)
                .alert(mockAlert)
                .status(IncidentStatus.ACTIVE)
                .detectedAt(LocalDateTime.now())
                .build();

        when(incidentRepository.findFirstByServiceAndAlertMetricTypeAndStatus(
                mockService, "CPU", IncidentStatus.ACTIVE))
                .thenReturn(Optional.of(activeIncident));

        incidentDetectionService.monitorTelemetryMetrics();

        verify(incidentRepository, times(1)).save(argThat(incident -> 
                incident.getStatus() == IncidentStatus.RESOLVED && 
                incident.getResolvedAt() != null
        ));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/incidents"), eq("REFRESH"));
    }
}
