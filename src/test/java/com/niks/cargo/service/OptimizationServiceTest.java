package com.niks.cargo.service;

import com.niks.cargo.algorithm.OptimizerAlgorithm;
import com.niks.cargo.dto.request.OptimizationRequestDto;
import com.niks.cargo.dto.request.ShipmentDto;
import com.niks.cargo.dto.response.OptimizationResponseDto;
import com.niks.cargo.exception.ResourceNotFoundException;
import com.niks.cargo.mapper.CustomMapper;
import com.niks.cargo.model.OptimizationRequest;
import com.niks.cargo.repository.OptimizationRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OptimizationServiceTest {

    @Mock
    private OptimizerAlgorithm algorithm;

    @Mock
    private OptimizationRequestRepository repository;

    @Mock
    private CustomMapper mapper;

    @InjectMocks
    private OptimizationService service;

    @Test
    void shouldOptimizeAndPersistRequest() {
        OptimizationRequestDto request = buildRequest();
        List<ShipmentDto> selectedShipments = List.of(shipment("Parcel A", 5, 120));
        OptimizationRequest entity = buildEntity();
        OptimizationResponseDto expectedResponse = buildResponse();

        when(algorithm.chooseMax(request.getAvailableShipments(), request.getMaxVolume()))
                .thenReturn(selectedShipments);
        when(mapper.toOptimizationRequest(eq(request), eq(selectedShipments)))
                .thenReturn(entity);
        when(mapper.toOptimizationResponse(entity))
                .thenReturn(expectedResponse);

        OptimizationResponseDto result = service.optimize(request);

        assertThat(result).isEqualTo(expectedResponse);
        verify(repository).save(entity);
    }

    @Test
    void shouldReturnEmptyResponseWhenNoShipmentsFit() {
        OptimizationRequestDto request = buildRequest();
        OptimizationRequest entity = buildEntity();
        OptimizationResponseDto expectedResponse = new OptimizationResponseDto();
        expectedResponse.setSelectedShipments(List.of());
        expectedResponse.setTotalRevenue(BigDecimal.ZERO);
        expectedResponse.setTotalVolume(0);

        when(algorithm.chooseMax(any(), any())).thenReturn(List.of());
        when(mapper.toOptimizationRequest(any(), any())).thenReturn(entity);
        when(mapper.toOptimizationResponse(entity)).thenReturn(expectedResponse);

        OptimizationResponseDto result = service.optimize(request);

        assertThat(result.getSelectedShipments()).isEmpty();
        assertThat(result.getTotalRevenue()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldReturnAllOptimizationRequests() {
        OptimizationRequest entity = buildEntity();
        OptimizationResponseDto response = buildResponse();

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toOptimizationResponse(entity)).thenReturn(response);

        List<OptimizationResponseDto> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(response);
    }

    @Test
    void shouldReturnEmptyListWhenNoRequestsExist() {
        when(repository.findAll()).thenReturn(List.of());

        List<OptimizationResponseDto> result = service.getAll();

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnOptimizationRequestById() {
        OptimizationRequest entity = buildEntity();
        OptimizationResponseDto response = buildResponse();

        when(repository.findById("test-id")).thenReturn(Optional.of(entity));
        when(mapper.toOptimizationResponse(entity)).thenReturn(response);

        OptimizationResponseDto result = service.getById("test-id");

        assertThat(result).isEqualTo(response);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenIdNotFound() {
        when(repository.findById("non-existing-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById("non-existing-id"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("non-existing-id");
    }

    // helper methods
    private OptimizationRequestDto buildRequest() {
        OptimizationRequestDto request = new OptimizationRequestDto();
        request.setMaxVolume(15);
        request.setAvailableShipments(List.of(
                shipment("Parcel A", 5, 120),
                shipment("Parcel B", 10, 200)
        ));
        return request;
    }

    private OptimizationRequest buildEntity() {
        OptimizationRequest entity = new OptimizationRequest();
        entity.setId("test-id");
        entity.setMaxVolume(15);
        entity.setTotalVolume(15);
        entity.setTotalRevenue(BigDecimal.valueOf(320));
        entity.setCreatedAt(LocalDateTime.now());
        entity.setSelectedShipments(List.of());
        return entity;
    }

    private OptimizationResponseDto buildResponse() {
        OptimizationResponseDto response = new OptimizationResponseDto();
        response.setRequestId("test-id");
        response.setTotalRevenue(BigDecimal.valueOf(320));
        response.setTotalVolume(15);
        response.setCreatedAt(LocalDateTime.now());
        response.setSelectedShipments(List.of());
        return response;
    }

    private ShipmentDto shipment(String name, int volume, int revenue) {
        ShipmentDto dto = new ShipmentDto();
        dto.setName(name);
        dto.setVolume(volume);
        dto.setRevenue(BigDecimal.valueOf(revenue));
        return dto;
    }
}
