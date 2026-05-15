package com.cloudpulse.backend.service;

import com.cloudpulse.backend.dto.AuthResponse;
import com.cloudpulse.backend.dto.LoginRequest;
import com.cloudpulse.backend.entity.User;
import com.cloudpulse.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    // REGISTER
    public User registerUser(User user) {

        user.setPassword(
                encoder.encode(user.getPassword())
        );

        return repo.save(user);
    }

    // LOGIN
    public AuthResponse login(LoginRequest request) {

        User user = repo.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        if (!encoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {

            throw new RuntimeException("Invalid password");
        }

        String token =
                jwtService.generateToken(user.getEmail());

        return new AuthResponse(token);
    }
}