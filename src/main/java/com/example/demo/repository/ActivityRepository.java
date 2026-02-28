package com.example.demo.repository;

import com.example.demo.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByStatus(String status);
    List<Activity> findByStartDateAfter(LocalDateTime date);
    List<Activity> findByStartDateBeforeAndEndDateAfter(LocalDateTime start, LocalDateTime end);
    List<Activity> findByStartDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT a FROM Activity a WHERE a.title LIKE %:keyword% OR a.description LIKE %:keyword%")
    List<Activity> findByTitleContainingOrDescriptionContaining(@Param("keyword") String keyword, @Param("keyword2") String keyword2);
}