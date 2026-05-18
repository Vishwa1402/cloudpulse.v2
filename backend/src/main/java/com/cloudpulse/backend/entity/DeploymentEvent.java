package com.cloudpulse.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "deployment_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", referencedColumnName = "id", nullable = false)
    private Service service;

    @Column(nullable = false)
    private String version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deployed_by", referencedColumnName = "id")
    private User deployedBy;

    @Column(name = "deployed_at", nullable = false)
    private LocalDateTime deployedAt;

    @PrePersist
    protected void onCreate() {
        if (deployedAt == null) {
            deployedAt = LocalDateTime.now();
        }
    }
}
