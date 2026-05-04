package com.niks.cargo.mapper;

import com.niks.cargo.dto.request.OptimizationRequestDto;
import com.niks.cargo.dto.request.ShipmentDto;
import com.niks.cargo.dto.response.OptimizationResponseDto;
import com.niks.cargo.model.OptimizationRequest;
import com.niks.cargo.model.SelectedShipment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class CustomMapper {

    public OptimizationRequest toOptimizationRequest(
            OptimizationRequestDto request,
            List<ShipmentDto> shipments) {
        OptimizationRequest optimizationRequest = new OptimizationRequest();
        optimizationRequest.setId(UUID.randomUUID().toString());
        optimizationRequest.setMaxVolume(request.getMaxVolume());
        optimizationRequest.setTotalVolume(shipments.stream()
                .mapToInt(ShipmentDto::getVolume)
                .sum()
        );
        optimizationRequest.setTotalRevenue(shipments.stream()
                .map(shipmentDto -> shipmentDto.getRevenue())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        optimizationRequest.setCreatedAt(LocalDateTime.now());

        List<SelectedShipment> selectedShipments = shipments.stream()
                .map(dto -> toSelectedShipment(dto))
                .toList();
        selectedShipments.forEach(s -> s.setOptimizationRequest(optimizationRequest));
        optimizationRequest.setSelectedShipments(selectedShipments);
        return optimizationRequest;
    }

    private SelectedShipment toSelectedShipment(ShipmentDto shipment) {
        SelectedShipment selectedShipment = new SelectedShipment();
        selectedShipment.setName(shipment.getName());
        selectedShipment.setVolume(shipment.getVolume());
        selectedShipment.setRevenue(shipment.getRevenue());
        return selectedShipment;
    }

    public OptimizationResponseDto toOptimizationResponse(
            OptimizationRequest optimizationRequest) {
        OptimizationResponseDto response = new OptimizationResponseDto();
        response.setSelectedShipments(optimizationRequest.getSelectedShipments().stream()
                .map(s -> toShipmentDto(s))
                .toList()
        );
        response.setTotalRevenue(optimizationRequest.getTotalRevenue());
        response.setTotalVolume(optimizationRequest.getTotalVolume());
        response.setRequestId(optimizationRequest.getId());
        response.setCreatedAt(optimizationRequest.getCreatedAt());
        return response;
    }

    private ShipmentDto toShipmentDto(SelectedShipment shipment) {
        ShipmentDto shipmentDto = new ShipmentDto();
        shipmentDto.setName(shipment.getName());
        shipmentDto.setRevenue(shipment.getRevenue());
        shipmentDto.setVolume(shipment.getVolume());
        return shipmentDto;
    }
}
