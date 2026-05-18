package com.cloudpulse.backend.repository;

import com.cloudpulse.backend.entity.IncidentComment;
import com.cloudpulse.backend.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncidentCommentRepository extends JpaRepository<IncidentComment, Long> {
    List<IncidentComment> findByIncident(Incident incident);
}
