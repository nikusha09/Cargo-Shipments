package com.niks.cargo.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class OptimizationRequestDto {

    @NotNull
    @NotEmpty
    private List<ShipmentDto> availableShipments;

    @NotNull
    @Positive
    private Integer maxVolume;
}
