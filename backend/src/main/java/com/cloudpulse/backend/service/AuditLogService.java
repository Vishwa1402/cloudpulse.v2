package com.cloudpulse.backend.service;

import com.cloudpulse.backend.entity.AuditLog;
import com.cloudpulse.backend.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditRepo;

    public void log(String action, String performedBy, String details) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .performedBy(performedBy)
                .details(details)
                .build();
        auditRepo.save(log);
    }
}
