package com.cloudpulse.backend.controller;

import com.cloudpulse.backend.dto.DashboardSummaryResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4201"})
public class DashboardController {

    private final com.cloudpulse.backend.service.DashboardService dashboardService;

    public DashboardController(com.cloudpulse.backend.service.DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary() {
        return dashboardService.getSummary();
    }
}