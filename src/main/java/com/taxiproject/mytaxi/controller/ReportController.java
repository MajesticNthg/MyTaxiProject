package com.taxiproject.mytaxi.controller;

import com.taxiproject.mytaxi.Dto.ReportRequestDto;
import com.taxiproject.mytaxi.Dto.ReportResponseDto;
import com.taxiproject.mytaxi.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // ✅ Список доступных отчётов (типы)
    @GetMapping("/types")
    public ResponseEntity<List<Map<String, String>>> getReportTypes() {
        return ResponseEntity.ok(List.of(
                Map.of("type", "MILEAGE", "name", "Пробег автомобиля", "description", "Общий пробег за период"),
                Map.of("type", "TRAVEL_TIME", "name", "Время в пути", "description", "Общее время в пути за период")
        ));
    }

    // ✅ Сгенерировать отчёт
    @PostMapping("/generate")
    public ResponseEntity<?> generateReport(@RequestBody ReportRequestDto request) {
        try {
            ReportResponseDto report = reportService.generateReport(request);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
