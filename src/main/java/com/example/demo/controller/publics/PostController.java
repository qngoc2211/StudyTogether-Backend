package com.example.demo.controller.publics;

import com.example.demo.entity.Post;
import com.example.demo.entity.Comment;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.dto.PostDTO;
import com.example.demo.dto.request.CreatePostRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CommentRepository commentRepository;

    @GetMapping
    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepository.findByLockedFalse();
        return posts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(post -> {
                    post.setViewCount(post.getViewCount() + 1);
                    postRepository.save(post);
                    return ResponseEntity.ok(convertToDTO(post));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public List<PostDTO> getPostsByCategory(@PathVariable String category) {
        List<Post> posts = postRepository.findByCategoryAndLockedFalse(category);
        return posts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());
        post.setAuthor(username);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setLocked(false);
        post.setViewCount(0);
        
        Post savedPost = postRepository.save(post);
        return ResponseEntity.ok(savedPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody Post postDetails) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        return postRepository.findById(id)
                .map(post -> {
                    if (!post.getAuthor().equals(username) && 
                        !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        return ResponseEntity.status(403).body("Không có quyền sửa bài viết này");
                    }
                    
                    post.setTitle(postDetails.getTitle());
                    post.setContent(postDetails.getContent());
                    post.setCategory(postDetails.getCategory());
                    post.setUpdatedAt(LocalDateTime.now());
                    
                    Post updatedPost = postRepository.save(post);
                    return ResponseEntity.ok(updatedPost);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {  // Đổi thành ResponseEntity<?>
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        return postRepository.findById(id)
                .map(post -> {
                    if (!post.getAuthor().equals(username) && 
                        !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        return ResponseEntity.status(403).body("Không có quyền xóa bài viết này");
                    }
                    
                    postRepository.delete(post);
                    return ResponseEntity.ok().body("Xóa bài viết thành công");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/featured")
    public List<PostDTO> getFeaturedPosts() {
        List<Post> posts = postRepository.findTop10ByOrderByViewCountDesc();
        return posts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/latest")
    public List<PostDTO> getLatestPosts() {
        List<Post> posts = postRepository.findTop10ByOrderByCreatedAtDesc();
        return posts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{postId}/comments")
    public List<Comment> getPostComments(@PathVariable Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody String content) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        return postRepository.findById(postId)
                .map(post -> {
                    Comment comment = new Comment();
                    comment.setContent(content);
                    comment.setAuthor(username);
                    comment.setPost(post);
                    comment.setCreatedAt(LocalDateTime.now());
                    
                    Comment savedComment = commentRepository.save(comment);
                    return ResponseEntity.ok(savedComment);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        return commentRepository.findById(commentId)
                .map(comment -> {
                    if (!comment.getAuthor().equals(username) && 
                        !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        return ResponseEntity.status(403).body("Không có quyền xóa bình luận này");
                    }
                    
                    commentRepository.delete(comment);
                    return ResponseEntity.ok().body("Xóa bình luận thành công");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private PostDTO convertToDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthor(post.getAuthor());
        dto.setCategory(post.getCategory());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setViewCount(post.getViewCount());
        dto.setLocked(post.isLocked());
        dto.setCommentCount(commentRepository.countByPostId(post.getId()).intValue());
        return dto;
    }
}