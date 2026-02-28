package com.example.demo.controller.admin;

import com.example.demo.entity.Activity;
import com.example.demo.repository.ActivityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/activities")
@PreAuthorize("hasRole('ADMIN')")
public class AdminActivityController {

    private final ActivityRepository activityRepository;

    public AdminActivityController(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    // ===================== GET ALL =====================
    @GetMapping
    public ResponseEntity<List<Activity>> getAllActivities() {
        List<Activity> activities = activityRepository.findAll();
        return ResponseEntity.ok(activities);
    }

    // ===================== GET BY ID =====================
    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
        return activityRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ===================== CREATE =====================
    @PostMapping
    public ResponseEntity<Activity> createActivity(@RequestBody Activity activity) {

        activity.setId(null); // đảm bảo tạo mới

        Activity savedActivity = activityRepository.save(activity);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedActivity);
    }

    // ===================== UPDATE =====================
    @PutMapping("/{id}")
    public ResponseEntity<Activity> updateActivity(
            @PathVariable Long id,
            @RequestBody Activity activityDetails) {

        return activityRepository.findById(id)
                .map(existingActivity -> {

                    existingActivity.setTitle(activityDetails.getTitle());
                    existingActivity.setDescription(activityDetails.getDescription());
                    existingActivity.setLocation(activityDetails.getLocation());
                    existingActivity.setStartDate(activityDetails.getStartDate());
                    existingActivity.setEndDate(activityDetails.getEndDate());
                    existingActivity.setImage(activityDetails.getImage());
                    existingActivity.setMaxParticipants(activityDetails.getMaxParticipants());

                    Activity updatedActivity = activityRepository.save(existingActivity);
                    return ResponseEntity.ok(updatedActivity);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ===================== DELETE =====================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {

        if (!activityRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        activityRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}