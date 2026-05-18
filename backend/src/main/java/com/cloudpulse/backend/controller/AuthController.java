package com.cloudpulse.backend.controller;

import com.cloudpulse.backend.dto.LoginRequest;
import com.cloudpulse.backend.entity.User;
import com.cloudpulse.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4201"})
public class AuthController {

    @Autowired
    private UserService service;

    @Autowired
    private com.cloudpulse.backend.service.AuditLogService auditLogService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        User registered = service.registerUser(user);
        try {
            auditLogService.log("USER_REGISTER", registered.getEmail(), "Registered new user account: " + registered.getName() + " (" + registered.getRole() + ")");
        } catch (Exception e) {
            System.err.println("Failed to log registration audit log: " + e.getMessage());
        }
        return registered;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request
    ) {
        var response = service.login(request);
        try {
            auditLogService.log("LOGIN", request.getEmail(), "User logged in successfully.");
        } catch (Exception e) {
            System.err.println("Failed to log authentication audit log: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}