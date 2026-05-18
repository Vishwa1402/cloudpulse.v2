package com.cloudpulse.backend.repository;

import com.cloudpulse.backend.entity.ServiceMetric;
import com.cloudpulse.backend.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceMetricRepository extends JpaRepository<ServiceMetric, Long> {
    List<ServiceMetric> findByServiceOrderByCollectedAtDesc(Service service);
    List<ServiceMetric> findByServiceAndMetricTypeOrderByCollectedAtDesc(Service service, String metricType);
}
