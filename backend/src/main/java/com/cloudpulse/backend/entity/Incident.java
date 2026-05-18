package com.cloudpulse.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidents", indexes = {
    @Index(name = "idx_incident_status", columnList = "status"),
    @Index(name = "idx_incident_service", columnList = "service_id"),
    @Index(name = "idx_incident_detected_at", columnList = "detected_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private Service service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id")
    private Alert alert;

    @Builder.Default
    private String priority = "P1";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acknowledged_by")
    private User acknowledgedBy;

    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(length = 2000)
    private String aiSummary; // AI analysis and root cause suggestions

    @Column(nullable = false)
    private LocalDateTime detectedAt;

    private LocalDateTime resolvedAt;

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("serviceName")
    public String getServiceName() {
        return service != null ? service.getName() : "cloudpulse-demo-service";
    }

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("metricType")
    public String getMetricType() {
        return alert != null ? alert.getMetricType() : "CPU";
    }

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("metricValue")
    public Double getMetricValue() {
        return alert != null ? alert.getCurrentValue() : 0.0;
    }
}

