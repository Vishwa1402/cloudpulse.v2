package com.cloudpulse.backend.repository;

import com.cloudpulse.backend.entity.ServiceHealth;
import com.cloudpulse.backend.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceHealthRepository extends JpaRepository<ServiceHealth, Long> {
    List<ServiceHealth> findByServiceOrderByCheckedAtDesc(Service service);
}
