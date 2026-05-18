package com.cloudpulse.backend.repository;

import com.cloudpulse.backend.entity.Alert;
import com.cloudpulse.backend.entity.AlertRule;
import com.cloudpulse.backend.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByAlertRule(AlertRule alertRule);
    List<Alert> findByStatus(String status);
    List<Alert> findByServiceAndActive(Service service, boolean active);
}
