package com.cloudpulse.backend.repository;

import com.cloudpulse.backend.entity.CloudResource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CloudResourceRepository extends JpaRepository<CloudResource, Long> {
    List<CloudResource> findByUserId(Long userId);
}
