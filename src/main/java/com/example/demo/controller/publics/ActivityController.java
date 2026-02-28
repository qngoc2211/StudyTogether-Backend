package com.example.demo.controller.publics;

import com.example.demo.entity.Activity;
import com.example.demo.repository.ActivityRepository;
import com.example.demo.dto.ActivityDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*")
public class ActivityController {

    @Autowired
    private ActivityRepository activityRepository;

    // Lấy tất cả hoạt động (chỉ hiện hoạt động công khai)
    @GetMapping
    public List<ActivityDTO> getAllActivities() {
        List<Activity> activities = activityRepository.findAll();
        return activities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Lấy hoạt động sắp diễn ra
    @GetMapping("/upcoming")
    public List<ActivityDTO> getUpcomingActivities() {
        List<Activity> activities = activityRepository.findByStartDateAfter(LocalDateTime.now());
        return activities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Lấy hoạt động đang diễn ra
    @GetMapping("/ongoing")
    public List<ActivityDTO> getOngoingActivities() {
        LocalDateTime now = LocalDateTime.now();
        List<Activity> activities = activityRepository.findByStartDateBeforeAndEndDateAfter(now, now);
        return activities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Lấy hoạt động theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(@PathVariable Long id) {
        return activityRepository.findById(id)
                .map(activity -> ResponseEntity.ok(convertToDTO(activity)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Đăng ký tham gia hoạt động (yêu cầu đăng nhập)
    @PostMapping("/{id}/register")
    public ResponseEntity<?> registerActivity(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        return activityRepository.findById(id)
                .map(activity -> {
                    // Kiểm tra thời gian
                    if (activity.getStartDate().isBefore(LocalDateTime.now())) {
                        return ResponseEntity.badRequest().body("Hoạt động đã bắt đầu hoặc kết thúc");
                    }
                    
                    // Kiểm tra số lượng
                    if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
                        return ResponseEntity.badRequest().body("Hoạt động đã đủ số lượng người tham gia");
                    }
                    
                    // Tăng số người tham gia
                    activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
                    activityRepository.save(activity);
                    
                    return ResponseEntity.ok().body("Đăng ký thành công");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Hủy đăng ký tham gia
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelRegistration(@PathVariable Long id) {
        return activityRepository.findById(id)
                .map(activity -> {
                    if (activity.getCurrentParticipants() > 0) {
                        activity.setCurrentParticipants(activity.getCurrentParticipants() - 1);
                        activityRepository.save(activity);
                    }
                    return ResponseEntity.ok().body("Hủy đăng ký thành công");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Tìm kiếm hoạt động
    @GetMapping("/search")
    public List<ActivityDTO> searchActivities(@RequestParam String keyword) {
        List<Activity> activities = activityRepository.findByTitleContainingOrDescriptionContaining(keyword, keyword);
        return activities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Lọc hoạt động theo thời gian
    @GetMapping("/filter")
    public List<ActivityDTO> filterActivities(
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to) {
        
        if (from == null) from = LocalDateTime.now();
        if (to == null) to = from.plusMonths(1);
        
        List<Activity> activities = activityRepository.findByStartDateBetween(from, to);
        return activities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Convert Entity to DTO
    private ActivityDTO convertToDTO(Activity activity) {
        ActivityDTO dto = new ActivityDTO();
        dto.setId(activity.getId());
        dto.setTitle(activity.getTitle());
        dto.setDescription(activity.getDescription());
        dto.setLocation(activity.getLocation());
        dto.setStartDate(activity.getStartDate());
        dto.setEndDate(activity.getEndDate());
        dto.setImage(activity.getImage());
        dto.setMaxParticipants(activity.getMaxParticipants());
        dto.setCurrentParticipants(activity.getCurrentParticipants());
        dto.setStatus(determineStatus(activity));
        return dto;
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