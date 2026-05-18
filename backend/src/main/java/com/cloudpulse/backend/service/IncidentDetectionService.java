package com.cloudpulse.backend.service;

import com.cloudpulse.backend.entity.Incident;
import com.cloudpulse.backend.entity.IncidentSeverity;
import com.cloudpulse.backend.entity.IncidentStatus;
import com.cloudpulse.backend.repository.IncidentRepository;
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
            double cpu = prometheusService.getCpuUsage();
            double memory = prometheusService.getMemoryUsage();
            double errorRate = prometheusService.getErrorRate();

            evaluateCpuAnomaly(cpu);
            evaluateMemoryAnomaly(memory);
            evaluateErrorRateAnomaly(errorRate);

        } catch (Exception e) {
            log.error("Error during scheduled telemetry scrape cycle: {}", e.getMessage());
        }
    }

    private void evaluateCpuAnomaly(double cpu) {
        Optional<Incident> activeCpuIncident = incidentRepository
                .findFirstByServiceNameAndMetricTypeAndStatus(SERVICE_NAME, "CPU", IncidentStatus.ACTIVE);

        if (cpu >= CPU_THRESHOLD) {
            if (activeCpuIncident.isEmpty()) {
                // Trigger CPU incident
                String explanation = aiAnalysisService.generateIncidentAnalysis(SERVICE_NAME, "CPU", cpu);
                Incident incident = Incident.builder()
                        .serviceName(SERVICE_NAME)
                        .metricType("CPU")
                        .metricValue(cpu)
                        .severity(IncidentSeverity.CRITICAL)
                        .status(IncidentStatus.ACTIVE)
                        .description(String.format("Critical CPU saturation detected at %.2f%%", cpu))
                        .aiSummary(explanation)
                        .detectedAt(LocalDateTime.now())
                        .build();

                incidentRepository.save(incident);
                log.warn("Auto-detected Incident: CPU spike triggered on service {}", SERVICE_NAME);
                broadcastIncidentUpdate();
            }
        } else {
            if (activeCpuIncident.isPresent()) {
                // Auto-resolve incident
                Incident incident = activeCpuIncident.get();
                incident.setStatus(IncidentStatus.RESOLVED);
                incident.setResolvedAt(LocalDateTime.now());
                incidentRepository.save(incident);
                log.info("Auto-resolved Incident: CPU load recovered to %.2f%% on service {}", cpu, SERVICE_NAME);
                broadcastIncidentUpdate();
            }
        }
    }

    private void evaluateMemoryAnomaly(double memory) {
        Optional<Incident> activeMemIncident = incidentRepository
                .findFirstByServiceNameAndMetricTypeAndStatus(SERVICE_NAME, "MEMORY", IncidentStatus.ACTIVE);

        if (memory >= MEMORY_THRESHOLD) {
            if (activeMemIncident.isEmpty()) {
                // Trigger MEMORY incident
                String explanation = aiAnalysisService.generateIncidentAnalysis(SERVICE_NAME, "MEMORY", memory);
                Incident incident = Incident.builder()
                        .serviceName(SERVICE_NAME)
                        .metricType("MEMORY")
                        .metricValue(memory)
                        .severity(IncidentSeverity.CRITICAL)
                        .status(IncidentStatus.ACTIVE)
                        .description(String.format("JVM Heap/System memory near saturation at %.2f%%", memory))
                        .aiSummary(explanation)
                        .detectedAt(LocalDateTime.now())
                        .build();

                incidentRepository.save(incident);
                log.warn("Auto-detected Incident: Memory saturation triggered on service {}", SERVICE_NAME);
                broadcastIncidentUpdate();
            }
        } else {
            if (activeMemIncident.isPresent()) {
                // Auto-resolve incident
                Incident incident = activeMemIncident.get();
                incident.setStatus(IncidentStatus.RESOLVED);
                incident.setResolvedAt(LocalDateTime.now());
                incidentRepository.save(incident);
                log.info("Auto-resolved Incident: Memory allocation stabilized to %.2f%% on service {}", memory, SERVICE_NAME);
                broadcastIncidentUpdate();
            }
        }
    }

    private void evaluateErrorRateAnomaly(double errorRate) {
        Optional<Incident> activeErrIncident = incidentRepository
                .findFirstByServiceNameAndMetricTypeAndStatus(SERVICE_NAME, "ERROR_RATE", IncidentStatus.ACTIVE);

        if (errorRate >= ERROR_RATE_THRESHOLD) {
            if (activeErrIncident.isEmpty()) {
                // Trigger ERROR incident
                String explanation = aiAnalysisService.generateIncidentAnalysis(SERVICE_NAME, "ERROR_RATE", errorRate);
                Incident incident = Incident.builder()
                        .serviceName(SERVICE_NAME)
                        .metricType("ERROR_RATE")
                        .metricValue(errorRate)
                        .severity(IncidentSeverity.HIGH)
                        .status(IncidentStatus.ACTIVE)
                        .description(String.format("Elevated HTTP Error Rate detected at %.2f%%", errorRate))
                        .aiSummary(explanation)
                        .detectedAt(LocalDateTime.now())
                        .build();

                incidentRepository.save(incident);
                log.warn("Auto-detected Incident: High Error Rate triggered on service {}", SERVICE_NAME);
                broadcastIncidentUpdate();
            }
        } else {
            if (activeErrIncident.isPresent()) {
                // Auto-resolve incident
                Incident incident = activeErrIncident.get();
                incident.setStatus(IncidentStatus.RESOLVED);
                incident.setResolvedAt(LocalDateTime.now());
                incidentRepository.save(incident);
                log.info("Auto-resolved Incident: HTTP error rate dropped to %.2f%% on service {}", errorRate, SERVICE_NAME);
                broadcastIncidentUpdate();
            }
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
