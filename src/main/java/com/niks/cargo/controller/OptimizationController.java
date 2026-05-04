package com.niks.cargo.controller;

import com.niks.cargo.dto.request.OptimizationRequestDto;
import com.niks.cargo.dto.response.OptimizationResponseDto;
import com.niks.cargo.service.OptimizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/optimizations")
@RequiredArgsConstructor
public class OptimizationController {

    private final OptimizationService optimizationService;

    @PostMapping
    public ResponseEntity<OptimizationResponseDto> optimize(
            @Valid @RequestBody OptimizationRequestDto request) {
        log.info("POST /api/optimizations - maxVolume: {}, shipments count: {}",
                request.getMaxVolume(), request.getAvailableShipments().size());
        OptimizationResponseDto response = optimizationService.optimize(request);
        log.info("POST /api/optimizations - completed with requestId: {}", response.getRequestId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<OptimizationResponseDto>> getAll() {
        log.info("GET /api/optimizations - fetching all optimization requests");
        List<OptimizationResponseDto> optimizations = optimizationService.getAll();
        log.info("GET /api/optimizations - returning {} results", optimizations.size());
        return ResponseEntity.ok(optimizations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptimizationResponseDto> getById(@PathVariable String id) {
        log.info("GET /api/optimizations/{} - fetching optimization request", id);
        OptimizationResponseDto response = optimizationService.getById(id);
        log.info("GET /api/optimations/{} - found and returning", id);
        return ResponseEntity.ok(response);
    }
}
