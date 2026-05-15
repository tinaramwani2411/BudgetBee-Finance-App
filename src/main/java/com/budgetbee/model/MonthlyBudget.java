package com.budgetbee.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_budgets",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "month"}))
public class MonthlyBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 7)
    private String month;

    @Column(name = "budget_amount", nullable = false)
    private Double budgetAmount;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public MonthlyBudget() {}

    public MonthlyBudget(User user, String month, Double budgetAmount) {
        this.user = user;
        this.month = month;
        this.budgetAmount = budgetAmount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public Double getBudgetAmount() { return budgetAmount; }
    public void setBudgetAmount(Double budgetAmount) { this.budgetAmount = budgetAmount; }
}
