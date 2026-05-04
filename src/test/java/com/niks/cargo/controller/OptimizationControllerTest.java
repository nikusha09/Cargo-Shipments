package com.niks.cargo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.niks.cargo.dto.request.OptimizationRequestDto;
import com.niks.cargo.dto.request.ShipmentDto;
import com.niks.cargo.dto.response.OptimizationResponseDto;
import com.niks.cargo.exception.ResourceNotFoundException;
import com.niks.cargo.service.OptimizationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OptimizationController.class)
class OptimizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private OptimizationService optimizationService;

    @Test
    void shouldReturnCreatedWhenOptimizationSucceeds() throws Exception {
        OptimizationRequestDto request = buildRequest();
        OptimizationResponseDto response = buildResponse();

        when(optimizationService.optimize(any())).thenReturn(response);

        mockMvc.perform(post("/api/optimizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.requestId").value("test-id"))
                .andExpect(jsonPath("$.totalRevenue").value(320))
                .andExpect(jsonPath("$.totalVolume").value(15))
                .andExpect(jsonPath("$.selectedShipments").isArray());
    }

    @Test
    void shouldReturnEmptyListWhenNoShipmentsFit() throws Exception {
        OptimizationRequestDto request = buildRequest();
        OptimizationResponseDto response = new OptimizationResponseDto();
        response.setSelectedShipments(List.of());
        response.setTotalRevenue(BigDecimal.ZERO);
        response.setTotalVolume(0);

        when(optimizationService.optimize(any())).thenReturn(response);

        mockMvc.perform(post("/api/optimizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.selectedShipments").isEmpty())
                .andExpect(jsonPath("$.totalRevenue").value(0));
    }

    @Test
    void shouldReturnBadRequestWhenMaxVolumeIsMissing() throws Exception {
        OptimizationRequestDto request = new OptimizationRequestDto();
        request.setAvailableShipments(List.of(shipment("Parcel A", 5, 120)));

        mockMvc.perform(post("/api/optimizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenShipmentsListIsEmpty() throws Exception {
        OptimizationRequestDto request = new OptimizationRequestDto();
        request.setMaxVolume(15);
        request.setAvailableShipments(List.of());

        mockMvc.perform(post("/api/optimizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnAllOptimizationRequests() throws Exception {
        when(optimizationService.getAll()).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/optimizations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].requestId").value("test-id"));
    }

    @Test
    void shouldReturnOptimizationRequestById() throws Exception {
        when(optimizationService.getById("test-id")).thenReturn(buildResponse());

        mockMvc.perform(get("/api/optimizations/test-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value("test-id"));
    }

    @Test
    void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        when(optimizationService.getById(eq("non-existing-id")))
                .thenThrow(new ResourceNotFoundException("Optimization request not found with id: non-existing-id"));

        mockMvc.perform(get("/api/optimizations/non-existing-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Optimization request not found with id: non-existing-id"));
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

    private OptimizationResponseDto buildResponse() {
        OptimizationResponseDto response = new OptimizationResponseDto();
        response.setRequestId("test-id");
        response.setTotalRevenue(BigDecimal.valueOf(320));
        response.setTotalVolume(15);
        response.setCreatedAt(LocalDateTime.now());
        response.setSelectedShipments(List.of(
                shipment("Parcel A", 5, 120),
                shipment("Parcel B", 10, 200)
        ));
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
