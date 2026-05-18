package com.cloudpulse.backend.repository;

import com.cloudpulse.backend.entity.IncidentStatusHistory;
import com.cloudpulse.backend.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncidentStatusHistoryRepository extends JpaRepository<IncidentStatusHistory, Long> {
    List<IncidentStatusHistory> findByIncidentOrderByChangedAtDesc(Incident incident);
}
