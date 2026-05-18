package com.cloudpulse.backend.repository;

import com.cloudpulse.backend.entity.Project;
import com.cloudpulse.backend.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOrganization(Organization organization);
    Optional<Project> findByName(String name);
}
