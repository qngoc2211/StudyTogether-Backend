package com.example.demo.controller.admin;

import com.example.demo.entity.Activity;
import com.example.demo.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/activities")
@CrossOrigin(origins = "*")
public class AdminActivityController {

    @Autowired
    private ActivityRepository activityRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
        return activityRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Activity createActivity(@RequestBody Activity activity) {
        return activityRepository.save(activity);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Activity> updateActivity(@PathVariable Long id, @RequestBody Activity activityDetails) {
        return activityRepository.findById(id)
                .map(activity -> {
                    activity.setTitle(activityDetails.getTitle());
                    activity.setDescription(activityDetails.getDescription());
                    activity.setLocation(activityDetails.getLocation());
                    activity.setStartDate(activityDetails.getStartDate());
                    activity.setEndDate(activityDetails.getEndDate());
                    activity.setImage(activityDetails.getImage());
                    activity.setMaxParticipants(activityDetails.getMaxParticipants());
                    return ResponseEntity.ok(activityRepository.save(activity));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteActivity(@PathVariable Long id) {
        return activityRepository.findById(id)
                .map(activity -> {
                    activityRepository.delete(activity);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}