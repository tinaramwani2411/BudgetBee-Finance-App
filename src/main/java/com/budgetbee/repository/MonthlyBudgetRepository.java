package com.budgetbee.repository;

import com.budgetbee.model.MonthlyBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MonthlyBudgetRepository extends JpaRepository<MonthlyBudget, Long> {
    Optional<MonthlyBudget> findByUserIdAndMonth(Long userId, String month);
}
