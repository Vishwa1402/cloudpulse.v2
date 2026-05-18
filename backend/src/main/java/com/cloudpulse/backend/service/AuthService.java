package com.cloudpulse.backend.service;

import com.cloudpulse.backend.dto.*;
import com.cloudpulse.backend.entity.User;
import com.cloudpulse.backend.repository.UserRepository;
import com.cloudpulse.backend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private com.cloudpulse.backend.repository.OrganizationRepository organizationRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    public User register(RegisterRequest req) {
        User user = new User();
        user.setName(req.name);
        user.setEmail(req.email);
        user.setPassword(encoder.encode(req.password));

        // Resolve or create Organization
        String orgName = req.organizationName != null && !req.organizationName.trim().isEmpty() 
            ? req.organizationName.trim() 
            : "Default Organization";
        
        com.cloudpulse.backend.entity.Organization org = organizationRepo.findByName(orgName)
            .orElseGet(() -> organizationRepo.save(
                com.cloudpulse.backend.entity.Organization.builder()
                    .name(orgName)
                    .build()
            ));
        user.setOrganization(org);

        // Resolve and map user Role
        com.cloudpulse.backend.entity.Role userRole = com.cloudpulse.backend.entity.Role.VIEWER;
        if (req.role != null) {
            try {
                userRole = com.cloudpulse.backend.entity.Role.valueOf(req.role.toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                // Maintain default Viewer role on mismatch
            }
        }
        user.setRole(userRole);

        return repo.save(user);
    }
}