package com.budgetbee.controller;

import com.budgetbee.dto.DashboardDTO;
import com.budgetbee.model.MonthlyBudget;
import com.budgetbee.repository.MonthlyBudgetRepository;
import com.budgetbee.service.DashboardService;
import com.budgetbee.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;
    private final MonthlyBudgetRepository budgetRepository;

    public DashboardController(DashboardService dashboardService,
                               UserService userService,
                               MonthlyBudgetRepository budgetRepository) {
        this.dashboardService = dashboardService;
        this.userService = userService;
        this.budgetRepository = budgetRepository;
    }

    @GetMapping("/public")
    public ResponseEntity<DashboardDTO> getPublicDashboard() {
        return ResponseEntity.ok(dashboardService.getPublicDashboard());
    }

    @GetMapping
    public ResponseEntity<DashboardDTO> getUserDashboard(
            Principal principal,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return ResponseEntity.ok(
                dashboardService.getUserDashboard(principal.getName(), year, month));
    }

    @GetMapping("/budget-alert")
    public ResponseEntity<Map<String, Boolean>> checkBudgetAlert(
            Principal principal,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        boolean exceeded = dashboardService.isBudgetExceeded(principal.getName(), year, month);
        return ResponseEntity.ok(Map.of("exceeded", exceeded));
    }

    @PostMapping("/budget")
    public ResponseEntity<Map<String, String>> setMonthlyBudget(
            Principal principal,
            @RequestBody Map<String, Object> body) {

        var user = userService.getUserByUsername(principal.getName());
        int year = body.get("year") != null ? Integer.parseInt(body.get("year").toString()) : LocalDate.now().getYear();
        int month = body.get("month") != null ? Integer.parseInt(body.get("month").toString()) : LocalDate.now().getMonthValue();
        String monthKey = String.format("%d-%02d", year, month);
        Double amount = Double.valueOf(body.get("amount").toString());

        MonthlyBudget budget = budgetRepository.findByUserIdAndMonth(user.getId(), monthKey)
                .orElse(new MonthlyBudget(user, monthKey, amount));
        budget.setBudgetAmount(amount);

        budgetRepository.save(budget);
        return ResponseEntity.ok(Map.of("message", "Budget set successfully"));
    }

    @GetMapping("/budget")
    public ResponseEntity<Map<String, Object>> getMonthlyBudget(
            Principal principal,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        var user = userService.getUserByUsername(principal.getName());
        if (year == null) year = LocalDate.now().getYear();
        if (month == null) month = LocalDate.now().getMonthValue();
        String monthKey = String.format("%d-%02d", year, month);

        var budget = budgetRepository.findByUserIdAndMonth(user.getId(), monthKey);
        Map<String, Object> result = Map.of(
                "amount", budget.map(MonthlyBudget::getBudgetAmount).orElse(0.0),
                "month", monthKey
        );
        return ResponseEntity.ok(result);
    }
}
