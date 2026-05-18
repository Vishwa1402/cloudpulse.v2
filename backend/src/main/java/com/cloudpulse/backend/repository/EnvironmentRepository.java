package com.cloudpulse.backend.repository;

import com.cloudpulse.backend.entity.Environment;
import com.cloudpulse.backend.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnvironmentRepository extends JpaRepository<Environment, Long> {
    List<Environment> findByOrganization(Organization organization);
    Optional<Environment> findByNameAndOrganization(String name, Organization organization);
}
