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

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return service.registerUser(user);
    }

    @PostMapping("/login")
public ResponseEntity<?> login(
        @RequestBody LoginRequest request
) {

    return ResponseEntity.ok(
            service.login(request)
    );
}
}