package com.cloudpulse.backend.service;

import com.cloudpulse.backend.dto.DashboardSummaryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private PrometheusService prometheusService;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void testGetSummary_ReturnsCorrectMetrics() {
        when(prometheusService.getCpuUsage()).thenReturn(45.2);
        when(prometheusService.getMemoryUsage()).thenReturn(78.1);
        when(prometheusService.getErrorRate()).thenReturn(1.2);
        when(prometheusService.getRequestsPerSecond()).thenReturn(150.0);

        DashboardSummaryResponse summary = dashboardService.getSummary();

        assertNotNull(summary);
        assertEquals(45.2, summary.getCpuUsage());
        assertEquals(78.1, summary.getMemoryUsage());
        assertEquals(1.2, summary.getErrorRate());
        assertEquals(0, summary.getActiveAlerts());
        assertEquals(150.0, summary.getRequestsPerSecond());

        verify(prometheusService, times(1)).getCpuUsage();
        verify(prometheusService, times(1)).getMemoryUsage();
        verify(prometheusService, times(1)).getErrorRate();
        verify(prometheusService, times(1)).getRequestsPerSecond();
    }

    @Test
    void testGetSummary_PrometheusThrowsException_PropagatesException() {
        when(prometheusService.getCpuUsage()).thenThrow(new RuntimeException("Prometheus down"));

        assertThrows(RuntimeException.class, () -> dashboardService.getSummary());
    }
}
