package com.example.demo.service;

import com.example.demo.entity.Users;
import com.example.demo.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }
    
    public Users getUserById(Long id) {  // Đổi Integer → Long
        return usersRepository.findById(id).orElse(null);
    }
    
    public Users getUserByUsername(String username) {
        return usersRepository.findByUsername(username).orElse(null);
    }
    
    public Users createUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);
        user.setRole(user.getRole() != null ? user.getRole() : "ROLE_USER");
        return usersRepository.save(user);
    }
    
    public Users updateUser(Long id, Users userDetails) {  // Đổi Integer → Long
        Users user = getUserById(id);
        if (user != null) {
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            user.setFullName(userDetails.getFullName());  // Thêm dòng này
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            return usersRepository.save(user);
        }
        return null;
    }
    
    public void deleteUser(Long id) {  // Đổi Integer → Long
        usersRepository.deleteById(id);
    }
    
    public Users updateLastLogin(String username) {
        Users user = getUserByUsername(username);
        if (user != null) {
            user.setLastLogin(LocalDateTime.now());  // Thêm dòng này
            return usersRepository.save(user);
        }
        return null;
    }
    
    public boolean changePassword(Long id, String oldPassword, String newPassword) {  // Đổi Integer → Long
        Users user = getUserById(id);
        if (user != null && passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            usersRepository.save(user);
            return true;
        }
        return false;
    }
    
    public List<Users> getTopUsersByPoints(int limit) {
        return usersRepository.findTopByOrderByPointsDesc(limit);  // Sửa method name
    }
}