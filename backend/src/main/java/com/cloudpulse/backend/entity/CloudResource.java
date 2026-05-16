package com.cloudpulse.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cloud_resources")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    private String type;
    
    @Enumerated(EnumType.STRING)
    private CloudProvider provider;

    private String status;
    
    private Double costPerHour;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
