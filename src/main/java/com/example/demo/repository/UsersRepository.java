package com.example.demo.repository;

import com.example.demo.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {

    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String email);   // thêm nếu chưa có

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);         // 🔥 THÊM DÒNG NÀY
}