package com.budgetbee.controller;

import com.budgetbee.model.Expense;
import com.budgetbee.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses(
            Principal principal,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        if (category != null || year != null || month != null) {
            return ResponseEntity.ok(
                    expenseService.getFilteredExpenses(principal.getName(), category, year, month));
        }
        return ResponseEntity.ok(expenseService.getUserExpenses(principal.getName()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Expense>> searchExpenses(
            Principal principal,
            @RequestParam String q) {
        return ResponseEntity.ok(expenseService.searchExpenses(principal.getName(), q));
    }

    @PostMapping
    public ResponseEntity<Expense> addExpense(
            Principal principal,
            @RequestBody Map<String, Object> body) {

        String title = (String) body.get("title");
        Double amount = Double.valueOf(body.get("amount").toString());
        String category = (String) body.get("category");
        LocalDate date = LocalDate.parse((String) body.get("date"));
        String description = (String) body.getOrDefault("description", "");

        Expense expense = expenseService.addExpense(principal.getName(), title, amount, category, date, description);
        return ResponseEntity.ok(expense);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(
            Principal principal,
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        String title = (String) body.get("title");
        Double amount = body.get("amount") != null ? Double.valueOf(body.get("amount").toString()) : null;
        String category = (String) body.get("category");
        LocalDate date = body.get("date") != null ? LocalDate.parse((String) body.get("date")) : null;
        String description = (String) body.getOrDefault("description", null);

        Expense expense = expenseService.updateExpense(id, principal.getName(), title, amount, category, date, description);
        return ResponseEntity.ok(expense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteExpense(Principal principal, @PathVariable Long id) {
        expenseService.deleteExpense(id, principal.getName());
        return ResponseEntity.ok(Map.of("message", "Expense deleted successfully"));
    }
}
