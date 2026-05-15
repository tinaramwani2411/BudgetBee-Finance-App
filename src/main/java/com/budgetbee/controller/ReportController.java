package com.budgetbee.controller;

import com.budgetbee.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            Principal principal,
            @RequestParam int year,
            @RequestParam int month) throws Exception {

        byte[] data = reportService.generatePdfReport(principal.getName(), year, month);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                String.format("budgetbee_report_%d_%02d.pdf", year, month));

        return ResponseEntity.ok().headers(headers).body(data);
    }

    @GetMapping("/csv")
    public ResponseEntity<byte[]> downloadCsv(
            Principal principal,
            @RequestParam int year,
            @RequestParam int month) throws Exception {

        byte[] data = reportService.generateCsvReport(principal.getName(), year, month);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment",
                String.format("budgetbee_report_%d_%02d.csv", year, month));

        return ResponseEntity.ok().headers(headers).body(data);
    }
}
