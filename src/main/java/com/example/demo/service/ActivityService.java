package com.example.demo.service;

import com.example.demo.entity.Activity;
import com.example.demo.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityService {
    
    @Autowired
    private ActivityRepository activityRepository;
    
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }
    
    public Activity getActivityById(Long id) {
        return activityRepository.findById(id).orElse(null);
    }
    
    public Activity createActivity(Activity activity) {
        activity.setStatus(determineStatus(activity));
        return activityRepository.save(activity);
    }
    
    public Activity updateActivity(Long id, Activity activityDetails) {
        Activity activity = getActivityById(id);
        if (activity != null) {
            activity.setTitle(activityDetails.getTitle());
            activity.setDescription(activityDetails.getDescription());
            activity.setLocation(activityDetails.getLocation());
            activity.setStartDate(activityDetails.getStartDate());
            activity.setEndDate(activityDetails.getEndDate());
            activity.setImage(activityDetails.getImage());
            activity.setMaxParticipants(activityDetails.getMaxParticipants());
            activity.setStatus(determineStatus(activity));
            return activityRepository.save(activity);
        }
        return null;
    }
    
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }
    
    public List<Activity> getUpcomingActivities() {
        return activityRepository.findByStartDateAfter(LocalDateTime.now());
    }
    
    public boolean registerParticipant(Long activityId) {
        Activity activity = getActivityById(activityId);
        if (activity != null && activity.getCurrentParticipants() < activity.getMaxParticipants()) {
            activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
            activityRepository.save(activity);
            return true;
        }
        return false;
    }
    
    private String determineStatus(Activity activity) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getStartDate())) {
            return "UPCOMING";
        } else if (now.isAfter(activity.getEndDate())) {
            return "FINISHED";
        } else {
            return "ONGOING";
        }
    }
}