package com.niks.cargo.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShipmentDto {
    private Integer volume;
    private BigDecimal revenue;
    private String name;
}
