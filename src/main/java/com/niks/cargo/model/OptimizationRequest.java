package com.niks.cargo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "optimization_requests")
@Getter
@Setter
@NoArgsConstructor
public class OptimizationRequest {

    @Id
    private String id;

    @Column(nullable = false)
    private Integer maxVolume;

    @Column(nullable = false)
    private Integer totalVolume;

    @Column(nullable = false)
    private BigDecimal totalRevenue;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "optimizationRequest", cascade = CascadeType.ALL)
    private List<SelectedShipment> selectedShipments = new ArrayList<>();
}
