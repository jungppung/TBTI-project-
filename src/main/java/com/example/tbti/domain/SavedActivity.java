package com.example.tbti.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_activity")
@Getter
@Setter
@NoArgsConstructor
public class SavedActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saved_activity_id")
    private Long savedActivityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", nullable = false)
    private UserHistory userHistory;

    @Column(name = "activity_name", length = 255, nullable = false)
    private String activityName;

    @Column(name = "activity_type", length = 50)
    private String activityType;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
