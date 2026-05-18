package com.cloudpulse.backend.service;

import com.cloudpulse.backend.dto.DashboardSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PrometheusService prometheusService;

    public DashboardSummaryResponse getSummary() {
        double cpu = prometheusService.getCpuUsage();
        double mem = prometheusService.getMemoryUsage();
        double err = prometheusService.getErrorRate();
        double rps = prometheusService.getRequestsPerSecond();

        return DashboardSummaryResponse.builder()
                .cpuUsage(cpu)
                .memoryUsage(mem)
                .errorRate(err)
                .activeAlerts(0) // Default active alerts count
                .requestsPerSecond(rps)
                .build();
    }
}
