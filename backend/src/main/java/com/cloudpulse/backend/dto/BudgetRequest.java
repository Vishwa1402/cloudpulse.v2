package com.cloudpulse.backend.dto;

import com.cloudpulse.backend.entity.CloudProvider;
import lombok.Data;

@Data
public class BudgetRequest {
    private CloudProvider provider;
    private Double monthlyAmount;
    private Double alertThresholdPercentage;
}
