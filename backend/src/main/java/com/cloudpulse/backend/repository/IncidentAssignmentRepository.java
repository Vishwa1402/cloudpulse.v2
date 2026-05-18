package com.cloudpulse.backend.repository;

import com.cloudpulse.backend.entity.IncidentAssignment;
import com.cloudpulse.backend.entity.Incident;
import com.cloudpulse.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncidentAssignmentRepository extends JpaRepository<IncidentAssignment, Long> {
    List<IncidentAssignment> findByIncident(Incident incident);
    List<IncidentAssignment> findByAssignedUser(User user);
}
