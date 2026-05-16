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
    private int monthlyCost;
    private int activeResources;
    private int alerts;
    private int savings;
    private String anomalyAlert;
}