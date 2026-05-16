package com.cloudpulse.backend.controller;

import com.cloudpulse.backend.dto.BudgetRequest;
import com.cloudpulse.backend.dto.BudgetResponse;
import com.cloudpulse.backend.entity.Budget;
import com.cloudpulse.backend.entity.User;
import com.cloudpulse.backend.repository.BudgetRepository;
import com.cloudpulse.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/finance/budgets")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4201"})
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getBudgets() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        List<BudgetResponse> responses = budgetRepository.findByUserId(user.getId()).stream()
                .map(b -> BudgetResponse.builder()
                        .id(b.getId())
                        .provider(b.getProvider())
                        .monthlyAmount(b.getMonthlyAmount())
                        .alertThresholdPercentage(b.getAlertThresholdPercentage())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public ResponseEntity<BudgetResponse> createOrUpdateBudget(@RequestBody BudgetRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        Optional<Budget> existingOpt = budgetRepository.findByUserIdAndProvider(user.getId(), request.getProvider());
        Budget budget = existingOpt.orElse(new Budget());
        
        budget.setUser(user);
        budget.setProvider(request.getProvider());
        budget.setMonthlyAmount(request.getMonthlyAmount());
        budget.setAlertThresholdPercentage(request.getAlertThresholdPercentage());
        
        budget = budgetRepository.save(budget);

        return ResponseEntity.ok(BudgetResponse.builder()
                .id(budget.getId())
                .provider(budget.getProvider())
                .monthlyAmount(budget.getMonthlyAmount())
                .alertThresholdPercentage(budget.getAlertThresholdPercentage())
                .build());
    }
}
