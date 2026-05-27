package com.example.tbti.repository;

import com.example.tbti.domain.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findByCurrencyCode(String currencyCode);
}
