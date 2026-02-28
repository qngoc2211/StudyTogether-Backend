package com.example.demo.controller.admin;

import com.example.demo.entity.Users;
import com.example.demo.repository.UsersRepository;
import com.example.demo.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "*")
public class AdminUserController {

    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAllUsers() {
        List<Users> users = usersRepository.findAll();
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {  // Đổi Long → Long
        return usersRepository.findById(id)
                .map(user -> ResponseEntity.ok(convertToDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Users createUser(@RequestBody Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Users> updateUser(@PathVariable Long id, @RequestBody Users userDetails) {  // Đổi Long → Long
        return usersRepository.findById(id)
                .map(user -> {
                    user.setUsername(userDetails.getUsername());
                    user.setEmail(userDetails.getEmail());
                    user.setFullName(userDetails.getFullName());  // Sửa getFullName()
                    user.setRole(userDetails.getRole());
                    user.setActive(userDetails.getActive());  // Sửa isActive() → getActive()
                    if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                    }
                    return ResponseEntity.ok(usersRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {  // Đổi Long → Long
        return usersRepository.findById(id)
                .map(user -> {
                    usersRepository.delete(user);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Users> toggleUserStatus(@PathVariable Long id) {  // Đổi Long → Long
        return usersRepository.findById(id)
                .map(user -> {
                    user.setActive(!user.getActive());  // Sửa isActive() → getActive()
                    return ResponseEntity.ok(usersRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/change-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Users> changeRole(@PathVariable Long id, @RequestParam String role) {  // Đổi Long → Long
        return usersRepository.findById(id)
                .map(user -> {
                    user.setRole(role);
                    return ResponseEntity.ok(usersRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private UserDTO convertToDTO(Users user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());  // Bây giờ là Long
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());  // Thêm dòng này
        dto.setRole(user.getRole());
        dto.setActive(user.getActive());  // Sửa isActive() → getActive()
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLogin(user.getLastLogin());  // Thêm dòng này
        dto.setTotalPoints(user.getPoints());  // Sửa getTotalPoints() → getPoints()
        return dto;
    }
}