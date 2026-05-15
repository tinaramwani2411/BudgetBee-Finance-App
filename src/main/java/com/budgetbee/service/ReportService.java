package com.budgetbee.service;

import com.budgetbee.model.Expense;
import com.budgetbee.model.User;
import com.budgetbee.repository.ExpenseRepository;
import com.opencsv.CSVWriter;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ReportService {

    private final ExpenseRepository expenseRepository;
    private final UserService userService;

    public ReportService(ExpenseRepository expenseRepository, UserService userService) {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
    }

    public byte[] generatePdfReport(String username, int year, int month) throws Exception {
        User user = userService.getUserByUsername(username);
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Expense> expenses = expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(user.getId(), start, end);
        Double totalExpense = expenseRepository.getTotalExpenseForPeriod(user.getId(), start, end);
        List<Map<String, Object>> categorySummary = expenseRepository.getCategorySummary(user.getId(), start, end);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(document, page);
            cs.setFont(PDType1Font.HELVETICA_BOLD, 22);
            cs.beginText();
            cs.newLineAtOffset(50, 750);
            cs.showText("BudgetBee - Expense Report");
            cs.endText();

            cs.setFont(PDType1Font.HELVETICA, 12);
            addLine(cs, 50, 720, "User: " + user.getUsername());

            String monthName = java.time.YearMonth.of(year, month)
                    .getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            addLine(cs, 50, 700, "Month: " + monthName + " " + year);
            addLine(cs, 50, 680, "Total Expense: Rs." + String.format("%.2f", totalExpense));

            int y = 650;
            cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
            addLine(cs, 50, y, "Expenses:");
            y -= 25;

            cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
            addLine(cs, 50, y, String.format("%-4s %-25s %-12s %-15s %s", "#", "Title", "Amount", "Category", "Date"));
            y -= 18;

            cs.setFont(PDType1Font.HELVETICA, 10);
            int idx = 1;
            for (Expense e : expenses) {
                if (y < 50) {
                    cs.endText();
                    cs.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    cs = new PDPageContentStream(document, page);
                    cs.setFont(PDType1Font.HELVETICA, 10);
                    cs.beginText();
                    cs.newLineAtOffset(50, 750);
                    y = 750;
                }
                String title = e.getTitle().length() > 22 ? e.getTitle().substring(0, 22) + "..." : e.getTitle();
                String line = String.format("%-4d %-25s Rs.%-8.2f %-15s %s",
                        idx++, title, e.getAmount(), e.getCategory(), e.getDate().toString());
                cs.showText(line);
                cs.newLineAtOffset(0, -15);
                y -= 15;
            }

            cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
            cs.newLineAtOffset(0, -25);
            y -= 25;
            cs.showText("Category Summary:");
            cs.newLineAtOffset(0, -20);
            y -= 20;

            cs.setFont(PDType1Font.HELVETICA, 10);
            for (Map<String, Object> entry : categorySummary) {
                String cat = (String) entry.get("category");
                Double t = (Double) entry.get("total");
                cs.showText(String.format("%-20s Rs.%.2f", cat, t));
                cs.newLineAtOffset(0, -15);
            }

            cs.endText();
            cs.close();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    private void addLine(PDPageContentStream cs, int x, int y, String text) throws Exception {
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    public byte[] generateCsvReport(String username, int year, int month) throws Exception {
        User user = userService.getUserByUsername(username);
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Expense> expenses = expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(user.getId(), start, end);
        Double totalExpense = expenseRepository.getTotalExpenseForPeriod(user.getId(), start, end);
        List<Map<String, Object>> categorySummary = expenseRepository.getCategorySummary(user.getId(), start, end);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw)) {

            String monthName = java.time.YearMonth.of(year, month)
                    .getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            writer.writeNext(new String[]{"BudgetBee Expense Report"});
            writer.writeNext(new String[]{"User", user.getUsername()});
            writer.writeNext(new String[]{"Month", monthName + " " + year});
            writer.writeNext(new String[]{"Total Expense", String.format("Rs.%.2f", totalExpense)});
            writer.writeNext(new String[]{""});
            writer.writeNext(new String[]{"Title", "Amount", "Category", "Date"});

            for (Expense e : expenses) {
                writer.writeNext(new String[]{
                        e.getTitle(),
                        String.format("%.2f", e.getAmount()),
                        e.getCategory(),
                        e.getDate().toString()
                });
            }

            writer.writeNext(new String[]{""});
            writer.writeNext(new String[]{"Category Summary"});
            writer.writeNext(new String[]{"Category", "Total"});
            for (Map<String, Object> entry : categorySummary) {
                writer.writeNext(new String[]{
                        (String) entry.get("category"),
                        String.format("Rs.%.2f", (Double) entry.get("total"))
                });
            }
        }

        return baos.toByteArray();
    }
}
