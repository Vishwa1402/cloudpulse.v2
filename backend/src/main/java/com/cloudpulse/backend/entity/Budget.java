package com.cloudpulse.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "budgets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Double monthlyAmount;
    
    private Double alertThresholdPercentage;
    
    @Enumerated(EnumType.STRING)
    private CloudProvider provider;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
