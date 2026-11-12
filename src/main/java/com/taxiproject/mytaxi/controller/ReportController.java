package com.taxiproject.mytaxi.controller;

import com.taxiproject.mytaxi.Dto.ReportRequestDto;
import com.taxiproject.mytaxi.Dto.ReportResponseDto;
import com.taxiproject.mytaxi.service.PdfExportService;
import com.taxiproject.mytaxi.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final PdfExportService pdfExportService;

    public ReportController(ReportService reportService, PdfExportService pdfExportService) {
        this.reportService = reportService;
        this.pdfExportService = pdfExportService;
    }

    @GetMapping("/types")
    public ResponseEntity<List<Map<String, String>>> getReportTypes() {
        return ResponseEntity.ok(List.of(
                Map.of("type", "MILEAGE", "name", "Пробег автомобиля", "description", "Общий пробег за период"),
                Map.of("type", "TRAVEL_TIME", "name", "Время в пути", "description", "Общее время в пути за период")
        ));
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateReport(@RequestBody ReportRequestDto request) {
        try {
            ReportResponseDto report = reportService.generateReport(request);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("export/pdf")
    public ResponseEntity<byte[]> exportReportToPdf(@RequestBody ReportRequestDto request) {
        try {
            ReportResponseDto report = reportService.generateReport(request);

            byte[] pdfBytes = pdfExportService.generateReportPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "report_" + request.getReportType() + ".pdf");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
