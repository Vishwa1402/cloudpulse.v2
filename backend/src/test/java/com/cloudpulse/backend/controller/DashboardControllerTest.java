package com.cloudpulse.backend.controller;

import com.cloudpulse.backend.dto.DashboardSummaryResponse;
import com.cloudpulse.backend.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    @Test
    void testGetSummary_Success() {
        DashboardSummaryResponse response = DashboardSummaryResponse.builder()
                .cpuUsage(45.2)
                .memoryUsage(78.1)
                .errorRate(1.2)
                .activeAlerts(3)
                .requestsPerSecond(150.0)
                .build();

        when(dashboardService.getSummary()).thenReturn(response);

        DashboardSummaryResponse result = dashboardController.getSummary();

        assertNotNull(result);
        assertEquals(45.2, result.getCpuUsage());
        assertEquals(78.1, result.getMemoryUsage());
        assertEquals(1.2, result.getErrorRate());
        assertEquals(3, result.getActiveAlerts());
        assertEquals(150.0, result.getRequestsPerSecond());

        verify(dashboardService, times(1)).getSummary();
    }

    @Test
    void testGetSummary_ServiceThrowsException_PropagatesException() {
        when(dashboardService.getSummary()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> dashboardController.getSummary());
    }
}
