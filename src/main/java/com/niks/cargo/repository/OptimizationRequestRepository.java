package com.niks.cargo.repository;

import com.niks.cargo.model.OptimizationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptimizationRequestRepository extends JpaRepository<OptimizationRequest, String> {
}
