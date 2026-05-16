package com.cloudpulse.backend.service;

import com.cloudpulse.backend.entity.Budget;
import com.cloudpulse.backend.entity.CloudProvider;
import com.cloudpulse.backend.entity.CloudResource;
import com.cloudpulse.backend.entity.User;
import com.cloudpulse.backend.repository.BudgetRepository;
import com.cloudpulse.backend.repository.CloudResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CloudResourceRepository cloudResourceRepository;
    private final EmailService emailService;

    public void checkBudgetsForUser(User user) {
        List<Budget> budgets = budgetRepository.findByUserId(user.getId());
        List<CloudResource> resources = cloudResourceRepository.findByUserId(user.getId());

        for (Budget budget : budgets) {
            double currentCost = resources.stream()
                    .filter(r -> r.getProvider() == budget.getProvider() && ("RUNNING".equalsIgnoreCase(r.getStatus()) || "ACTIVE".equalsIgnoreCase(r.getStatus())))
                    .mapToDouble(r -> (r.getCostPerHour() != null ? r.getCostPerHour() : 0.0) * 730)
                    .sum();

            double thresholdAmount = budget.getMonthlyAmount() * (budget.getAlertThresholdPercentage() / 100.0);

            if (currentCost > thresholdAmount) {
                log.warn("BUDGET BREACHED for user {} on provider {}. Current: {}, Threshold: {}", 
                        user.getEmail(), budget.getProvider(), currentCost, thresholdAmount);
                emailService.sendBudgetAlert(user.getEmail(), budget.getProvider().name(), currentCost, budget.getMonthlyAmount());
            }
        }
    }
}
