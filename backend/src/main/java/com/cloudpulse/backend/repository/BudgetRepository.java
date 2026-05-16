package com.cloudpulse.backend.repository;

import com.cloudpulse.backend.entity.Budget;
import com.cloudpulse.backend.entity.CloudProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserId(Long userId);
    Optional<Budget> findByUserIdAndProvider(Long userId, CloudProvider provider);
}
