package com.example.demo.service;

import com.example.demo.dto.PostDTO;
import com.example.demo.dto.request.CreatePostRequest;
import com.example.demo.dto.request.UpdatePostRequest;
import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostDTO create(CreatePostRequest request) {

        Post post = new Post(
                request.title(),
                request.content(),
                request.author(),
                request.category()
        );

        Post saved = postRepository.save(post);
        return mapToDTO(saved);
    }

    public PostDTO update(Long id, UpdatePostRequest request) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(request.title());
        post.setContent(request.content());
        post.setCategory(request.category());
        post.setLocked(request.locked());

        Post updated = postRepository.save(post);
        return mapToDTO(updated);
    }

    public PostDTO updatePost(Long id, UpdatePostRequest request) {
        return update(id, request);
    }

    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("Post not found");
        }
        postRepository.deleteById(id);
    }

    private PostDTO mapToDTO(Post post) {
        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getCategory(),
                post.getLocked(),
                post.getViewCount(),
                post.getCreatedAt()
        );
    }
}