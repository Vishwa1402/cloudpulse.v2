package com.cloudpulse.backend.controller;

import com.cloudpulse.backend.entity.AuditLog;
import com.cloudpulse.backend.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4201"})
public class AuditLogController {

    @Autowired
    private AuditLogRepository auditRepo;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<List<AuditLog>> getLogs() {
        return ResponseEntity.ok(auditRepo.findAll());
    }
}
