package com.cloudpulse.backend.repository;

import com.cloudpulse.backend.entity.IncidentEvent;
import com.cloudpulse.backend.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncidentEventRepository extends JpaRepository<IncidentEvent, Long> {
    List<IncidentEvent> findByIncident(Incident incident);
}
