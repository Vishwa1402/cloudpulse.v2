package com.cloudpulse.backend.repository;

import com.cloudpulse.backend.entity.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationChannelRepository extends JpaRepository<NotificationChannel, Long> {
    Optional<NotificationChannel> findByName(String name);
    List<NotificationChannel> findByType(String type);
}
