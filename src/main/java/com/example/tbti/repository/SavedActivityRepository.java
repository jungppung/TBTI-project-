package com.example.tbti.repository;

import com.example.tbti.domain.SavedActivity;
import com.example.tbti.domain.User;
import com.example.tbti.domain.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedActivityRepository extends JpaRepository<SavedActivity, Long> {

    List<SavedActivity> findByUser(User user);

    List<SavedActivity> findByUserHistory(UserHistory userHistory);
}
