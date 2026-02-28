package com.example.demo.repository;

import com.example.demo.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByOrderByCreatedAtDesc();
    List<Post> findByAuthor(String author);
    List<Post> findByCategory(String category);
    List<Post> findByLocked(boolean locked);
    List<Post> findByLockedFalse();
    List<Post> findByCategoryAndLockedFalse(String category);
    List<Post> findTop10ByOrderByViewCountDesc();
    List<Post> findTop10ByOrderByCreatedAtDesc();
}