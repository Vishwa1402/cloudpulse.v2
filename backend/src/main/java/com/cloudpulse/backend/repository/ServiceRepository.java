package com.cloudpulse.backend.repository;

import com.cloudpulse.backend.entity.Service;
import com.cloudpulse.backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByProject(Project project);
}
