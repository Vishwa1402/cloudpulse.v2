package com.cloudpulse.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private String metricType; // CPU, MEMORY, ERROR_RATE, LATENCY

    private Double metricValue;

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
}
