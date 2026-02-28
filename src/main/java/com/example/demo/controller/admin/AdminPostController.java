package com.example.demo.controller.admin;

import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/posts")
@CrossOrigin(origins = "*")
public class AdminPostController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Post createPost(@RequestBody Post post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post postDetails) {
        return postRepository.findById(id)
                .map(post -> {
                    post.setTitle(postDetails.getTitle());
                    post.setContent(postDetails.getContent());
                    post.setCategory(postDetails.getCategory());
                    post.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(postRepository.save(post));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(post -> {
                    postRepository.delete(post);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Post> toggleLock(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(post -> {
                    post.setLocked(!post.isLocked());
                    post.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(postRepository.save(post));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}