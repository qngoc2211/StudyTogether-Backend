package com.example.demo.controller.admin;

import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/posts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostController {

    private final PostRepository postRepository;

    public AdminPostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // ===================== GET ALL =====================
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return ResponseEntity.ok(posts);
    }

    // ===================== GET BY ID =====================
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ===================== CREATE =====================
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        LocalDateTime now = LocalDateTime.now();

        post.setId(null); // đảm bảo tạo mới
        post.setCreatedAt(now);
        post.setUpdatedAt(now);
        post.setLocked(false);

        Post savedPost = postRepository.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    // ===================== UPDATE =====================
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @RequestBody Post postDetails) {

        return postRepository.findById(id)
                .map(existingPost -> {

                    existingPost.setTitle(postDetails.getTitle());
                    existingPost.setContent(postDetails.getContent());
                    existingPost.setCategory(postDetails.getCategory());
                    existingPost.setUpdatedAt(LocalDateTime.now());

                    Post updatedPost = postRepository.save(existingPost);
                    return ResponseEntity.ok(updatedPost);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ===================== DELETE =====================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {

        if (!postRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        postRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ===================== TOGGLE LOCK =====================
    @PatchMapping("/{id}/lock")
    public ResponseEntity<Post> toggleLock(@PathVariable Long id) {

        return postRepository.findById(id)
                .map(post -> {
                    post.setLocked(!post.isLocked());
                    post.setUpdatedAt(LocalDateTime.now());

                    Post updatedPost = postRepository.save(post);
                    return ResponseEntity.ok(updatedPost);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}