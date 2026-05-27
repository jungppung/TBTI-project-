package com.example.tbti.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_history")
@Getter
@Setter
@NoArgsConstructor
public class UserHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "input_budget", nullable = false)
    private Integer inputBudget;

    @Column(name = "travel_days", nullable = false)
    private Integer travelDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private CityCost city;

    @Enumerated(EnumType.STRING)
    @Column(name = "consumption_style", length = 20, nullable = false)
    private ConsumptionStyle consumptionStyle;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_style", length = 20, nullable = false)
    private ActivityStyle activityStyle;

    @Column(name = "calculated_tbti_code", length = 3, nullable = false)
    private String calculatedTbtiCode;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
