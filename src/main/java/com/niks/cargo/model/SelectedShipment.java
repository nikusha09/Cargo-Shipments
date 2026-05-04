package com.niks.cargo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "selected_shipments")
@Getter
@Setter
@NoArgsConstructor
public class SelectedShipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer volume;

    @Column(nullable = false)
    private BigDecimal revenue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "optimization_request_id", nullable = false)
    private OptimizationRequest optimizationRequest;
}
