package com.cloudpulse.backend.repository;

import com.cloudpulse.backend.entity.Incident;
import com.cloudpulse.backend.entity.IncidentStatus;
import com.cloudpulse.backend.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    List<Incident> findByStatus(IncidentStatus status);

    Optional<Incident> findFirstByServiceAndAlertMetricTypeAndStatus(Service service, String metricType, IncidentStatus status);

    List<Incident> findAllByOrderByDetectedAtDesc();
}
