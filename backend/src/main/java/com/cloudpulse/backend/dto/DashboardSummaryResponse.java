package com.cloudpulse.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private double cpuUsage;
    private double memoryUsage;
    private double errorRate;
    private int activeAlerts;
    private double requestsPerSecond;
}