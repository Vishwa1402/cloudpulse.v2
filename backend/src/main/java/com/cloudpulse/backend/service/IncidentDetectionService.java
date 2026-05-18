package com.cloudpulse.backend.service;

import com.cloudpulse.backend.entity.*;
import com.cloudpulse.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IncidentDetectionService {

    private final PrometheusService prometheusService;
    private final IncidentRepository incidentRepository;
    private final AiAnalysisService aiAnalysisService;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    // New Repositories injected for normalized schema bootstrapping and tracking
    private final OrganizationRepository organizationRepository;
    private final ProjectRepository projectRepository;
    private final EnvironmentRepository environmentRepository;
    private final ServiceRepository serviceRepository;
    private final AlertRepository alertRepository;
    private final AlertRuleRepository alertRuleRepository;
    private final ServiceMetricRepository serviceMetricRepository;
    private final ServiceHealthRepository serviceHealthRepository;
    private final IncidentStatusHistoryRepository incidentStatusHistoryRepository;
    private final IncidentEventRepository incidentEventRepository;

    private static final String SERVICE_NAME = "cloudpulse-demo-service";

    // Thresholds
    private static final double CPU_THRESHOLD = 80.0;
    private static final double MEMORY_THRESHOLD = 90.0;
    private static final double ERROR_RATE_THRESHOLD = 1.0;

    /**
     * Periodically monitors system metrics every 5 seconds.
     * Evaluates against thresholds, triggers alerts, and closes resolved items.
     */
    @Scheduled(fixedRate = 5000)
    @Transactional
    public void monitorTelemetryMetrics() {
        log.debug("Incident monitor cycle started...");

        try {
            // Ensure bootstrap data is fully present in database (zero-downtime, fully compatible)
            com.cloudpulse.backend.entity.Service dbService = bootstrapDefaultEnvironmentAndService();

            double cpu = prometheusService.getCpuUsage();
            double memory = prometheusService.getMemoryUsage();
            double errorRate = prometheusService.getErrorRate();

            // 1. Ingest metrics to time-series DB for graphs/trends
            saveServiceMetric(dbService, "CPU", cpu);
            saveServiceMetric(dbService, "MEMORY", memory);
            saveServiceMetric(dbService, "ERROR_RATE", errorRate);

            // 2. Track service uptime check
            saveServiceHealthCheck(dbService, cpu < 95.0 ? "UP" : "DEGRADED", cpu * 2.0);

            // 3. Evaluate breaches
            evaluateCpuAnomaly(dbService, cpu);
            evaluateMemoryAnomaly(dbService, memory);
            evaluateErrorRateAnomaly(dbService, errorRate);

        } catch (Exception e) {
            log.error("Error during scheduled telemetry scrape cycle: {}", e.getMessage());
        }
    }

    private com.cloudpulse.backend.entity.Service bootstrapDefaultEnvironmentAndService() {
        // Fetch or create default organization
        Organization org = organizationRepository.findByName("Default Organization")
                .orElseGet(() -> organizationRepository.save(
                        Organization.builder().name("Default Organization").build()
                ));

        // Fetch or create default environment
        Environment env = environmentRepository.findByNameAndOrganization("PRODUCTION", org)
                .orElseGet(() -> environmentRepository.save(
                        Environment.builder()
                                .name("PRODUCTION")
                                .organization(org)
                                .build()
                ));

        // Fetch or create default project
        Project project = projectRepository.findByName("Default Project")
                .orElseGet(() -> projectRepository.save(
                        Project.builder()
                                .name("Default Project")
                                .organization(org)
                                .environmentType("PRODUCTION")
                                .build()
                ));

        // Fetch or create default service
        return serviceRepository.findByName(SERVICE_NAME)
                .orElseGet(() -> serviceRepository.save(
                        com.cloudpulse.backend.entity.Service.builder()
                                .name(SERVICE_NAME)
                                .project(project)
                                .environment(env)
                                .status("ACTIVE")
                                .metricsUrl("http://localhost:8080/actuator/prometheus")
                                .healthUrl("http://localhost:8080/actuator/health")
                                .description("Primary production scraping target for CloudPulse command console.")
                                .build()
                ));
    }

    private void saveServiceMetric(com.cloudpulse.backend.entity.Service service, String metricType, double value) {
        try {
            serviceMetricRepository.save(
                    ServiceMetric.builder()
                            .service(service)
                            .metricType(metricType)
                            .metricValue(value)
                            .collectedAt(LocalDateTime.now())
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to log service time-series metric: {}", e.getMessage());
        }
    }

    private void saveServiceHealthCheck(com.cloudpulse.backend.entity.Service service, String status, double responseTimeMs) {
        try {
            serviceHealthRepository.save(
                    ServiceHealth.builder()
                            .service(service)
                            .healthStatus(status)
                            .responseTime(responseTimeMs)
                            .checkedAt(LocalDateTime.now())
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to record service health check: {}", e.getMessage());
        }
    }

    private AlertRule getOrCreateAlertRule(com.cloudpulse.backend.entity.Service service, String metric, double threshold) {
        return alertRuleRepository.findByMetricAndActive(metric, true)
                .orElseGet(() -> alertRuleRepository.save(
                        AlertRule.builder()
                                .name("Critical " + metric + " breach threshold")
                                .metric(metric)
                                .threshold(threshold)
                                .comparisonOperator("GREATER_THAN")
                                .durationSeconds(60)
                                .service(service)
                                .active(true)
                                .build()
                ));
    }

    private void evaluateCpuAnomaly(com.cloudpulse.backend.entity.Service service, double cpu) {
        Optional<Incident> activeCpuIncident = incidentRepository
                .findFirstByServiceAndAlertMetricTypeAndStatus(service, "CPU", IncidentStatus.ACTIVE);

        if (cpu >= CPU_THRESHOLD) {
            if (activeCpuIncident.isEmpty()) {
                // Get or create Alert Rule
                AlertRule rule = getOrCreateAlertRule(service, "CPU", CPU_THRESHOLD);

                // Create Alert
                Alert alert = alertRepository.save(
                        Alert.builder()
                                .alertRule(rule)
                                .service(service)
                                .metricType("CPU")
                                .thresholdValue(CPU_THRESHOLD)
                                .currentValue(cpu)
                                .status("TRIGGERED")
                                .active(true)
                                .build()
                );

                // Trigger CPU incident
                String explanation = aiAnalysisService.generateIncidentAnalysis(SERVICE_NAME, "CPU", cpu);
                Incident incident = Incident.builder()
                        .service(service)
                        .alert(alert)
                        .title("Critical CPU saturation breach")
                        .priority("P0")
                        .severity(IncidentSeverity.CRITICAL)
                        .status(IncidentStatus.ACTIVE)
                        .description(String.format("Critical CPU saturation detected at %.2f%%", cpu))
                        .aiSummary(explanation)
                        .detectedAt(LocalDateTime.now())
                        .build();

                incidentRepository.save(incident);

                // Log state transitions and incident timeline details
                logIncidentTimeline(incident, null, "ACTIVE", "Critical CPU Saturation breached baseline threshold of " + CPU_THRESHOLD + "%.");

                log.warn("Auto-detected Incident: CPU spike triggered on service {}", SERVICE_NAME);
                try {
                    notificationService.dispatch(incident, "DETECTED");
                } catch (Exception e) {
                    log.error("Failed to dispatch CPU detection alert: {}", e.getMessage());
                }
                broadcastIncidentUpdate();
            }
        } else {
            if (activeCpuIncident.isPresent()) {
                // Auto-resolve incident
                Incident incident = activeCpuIncident.get();
                incident.setStatus(IncidentStatus.RESOLVED);
                incident.setResolvedAt(LocalDateTime.now());
                incidentRepository.save(incident);

                // Resolve Alert
                if (incident.getAlert() != null) {
                    Alert alert = incident.getAlert();
                    alert.setStatus("RESOLVED");
                    alert.setActive(false);
                    alert.setResolvedAt(LocalDateTime.now());
                    alertRepository.save(alert);
                }

                // Log timeline trace
                logIncidentTimeline(incident, "ACTIVE", "RESOLVED", "CPU metrics recovered to " + String.format("%.2f%%", cpu) + ".");

                log.info("Auto-resolved Incident: CPU load recovered to %.2f%% on service {}", cpu, SERVICE_NAME);
                try {
                    notificationService.dispatch(incident, "RESOLVED");
                } catch (Exception e) {
                    log.error("Failed to dispatch CPU resolution alert: {}", e.getMessage());
                }
                broadcastIncidentUpdate();
            }
        }
    }

    private void evaluateMemoryAnomaly(com.cloudpulse.backend.entity.Service service, double memory) {
        Optional<Incident> activeMemIncident = incidentRepository
                .findFirstByServiceAndAlertMetricTypeAndStatus(service, "MEMORY", IncidentStatus.ACTIVE);

        if (memory >= MEMORY_THRESHOLD) {
            if (activeMemIncident.isEmpty()) {
                // Get or create Alert Rule
                AlertRule rule = getOrCreateAlertRule(service, "MEMORY", MEMORY_THRESHOLD);

                // Create Alert
                Alert alert = alertRepository.save(
                        Alert.builder()
                                .alertRule(rule)
                                .service(service)
                                .metricType("MEMORY")
                                .thresholdValue(MEMORY_THRESHOLD)
                                .currentValue(memory)
                                .status("TRIGGERED")
                                .active(true)
                                .build()
                );

                // Trigger MEMORY incident
                String explanation = aiAnalysisService.generateIncidentAnalysis(SERVICE_NAME, "MEMORY", memory);
                Incident incident = Incident.builder()
                        .service(service)
                        .alert(alert)
                        .title("Near Heap Exhaustion Warning")
                        .priority("P1")
                        .severity(IncidentSeverity.CRITICAL)
                        .status(IncidentStatus.ACTIVE)
                        .description(String.format("JVM Heap/System memory near saturation at %.2f%%", memory))
                        .aiSummary(explanation)
                        .detectedAt(LocalDateTime.now())
                        .build();

                incidentRepository.save(incident);

                // Log state transitions and incident timeline details
                logIncidentTimeline(incident, null, "ACTIVE", "JVM Heap near-saturation reached " + memory + "%.");

                log.warn("Auto-detected Incident: Memory saturation triggered on service {}", SERVICE_NAME);
                try {
                    notificationService.dispatch(incident, "DETECTED");
                } catch (Exception e) {
                    log.error("Failed to dispatch Memory detection alert: {}", e.getMessage());
                }
                broadcastIncidentUpdate();
            }
        } else {
            if (activeMemIncident.isPresent()) {
                // Auto-resolve incident
                Incident incident = activeMemIncident.get();
                incident.setStatus(IncidentStatus.RESOLVED);
                incident.setResolvedAt(LocalDateTime.now());
                incidentRepository.save(incident);

                // Resolve Alert
                if (incident.getAlert() != null) {
                    Alert alert = incident.getAlert();
                    alert.setStatus("RESOLVED");
                    alert.setActive(false);
                    alert.setResolvedAt(LocalDateTime.now());
                    alertRepository.save(alert);
                }

                // Log timeline trace
                logIncidentTimeline(incident, "ACTIVE", "RESOLVED", "Memory allocation stabilized to " + String.format("%.2f%%", memory) + ".");

                log.info("Auto-resolved Incident: Memory allocation stabilized to %.2f%% on service {}", memory, SERVICE_NAME);
                try {
                    notificationService.dispatch(incident, "RESOLVED");
                } catch (Exception e) {
                    log.error("Failed to dispatch Memory resolution alert: {}", e.getMessage());
                }
                broadcastIncidentUpdate();
            }
        }
    }

    private void evaluateErrorRateAnomaly(com.cloudpulse.backend.entity.Service service, double errorRate) {
        Optional<Incident> activeErrIncident = incidentRepository
                .findFirstByServiceAndAlertMetricTypeAndStatus(service, "ERROR_RATE", IncidentStatus.ACTIVE);

        if (errorRate >= ERROR_RATE_THRESHOLD) {
            if (activeErrIncident.isEmpty()) {
                // Get or create Alert Rule
                AlertRule rule = getOrCreateAlertRule(service, "ERROR_RATE", ERROR_RATE_THRESHOLD);

                // Create Alert
                Alert alert = alertRepository.save(
                        Alert.builder()
                                .alertRule(rule)
                                .service(service)
                                .metricType("ERROR_RATE")
                                .thresholdValue(ERROR_RATE_THRESHOLD)
                                .currentValue(errorRate)
                                .status("TRIGGERED")
                                .active(true)
                                .build()
                );

                // Trigger ERROR incident
                String explanation = aiAnalysisService.generateIncidentAnalysis(SERVICE_NAME, "ERROR_RATE", errorRate);
                Incident incident = Incident.builder()
                        .service(service)
                        .alert(alert)
                        .title("High HTTP Error Rates")
                        .priority("P0")
                        .severity(IncidentSeverity.HIGH)
                        .status(IncidentStatus.ACTIVE)
                        .description(String.format("Elevated HTTP Error Rate detected at %.2f%%", errorRate))
                        .aiSummary(explanation)
                        .detectedAt(LocalDateTime.now())
                        .build();

                incidentRepository.save(incident);

                // Log state transitions and incident timeline details
                logIncidentTimeline(incident, null, "ACTIVE", "HTTP Error Rate exceeded 1.0% limit, hitting " + errorRate + "%.");

                log.warn("Auto-detected Incident: High Error Rate triggered on service {}", SERVICE_NAME);
                try {
                    notificationService.dispatch(incident, "DETECTED");
                } catch (Exception e) {
                    log.error("Failed to dispatch Error Rate detection alert: {}", e.getMessage());
                }
                broadcastIncidentUpdate();
            }
        } else {
            if (activeErrIncident.isPresent()) {
                // Auto-resolve incident
                Incident incident = activeErrIncident.get();
                incident.setStatus(IncidentStatus.RESOLVED);
                incident.setResolvedAt(LocalDateTime.now());
                incidentRepository.save(incident);

                // Resolve Alert
                if (incident.getAlert() != null) {
                    Alert alert = incident.getAlert();
                    alert.setStatus("RESOLVED");
                    alert.setActive(false);
                    alert.setResolvedAt(LocalDateTime.now());
                    alertRepository.save(alert);
                }

                // Log timeline trace
                logIncidentTimeline(incident, "ACTIVE", "RESOLVED", "HTTP transaction stability recovered to " + String.format("%.2f%%", errorRate) + ".");

                log.info("Auto-resolved Incident: HTTP error rate dropped to %.2f%% on service {}", errorRate, SERVICE_NAME);
                try {
                    notificationService.dispatch(incident, "RESOLVED");
                } catch (Exception e) {
                    log.error("Failed to dispatch Error Rate resolution alert: {}", e.getMessage());
                }
                broadcastIncidentUpdate();
            }
        }
    }

    private void logIncidentTimeline(Incident incident, String oldStatus, String newStatus, String description) {
        try {
            // Write status history record
            incidentStatusHistoryRepository.save(
                    IncidentStatusHistory.builder()
                            .incident(incident)
                            .oldStatus(oldStatus)
                            .newStatus(newStatus)
                            .changedAt(LocalDateTime.now())
                            .build()
            );

            // Record event logging entry
            incidentEventRepository.save(
                    IncidentEvent.builder()
                            .incident(incident)
                            .eventDescription(description)
                            .createdAt(LocalDateTime.now())
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to write to incident status timeline: {}", e.getMessage());
        }
    }

    private void broadcastIncidentUpdate() {
        try {
            messagingTemplate.convertAndSend("/topic/incidents", "REFRESH");
            log.info("Successfully broadcasted incident refresh payload to STOMP clients.");
        } catch (Exception e) {
            log.error("Failed to broadcast incident websocket update: {}", e.getMessage());
        }
    }
}
