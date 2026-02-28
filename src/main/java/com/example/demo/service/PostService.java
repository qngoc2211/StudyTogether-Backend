package com.example.demo.service;

import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    public List<Post> getAllPosts() {
        return postRepository.findByOrderByCreatedAtDesc();
    }
    
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }
    
    public Post createPost(Post post) {
        return postRepository.save(post);
    }
    
    public Post updatePost(Long id, Post postDetails) {
        Post post = getPostById(id);
        if (post != null) {
            post.setTitle(postDetails.getTitle());
            post.setContent(postDetails.getContent());
            return postRepository.save(post);
        }
        return null;
    }
    
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
    
    public List<Post> getPostsByCategory(String category) {
        return postRepository.findByCategory(category);
    }
}