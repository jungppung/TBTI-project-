package com.example.tbti.repository;

import com.example.tbti.domain.CityCost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityCostRepository extends JpaRepository<CityCost, Long> {

    Optional<CityCost> findByCurrencyCode(String currencyCode);
}
