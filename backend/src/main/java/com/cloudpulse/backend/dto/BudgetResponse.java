package com.cloudpulse.backend.dto;

import com.cloudpulse.backend.entity.CloudProvider;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BudgetResponse {
    private Long id;
    private CloudProvider provider;
    private Double monthlyAmount;
    private Double alertThresholdPercentage;
}
