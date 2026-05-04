package com.niks.cargo.service;

import com.niks.cargo.algorithm.OptimizerAlgorithm;
import com.niks.cargo.dto.request.OptimizationRequestDto;
import com.niks.cargo.dto.request.ShipmentDto;
import com.niks.cargo.dto.response.OptimizationResponseDto;
import com.niks.cargo.exception.ResourceNotFoundException;
import com.niks.cargo.model.OptimizationRequest;
import com.niks.cargo.repository.OptimizationRequestRepository;
import com.niks.cargo.mapper.CustomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptimizationService {

    private final OptimizerAlgorithm algorithm;
    private final OptimizationRequestRepository repository;
    private final CustomMapper mapper;

    @Transactional
    public OptimizationResponseDto optimize(OptimizationRequestDto request) {
        log.info("Optimization request received with maxVolume: {}", request.getMaxVolume());

        List<ShipmentDto> shipments =
                algorithm.chooseMax(request.getAvailableShipments(), request.getMaxVolume());
        log.info("Algorithm selected {} shipments", shipments.size());

        OptimizationRequest optimizationRequest =
                mapper.toOptimizationRequest(request, shipments);
        repository.save(optimizationRequest);
        log.info("Optimization request saved with id: {}", optimizationRequest.getId());

        return mapper.toOptimizationResponse(optimizationRequest);
    }

    @Transactional(readOnly = true)
    public List<OptimizationResponseDto> getAll() {
        log.info("Fetching all optimization requests");
        List<OptimizationRequest> optimizationRequests = repository.findAll();
        log.info("Returning {} optimization requests", optimizationRequests.size());
        return optimizationRequests.stream()
                .map(mapper::toOptimizationResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OptimizationResponseDto getById(String id) {
        log.info("Fetching optimization request with id: {}", id);
        OptimizationRequest optimizationRequest = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Optimization request not found with id: {}", id);
                    return new ResourceNotFoundException("Optimization request not found with id: " + id);
                });
        log.info("Returning optimization request with id: {}", id);
        return mapper.toOptimizationResponse(optimizationRequest);
    }
}
