package com.cloudpulse.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiAnalysisServiceTest {

    @Mock
    private PrometheusService prometheusService;

    private AiAnalysisService aiAnalysisService;

    @BeforeEach
    void setUp() {
        aiAnalysisService = new AiAnalysisService(prometheusService);
    }

    @Test
    void generateIncidentAnalysis_CpuSpike_ReturnsCorrectSummary() {
        String analysis = aiAnalysisService.generateIncidentAnalysis("demo-service", "CPU", 85.5);
        
        assertNotNull(analysis);
        assertTrue(analysis.contains("[FACT]"));
        assertTrue(analysis.contains("CPU utilization has spiked to 85.50%"));
        assertTrue(analysis.contains("[SUGGESTION]"));
        assertTrue(analysis.contains("thread pool exhaustion"));
        assertTrue(analysis.contains("[RECOMMENDED ACTION]"));
    }

    @Test
    void generateIncidentAnalysis_MemorySpike_ReturnsCorrectSummary() {
        String analysis = aiAnalysisService.generateIncidentAnalysis("demo-service", "MEMORY", 94.2);
        
        assertNotNull(analysis);
        assertTrue(analysis.contains("[FACT]"));
        assertTrue(analysis.contains("System memory allocation is at 94.20%"));
        assertTrue(analysis.contains("[SUGGESTION]"));
        assertTrue(analysis.contains("memory leaks"));
    }

    @Test
    void generateIncidentAnalysis_ErrorRateSpike_ReturnsCorrectSummary() {
        String analysis = aiAnalysisService.generateIncidentAnalysis("demo-service", "ERROR_RATE", 2.5);
        
        assertNotNull(analysis);
        assertTrue(analysis.contains("[FACT]"));
        assertTrue(analysis.contains("HTTP error rate has spiked to 2.50%"));
        assertTrue(analysis.contains("[SUGGESTION]"));
        assertTrue(analysis.contains("Internal Server Errors"));
    }

    @Test
    void handleTelemetryChat_CpuQuery_RetrievesLiveCpu() {
        when(prometheusService.getCpuUsage()).thenReturn(45.2);
        when(prometheusService.getMemoryUsage()).thenReturn(70.0);
        when(prometheusService.getRequestsPerSecond()).thenReturn(10.0);
        when(prometheusService.getErrorRate()).thenReturn(0.0);

        String reply = aiAnalysisService.handleTelemetryChat("Why did CPU spike?");

        assertNotNull(reply);
        assertTrue(reply.contains("CPU"));
        assertTrue(reply.contains("45.20%"));
        assertTrue(reply.contains("[FACT]"));
        assertTrue(reply.contains("[SUGGESTION]"));
    }

    @Test
    void handleTelemetryChat_HealthOverviewQuery_RetrievesAllTelemetry() {
        when(prometheusService.getCpuUsage()).thenReturn(12.5);
        when(prometheusService.getMemoryUsage()).thenReturn(45.0);
        when(prometheusService.getRequestsPerSecond()).thenReturn(150.0);
        when(prometheusService.getErrorRate()).thenReturn(0.1);

        String reply = aiAnalysisService.handleTelemetryChat("What is the overall system health?");

        assertNotNull(reply);
        assertTrue(reply.contains("CPU Utilization: 12.50%"));
        assertTrue(reply.contains("Memory Allocation: 45.00%"));
        assertTrue(reply.contains("Error Rate: 0.10%"));
        assertTrue(reply.contains("Request Rate: 150.00 RPS"));
        assertTrue(reply.contains("SYSTEM FULLY OPERATIONAL"));
    }
}
