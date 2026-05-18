package com.cloudpulse.backend.controller;

import com.cloudpulse.backend.entity.Project;
import com.cloudpulse.backend.entity.User;
import com.cloudpulse.backend.repository.ProjectRepository;
import com.cloudpulse.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4201"})
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private UserRepository userRepo;

    @GetMapping
    public ResponseEntity<List<Project>> getProjects() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getOrganization() == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(projectRepo.findByOrganization(user.getOrganization()));
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getOrganization() == null) {
            throw new RuntimeException("User does not belong to any organization");
        }
        project.setOrganization(user.getOrganization());
        return ResponseEntity.ok(projectRepo.save(project));
    }
}
