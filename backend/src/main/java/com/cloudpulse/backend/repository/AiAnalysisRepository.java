package com.cloudpulse.backend.repository;

import com.cloudpulse.backend.entity.AiAnalysis;
import com.cloudpulse.backend.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AiAnalysisRepository extends JpaRepository<AiAnalysis, Long> {
    Optional<AiAnalysis> findByIncident(Incident incident);
}
