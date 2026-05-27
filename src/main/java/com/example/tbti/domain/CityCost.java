package com.example.tbti.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "city_cost")
@Getter
@Setter
@NoArgsConstructor
public class CityCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "country_name", length = 50, nullable = false)
    private String countryName;

    @Column(name = "city_name", length = 50, nullable = false)
    private String cityName;

    @Column(name = "currency_code", length = 10, nullable = false)
    private String currencyCode;

    @Column(name = "standard_daily_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal standardDailyCost;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
