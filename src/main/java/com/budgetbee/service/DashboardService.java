package com.budgetbee.service;

import com.budgetbee.dto.DashboardDTO;
import com.budgetbee.model.Expense;
import com.budgetbee.model.MonthlyBudget;
import com.budgetbee.model.User;
import com.budgetbee.repository.ExpenseRepository;
import com.budgetbee.repository.MonthlyBudgetRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ExpenseRepository expenseRepository;
    private final UserService userService;
    private final MonthlyBudgetRepository budgetRepository;

    public DashboardService(ExpenseRepository expenseRepository,
                            UserService userService,
                            MonthlyBudgetRepository budgetRepository) {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
        this.budgetRepository = budgetRepository;
    }

    public DashboardDTO getUserDashboard(String username, Integer year, Integer month) {
        User user = userService.getUserByUsername(username);
        return buildDashboard(user.getId(), year, month);
    }

    public DashboardDTO getPublicDashboard() {
        DashboardDTO dto = new DashboardDTO();
        dto.setTotalExpense(4520.00);
        dto.setLastExpense(250.00);
        dto.setExpenseCount(24L);
        dto.setMonthlyBudget(5000.00);
        dto.setRemainingBudget(480.00);

        List<Map<String, Object>> categorySummary = new ArrayList<>();
        categorySummary.add(createMap("category", "Food", "total", 1200.00));
        categorySummary.add(createMap("category", "Transport", "total", 850.00));
        categorySummary.add(createMap("category", "Shopping", "total", 950.00));
        categorySummary.add(createMap("category", "Bills", "total", 1100.00));
        categorySummary.add(createMap("category", "Other", "total", 420.00));
        dto.setCategorySummary(categorySummary);

        List<Map<String, Object>> monthlyTrend = new ArrayList<>();
        monthlyTrend.add(createMap("month", 1, "total", 3800.00));
        monthlyTrend.add(createMap("month", 2, "total", 4100.00));
        monthlyTrend.add(createMap("month", 3, "total", 3900.00));
        monthlyTrend.add(createMap("month", 4, "total", 4520.00));
        dto.setMonthlyTrend(monthlyTrend);

        List<Map<String, Object>> recentTransactions = new ArrayList<>();
        recentTransactions.add(createMap("title", "Grocery Shopping", "amount", 250.00, "category", "Food", "date", "2026-04-15"));
        recentTransactions.add(createMap("title", "Bus Pass", "amount", 50.00, "category", "Transport", "date", "2026-04-14"));
        recentTransactions.add(createMap("title", "Electricity Bill", "amount", 320.00, "category", "Bills", "date", "2026-04-13"));
        recentTransactions.add(createMap("title", "New Shoes", "amount", 180.00, "category", "Shopping", "date", "2026-04-12"));
        recentTransactions.add(createMap("title", "Lunch Out", "amount", 35.00, "category", "Food", "date", "2026-04-11"));
        dto.setRecentTransactions(recentTransactions);

        return dto;
    }

    private DashboardDTO buildDashboard(Long userId, Integer year, Integer month) {
        DashboardDTO dto = new DashboardDTO();

        if (year == null) year = LocalDate.now().getYear();
        if (month == null) month = LocalDate.now().getMonthValue();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        Double totalExpense = expenseRepository.getTotalExpenseForPeriod(userId, start, end);
        Long expenseCount = expenseRepository.countByUserId(userId);

        List<Expense> recentList = expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, start, end);
        Double lastExpense = recentList.isEmpty() ? 0.0 : recentList.get(0).getAmount();

        Optional<MonthlyBudget> budgetOpt = budgetRepository.findByUserIdAndMonth(userId, String.format("%d-%02d", year, month));
        Double monthlyBudget = budgetOpt.map(MonthlyBudget::getBudgetAmount).orElse(0.0);
        Double remainingBudget = Math.max(0, monthlyBudget - totalExpense);

        dto.setTotalExpense(totalExpense);
        dto.setLastExpense(lastExpense);
        dto.setExpenseCount(expenseCount);
        dto.setMonthlyBudget(monthlyBudget);
        dto.setRemainingBudget(remainingBudget);

        List<Map<String, Object>> categorySummary = expenseRepository.getCategorySummary(userId, start, end);
        dto.setCategorySummary(categorySummary != null ? categorySummary : new ArrayList<>());

        List<Map<String, Object>> monthlyTrend = expenseRepository.getMonthlyTrend(userId, year);
        dto.setMonthlyTrend(monthlyTrend != null ? monthlyTrend : new ArrayList<>());

        List<Map<String, Object>> transactions = recentList.stream()
                .limit(10)
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", e.getId());
                    m.put("title", e.getTitle());
                    m.put("amount", e.getAmount());
                    m.put("category", e.getCategory());
                    m.put("date", e.getDate().toString());
                    return m;
                })
                .collect(Collectors.toList());
        dto.setRecentTransactions(transactions);

        return dto;
    }

    public boolean isBudgetExceeded(String username, Integer year, Integer month) {
        User user = userService.getUserByUsername(username);
        if (year == null) year = LocalDate.now().getYear();
        if (month == null) month = LocalDate.now().getMonthValue();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        Double totalExpense = expenseRepository.getTotalExpenseForPeriod(user.getId(), start, end);
        Optional<MonthlyBudget> budgetOpt = budgetRepository.findByUserIdAndMonth(user.getId(),
                String.format("%d-%02d", year, month));
        Double monthlyBudget = budgetOpt.map(MonthlyBudget::getBudgetAmount).orElse(0.0);

        return monthlyBudget > 0 && totalExpense > monthlyBudget;
    }

    private Map<String, Object> createMap(Object... kv) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            map.put(kv[i].toString(), kv[i + 1]);
        }
        return map;
    }
}
