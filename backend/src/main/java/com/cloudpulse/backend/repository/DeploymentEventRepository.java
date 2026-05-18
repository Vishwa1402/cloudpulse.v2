package com.cloudpulse.backend.repository;

import com.cloudpulse.backend.entity.DeploymentEvent;
import com.cloudpulse.backend.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeploymentEventRepository extends JpaRepository<DeploymentEvent, Long> {
    List<DeploymentEvent> findByServiceOrderByDeployedAtDesc(Service service);
}
