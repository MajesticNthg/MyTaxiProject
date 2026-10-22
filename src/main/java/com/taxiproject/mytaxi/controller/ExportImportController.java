package com.taxiproject.mytaxi.controller;

import com.taxiproject.mytaxi.Dto.*;
import com.taxiproject.mytaxi.entity.Trip;
import com.taxiproject.mytaxi.service.ExportImportService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/export-import")
public class ExportImportController {

    private final ExportImportService service;

    public ExportImportController(ExportImportService service) {
        this.service = service;
    }

    @GetMapping("/enterprises")
    public ResponseEntity<?> exportEnterprises(
            @RequestParam(defaultValue = "json") String format,
            HttpServletResponse response
    ) throws Exception {
        List<EnterpriseExportDto> data = service.exportEnterprises();
        return writeResponse(data, format, "enterprises", response);
    }

    @GetMapping("/cars")
    public ResponseEntity<?> exportCars(
            @RequestParam UUID enterpriseId,
            @RequestParam(defaultValue = "json") String format,
            HttpServletResponse response
    ) throws Exception {
        List<CarExportDto> data = service.exportCars(enterpriseId);
        return writeResponse(data, format, "cars", response);
    }

    @GetMapping("/trips")
    public ResponseEntity<?> exportTrips(
            @RequestParam UUID carId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "json") String format,
            HttpServletResponse response
    ) throws Exception {
        List<TripExportDto> data = service.exportTrips(carId, from, to);
        return writeResponse(data, format, "trips", response);
    }

    @PostMapping("/enterprises")
    public ResponseEntity<?> importEnterprises(@RequestParam("file") MultipartFile file) {
        try {
            int count = service.importEnterprises(file);
            return ResponseEntity.ok(Map.of("imported", count));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Дубликат данных"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/cars")
    public ResponseEntity<?> importCars(
            @RequestParam("file") MultipartFile file,
            @RequestParam UUID enterpriseId
    ) throws Exception {
        int count = service.importCars(file, enterpriseId);
        return ResponseEntity.ok(Map.of("imported", count));
    }

    @PostMapping("/trips")
    public ResponseEntity<?> importTrips(
            @RequestParam("file") MultipartFile file,
            @RequestParam UUID carId,
            @RequestParam(required = false) String startAddress
    ) {
        try {
            int count = service.importTrips(file, carId, startAddress);
            return ResponseEntity.ok(Map.of("imported", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/trips/single")
    public ResponseEntity<?> importSingleTrip(
            @RequestBody TripSingleImportDto dto,
            @RequestParam UUID carId
    ) {
        try {
            Trip trip = service.importSingleTripByAddress(dto, carId);
            return ResponseEntity.ok(Map.of(
                    "tripId", trip.getId(),
                    "message", "Поездка создана"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }


    private ResponseEntity<?> writeResponse(List<?> data, String format, String filename,
                                            HttpServletResponse response) throws Exception {
        if ("csv".equalsIgnoreCase(format)) {
            StringWriter stringWriter = new StringWriter();
            service.writeCsv(data, stringWriter);
            byte[] bytes = stringWriter.toString().getBytes(StandardCharsets.UTF_8);

            byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            byte[] bytesWithBom = new byte[bom.length + bytes.length];
            System.arraycopy(bom, 0, bytesWithBom, 0, bom.length);
            System.arraycopy(bytes, 0, bytesWithBom, bom.length, bytes.length);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + ".csv\"")
                    .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                    .body(bytesWithBom);
        }

        return ResponseEntity.ok(data);
    }
}
