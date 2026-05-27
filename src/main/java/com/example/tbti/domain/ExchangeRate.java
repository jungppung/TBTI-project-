package com.example.tbti.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rate")
@Getter
@Setter
@NoArgsConstructor
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rate_id")
    private Long rateId;

    @Column(name = "currency_code", length = 10, nullable = false, unique = true)
    private String currencyCode;

    @Column(name = "rate_to_krw", nullable = false, precision = 10, scale = 2)
    private BigDecimal rateToKrw;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
