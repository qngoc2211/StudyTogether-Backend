package com.example.demo.repository;

import com.example.demo.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByStatus(String status);
    List<Activity> findByStartDateAfter(LocalDateTime date);
    List<Activity> findByStartDateBetween(LocalDateTime start, LocalDateTime end);
    
    // Sửa method này
    List<Activity> findByTitleContainingOrDescriptionContaining(String title, String description);
}