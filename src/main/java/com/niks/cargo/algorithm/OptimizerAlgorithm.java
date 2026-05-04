package com.niks.cargo.algorithm;

import com.niks.cargo.dto.request.ShipmentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class OptimizerAlgorithm {

    public List<ShipmentDto> chooseMax(List<ShipmentDto> shipments, Integer maxVolume) {
        log.info("Starting algorithm with {} shipments and maxVolume: {}", shipments.size(), maxVolume);

        int n = shipments.size();
        // build DP table
        BigDecimal[][] dp = new BigDecimal[n + 1][maxVolume + 1];
        for (BigDecimal[] row : dp) {
            Arrays.fill(row, BigDecimal.ZERO);
        }

        // fill the table
        for (int i = 1; i <= n; i++) {
            ShipmentDto current = shipments.get(i - 1);
            int vol = current.getVolume();
            BigDecimal rev = current.getRevenue();

            for (int j = 0; j <= maxVolume; j++) {
                // option 1: don't take current shipment
                dp[i][j] = dp[i - 1][j];

                // option 2: take current shipment if it fits
                if (vol <= j) {
                    BigDecimal withCurrent = dp[i - 1][j - vol].add(rev);
                    if (withCurrent.compareTo(dp[i][j]) > 0) {
                        dp[i][j] = withCurrent;
                    }
                }
            }
        }

        // backtrack to find selected shipments
        List<ShipmentDto> selected = new ArrayList<>();
        int j = maxVolume;
        for (int i = n; i >= 1; i--) {
            if (!dp[i][j].equals(dp[i - 1][j])) {
                selected.add(shipments.get(i - 1));
                j -= shipments.get(i - 1).getVolume();
            }
        }

        log.info("Algorithm completed. Selected {} shipments with total revenue: {}",
                selected.size(),
                selected.stream().map(ShipmentDto::getRevenue).reduce(BigDecimal.ZERO, BigDecimal::add));

        return selected;
    }
}
