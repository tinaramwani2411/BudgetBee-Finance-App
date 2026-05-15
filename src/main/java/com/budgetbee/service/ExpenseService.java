package com.budgetbee.service;

import com.budgetbee.exception.ResourceNotFoundException;
import com.budgetbee.model.Expense;
import com.budgetbee.model.User;
import com.budgetbee.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserService userService;

    public ExpenseService(ExpenseRepository expenseRepository, UserService userService) {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
    }

    public List<Expense> getUserExpenses(String username) {
        User user = userService.getUserByUsername(username);
        return expenseRepository.findByUserIdOrderByDateDesc(user.getId());
    }

    public List<Expense> getUserExpensesByMonth(String username, int year, int month) {
        User user = userService.getUserByUsername(username);
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(user.getId(), start, end);
    }

    public List<Expense> searchExpenses(String username, String q) {
        User user = userService.getUserByUsername(username);
        return expenseRepository.searchByUserId(user.getId(), q);
    }

    public List<Expense> getFilteredExpenses(String username, String category, Integer year, Integer month) {
        User user = userService.getUserByUsername(username);
        LocalDate start = (year != null && month != null)
                ? LocalDate.of(year, month, 1)
                : LocalDate.of(2000, 1, 1);
        LocalDate end = (year != null && month != null)
                ? start.withDayOfMonth(start.lengthOfMonth())
                : LocalDate.of(2099, 12, 31);

        if (category != null && !category.isEmpty()) {
            return expenseRepository.findByUserIdAndCategoryAndDateBetweenOrderByDateDesc(
                    user.getId(), category, start, end);
        }
        return expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(user.getId(), start, end);
    }

    public Expense addExpense(String username, String title, Double amount, String category,
                               LocalDate date, String description) {
        User user = userService.getUserByUsername(username);
        Expense expense = new Expense(user, title, amount, category, date, description);
        return expenseRepository.save(expense);
    }

    public Expense updateExpense(Long id, String username, String title, Double amount,
                                  String category, LocalDate date, String description) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if (!expense.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You can only edit your own expenses");
        }

        if (title != null) expense.setTitle(title);
        if (amount != null) expense.setAmount(amount);
        if (category != null) expense.setCategory(category);
        if (date != null) expense.setDate(date);
        if (description != null) expense.setDescription(description);

        return expenseRepository.save(expense);
    }

    public void deleteExpense(Long id, String username) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if (!expense.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You can only delete your own expenses");
        }

        expenseRepository.delete(expense);
    }
}
