package com.cloudpulse.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_metrics", indexes = {
    @Index(name = "idx_metrics_service_time", columnList = "service_id, collected_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", referencedColumnName = "id", nullable = false)
    private Service service;

    @Column(name = "metric_type", nullable = false)
    private String metricType;

    @Column(name = "metric_value", nullable = false)
    private Double metricValue;

    @Column(name = "collected_at", nullable = false)
    private LocalDateTime collectedAt;

    @PrePersist
    protected void onCreate() {
        if (collectedAt == null) {
            collectedAt = LocalDateTime.now();
        }
    }
}
