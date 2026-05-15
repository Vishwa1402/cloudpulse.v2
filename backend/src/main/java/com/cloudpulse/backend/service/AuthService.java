package com.cloudpulse.backend.service;

import com.cloudpulse.backend.dto.*;
import com.cloudpulse.backend.entity.User;
import com.cloudpulse.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    public User register(RegisterRequest req) {
        User user = new User();
        user.setName(req.name);
        user.setEmail(req.email);
        user.setPassword(encoder.encode(req.password));

        return repo.save(user);
    }
}