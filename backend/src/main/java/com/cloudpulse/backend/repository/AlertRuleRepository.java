package com.cloudpulse.backend.repository;

import com.cloudpulse.backend.entity.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {
    List<AlertRule> findByMetric(String metric);
    Optional<AlertRule> findByMetricAndActive(String metric, boolean active);
}
