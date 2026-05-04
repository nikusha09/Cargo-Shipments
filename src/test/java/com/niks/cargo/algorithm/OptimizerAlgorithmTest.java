package com.niks.cargo.algorithm;

import com.niks.cargo.dto.request.ShipmentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OptimizerAlgorithmTest {

    private OptimizerAlgorithm algorithm;

    @BeforeEach
    void setUp() {
        algorithm = new OptimizerAlgorithm();
    }

    @Test
    void shouldSelectOptimalShipments() {
        List<ShipmentDto> shipments = List.of(
                shipment("Parcel A", 5, 120),
                shipment("Parcel B", 10, 200),
                shipment("Parcel C", 3, 80),
                shipment("Parcel D", 8, 160)
        );

        List<ShipmentDto> result = algorithm.chooseMax(shipments, 15);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ShipmentDto::getName)
                .containsExactlyInAnyOrder("Parcel A", "Parcel B");
        assertThat(totalRevenue(result)).isEqualByComparingTo(BigDecimal.valueOf(320));
        assertThat(totalVolume(result)).isEqualTo(15);
    }

    @Test
    void shouldReturnEmptyListWhenNoShipmentFits() {
        List<ShipmentDto> shipments = List.of(
                shipment("Parcel A", 10, 120),
                shipment("Parcel B", 20, 200)
        );

        List<ShipmentDto> result = algorithm.chooseMax(shipments, 5);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenInputIsEmpty() {
        List<ShipmentDto> result = algorithm.chooseMax(List.of(), 15);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldSelectSingleShipmentThatFits() {
        List<ShipmentDto> shipments = List.of(
                shipment("Parcel A", 5, 120),
                shipment("Parcel B", 20, 200)
        );

        List<ShipmentDto> result = algorithm.chooseMax(shipments, 5);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Parcel A");
    }

    @Test
    void shouldSelectShipmentWithExactCapacityFit() {
        List<ShipmentDto> shipments = List.of(
                shipment("Parcel A", 15, 300),
                shipment("Parcel B", 10, 200)
        );

        List<ShipmentDto> result = algorithm.chooseMax(shipments, 15);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Parcel A");
        assertThat(totalRevenue(result)).isEqualByComparingTo(BigDecimal.valueOf(300));
    }

    // helper methods
    private ShipmentDto shipment(String name, int volume, int revenue) {
        ShipmentDto dto = new ShipmentDto();
        dto.setName(name);
        dto.setVolume(volume);
        dto.setRevenue(BigDecimal.valueOf(revenue));
        return dto;
    }

    private BigDecimal totalRevenue(List<ShipmentDto> shipments) {
        return shipments.stream()
                .map(ShipmentDto::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int totalVolume(List<ShipmentDto> shipments) {
        return shipments.stream()
                .mapToInt(ShipmentDto::getVolume)
                .sum();
    }
}
