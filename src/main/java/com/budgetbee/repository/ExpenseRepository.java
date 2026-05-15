package com.budgetbee.repository;

import com.budgetbee.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserIdOrderByDateDesc(Long userId);

    List<Expense> findByUserIdAndCategoryOrderByDateDesc(Long userId, String category);

    List<Expense> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate start, LocalDate end);

    List<Expense> findByUserIdAndCategoryAndDateBetweenOrderByDateDesc(
            Long userId, String category, LocalDate start, LocalDate end);

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND LOWER(e.title) LIKE LOWER(CONCAT('%', :q, '%')) ORDER BY e.date DESC")
    List<Expense> searchByUserId(@Param("userId") Long userId, @Param("q") String q);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :start AND :end")
    Double getTotalExpenseForPeriod(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query(value = "SELECT e.category AS category, COALESCE(SUM(e.amount), 0) AS total FROM expenses e WHERE e.user_id = :userId AND e.date BETWEEN :start AND :end GROUP BY e.category ORDER BY total DESC", nativeQuery = true)
    List<Map<String, Object>> getCategorySummary(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query(value = "SELECT MONTH(e.date) AS month, COALESCE(SUM(e.amount), 0) AS total FROM expenses e WHERE e.user_id = :userId AND YEAR(e.date) = :year GROUP BY MONTH(e.date) ORDER BY MONTH(e.date)", nativeQuery = true)
    List<Map<String, Object>> getMonthlyTrend(@Param("userId") Long userId, @Param("year") int year);

    Long countByUserId(Long userId);

    List<Expense> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);
}
