package com.cloudpulse.backend.service;

import com.cloudpulse.backend.dto.DashboardSummaryResponse;
import com.cloudpulse.backend.entity.CloudResource;
import com.cloudpulse.backend.entity.User;
import com.cloudpulse.backend.repository.CloudResourceRepository;
import com.cloudpulse.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CloudResourceRepository cloudResourceRepository;
    private final UserRepository userRepository;
    private final DataSeederService dataSeederService;
    private final AwsIntegrationService awsIntegrationService;

    public DashboardSummaryResponse getSummary() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return DashboardSummaryResponse.builder().build();
        }
        
        // Attempt to sync real AWS resources first
        awsIntegrationService.syncEc2Instances(user);
        
        // Seed mock data if still empty (fallback for non-AWS users or if sync fails)
        dataSeederService.seedResourcesForUserIfEmpty(user);

        List<CloudResource> resources = cloudResourceRepository.findByUserId(user.getId());
        
        double totalMonthlyCost = resources.stream()
                .mapToDouble(res -> (res.getCostPerHour() != null ? res.getCostPerHour() : 0.0) * 730) // approx 730 hours in a month
                .sum();
                
        int activeResources = (int) resources.stream()
                .filter(res -> "RUNNING".equalsIgnoreCase(res.getStatus()) || "ACTIVE".equalsIgnoreCase(res.getStatus()))
                .count();

        return DashboardSummaryResponse.builder()
                .monthlyCost((int) totalMonthlyCost)
                .activeResources(activeResources)
                .alerts(0) // Default for now
                .savings(0) // Default for now
                .build();
    }
}
