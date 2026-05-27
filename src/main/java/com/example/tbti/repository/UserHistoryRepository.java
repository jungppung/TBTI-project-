package com.example.tbti.repository;

import com.example.tbti.domain.User;
import com.example.tbti.domain.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {

    List<UserHistory> findByUserOrderByCreatedAtDesc(User user);
}
