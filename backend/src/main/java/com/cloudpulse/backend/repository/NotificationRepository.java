package com.cloudpulse.backend.repository;

import com.cloudpulse.backend.entity.Notification;
import com.cloudpulse.backend.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByIncident(Incident incident);
    List<Notification> findByStatus(String status);
}
