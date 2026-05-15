package com.budgetbee.dto;

import java.util.List;
import java.util.Map;

public class DashboardDTO {

    private Double totalExpense;
    private Double lastExpense;
    private Long expenseCount;
    private Double remainingBudget;
    private Double monthlyBudget;
    private List<Map<String, Object>> categorySummary;
    private List<Map<String, Object>> monthlyTrend;
    private List<Map<String, Object>> recentTransactions;

    public Double getTotalExpense() { return totalExpense; }
    public void setTotalExpense(Double totalExpense) { this.totalExpense = totalExpense; }
    public Double getLastExpense() { return lastExpense; }
    public void setLastExpense(Double lastExpense) { this.lastExpense = lastExpense; }
    public Long getExpenseCount() { return expenseCount; }
    public void setExpenseCount(Long expenseCount) { this.expenseCount = expenseCount; }
    public Double getRemainingBudget() { return remainingBudget; }
    public void setRemainingBudget(Double remainingBudget) { this.remainingBudget = remainingBudget; }
    public Double getMonthlyBudget() { return monthlyBudget; }
    public void setMonthlyBudget(Double monthlyBudget) { this.monthlyBudget = monthlyBudget; }
    public List<Map<String, Object>> getCategorySummary() { return categorySummary; }
    public void setCategorySummary(List<Map<String, Object>> categorySummary) { this.categorySummary = categorySummary; }
    public List<Map<String, Object>> getMonthlyTrend() { return monthlyTrend; }
    public void setMonthlyTrend(List<Map<String, Object>> monthlyTrend) { this.monthlyTrend = monthlyTrend; }
    public List<Map<String, Object>> getRecentTransactions() { return recentTransactions; }
    public void setRecentTransactions(List<Map<String, Object>> recentTransactions) { this.recentTransactions = recentTransactions; }
}
