package com.cloudpulse.backend.controller;

import com.cloudpulse.backend.entity.Service;
import com.cloudpulse.backend.entity.Project;
import com.cloudpulse.backend.entity.User;
import com.cloudpulse.backend.repository.ServiceRepository;
import com.cloudpulse.backend.repository.ProjectRepository;
import com.cloudpulse.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4201"})
public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepo;

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private UserRepository userRepo;

    @GetMapping
    public ResponseEntity<List<Service>> getServices(@RequestParam(required = false) Long projectId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getOrganization() == null) {
            return ResponseEntity.ok(List.of());
        }

        if (projectId != null) {
            Project p = projectRepo.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
            if (!p.getOrganization().getId().equals(user.getOrganization().getId())) {
                throw new RuntimeException("Unauthorized project access");
            }
            return ResponseEntity.ok(serviceRepo.findByProject(p));
        }

        List<Project> projects = projectRepo.findByOrganization(user.getOrganization());
        List<Service> services = new ArrayList<>();
        for (Project p : projects) {
            services.addAll(serviceRepo.findByProject(p));
        }
        return ResponseEntity.ok(services);
    }

    @PostMapping
    public ResponseEntity<Service> createService(@RequestParam Long projectId, @RequestBody Service service) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getOrganization() == null) {
            throw new RuntimeException("User does not belong to any organization");
        }

        Project p = projectRepo.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        if (!p.getOrganization().getId().equals(user.getOrganization().getId())) {
            throw new RuntimeException("Unauthorized project association");
        }

        service.setProject(p);
        return ResponseEntity.ok(serviceRepo.save(service));
    }
}
