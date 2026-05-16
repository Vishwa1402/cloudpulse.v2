package com.cloudpulse.backend.service;

import com.cloudpulse.backend.dto.DashboardSummaryResponse;
import com.cloudpulse.backend.entity.CloudResource;
import com.cloudpulse.backend.entity.User;
import com.cloudpulse.backend.repository.CloudResourceRepository;
import com.cloudpulse.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LiveCostSimulationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final CloudResourceRepository cloudResourceRepository;
    private final BudgetService budgetService;

    private final Random random = new Random();

    @Scheduled(fixedRate = 3000)
    public void broadcastLiveCost() {
        for (User user : userRepository.findAll()) {
            budgetService.checkBudgetsForUser(user);
        }

        // For the global dashboard simulation, just aggregate ALL resources across the whole system
        // In a real app, this would be per-tenant/per-user.
        List<CloudResource> allResources = cloudResourceRepository.findAll();
        
        double totalMonthlyCost = allResources.stream()
                .mapToDouble(res -> (res.getCostPerHour() != null ? res.getCostPerHour() : 0.0) * 730)
                .sum();
                
        int activeResources = (int) allResources.stream()
                .filter(res -> "RUNNING".equalsIgnoreCase(res.getStatus()) || "ACTIVE".equalsIgnoreCase(res.getStatus()))
                .count();

        // Add a slight fluctuation to the total cost to make the graph look alive
        int costChange = random.nextInt(100) - 40; 
        totalMonthlyCost += costChange;

        String alert = null;
        if (costChange > 50) {
            alert = "System-wide compute spend increased sharply!";
        }

        DashboardSummaryResponse response = DashboardSummaryResponse.builder()
                .monthlyCost((int) totalMonthlyCost)
                .activeResources(activeResources)
                .alerts(alert != null ? 1 : 0)
                .savings((int)(totalMonthlyCost * 0.15)) // fake 15% savings
                .anomalyAlert(alert)
                .build();

        messagingTemplate.convertAndSend("/topic/costs", response);
    }
}
