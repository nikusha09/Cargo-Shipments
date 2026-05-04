package com.niks.cargo.dto.response;

import com.niks.cargo.dto.request.ShipmentDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OptimizationResponseDto {
    private List<ShipmentDto> selectedShipments;
    private BigDecimal totalRevenue;
    private Integer totalVolume;
    private String requestId;
    private LocalDateTime createdAt;
}
