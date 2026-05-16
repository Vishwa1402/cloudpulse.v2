package com.cloudpulse.backend.service;

import com.cloudpulse.backend.entity.Budget;
import com.cloudpulse.backend.entity.CloudProvider;
import com.cloudpulse.backend.entity.CloudResource;
import com.cloudpulse.backend.entity.User;
import com.cloudpulse.backend.repository.BudgetRepository;
import com.cloudpulse.backend.repository.CloudResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private CloudResourceRepository cloudResourceRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private BudgetService budgetService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@cloudpulse.com");
    }

    @Test
    void testCheckBudgetsForUser_BreachTriggersEmail() {
        // Arrange
        Budget awsBudget = new Budget();
        awsBudget.setProvider(CloudProvider.AWS);
        awsBudget.setMonthlyAmount(100.0);
        awsBudget.setAlertThresholdPercentage(80.0); // Breach threshold: 80.0

        CloudResource expensiveResource = new CloudResource();
        expensiveResource.setProvider(CloudProvider.AWS);
        expensiveResource.setStatus("RUNNING");
        expensiveResource.setCostPerHour(0.20); // 0.20 * 730 = $146/mo

        when(budgetRepository.findByUserId(1L)).thenReturn(List.of(awsBudget));
        when(cloudResourceRepository.findByUserId(1L)).thenReturn(List.of(expensiveResource));

        // Act
        budgetService.checkBudgetsForUser(testUser);

        // Assert
        verify(emailService, times(1)).sendBudgetAlert(
                eq("test@cloudpulse.com"), 
                eq("AWS"), 
                eq(146.0), 
                eq(100.0)
        );
    }

    @Test
    void testCheckBudgetsForUser_UnderThresholdNoEmail() {
        // Arrange
        Budget awsBudget = new Budget();
        awsBudget.setProvider(CloudProvider.AWS);
        awsBudget.setMonthlyAmount(100.0);
        awsBudget.setAlertThresholdPercentage(80.0); // Breach threshold: 80.0

        CloudResource cheapResource = new CloudResource();
        cheapResource.setProvider(CloudProvider.AWS);
        cheapResource.setStatus("RUNNING");
        cheapResource.setCostPerHour(0.05); // 0.05 * 730 = $36.5/mo

        when(budgetRepository.findByUserId(1L)).thenReturn(List.of(awsBudget));
        when(cloudResourceRepository.findByUserId(1L)).thenReturn(List.of(cheapResource));

        // Act
        budgetService.checkBudgetsForUser(testUser);

        // Assert
        verify(emailService, never()).sendBudgetAlert(anyString(), anyString(), anyDouble(), anyDouble());
    }
}
