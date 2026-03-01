package com.example.demo.controller.admin;

import com.example.demo.dto.PostDTO;
import com.example.demo.dto.request.UpdatePostRequest;
import com.example.demo.service.PostService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/posts")
public class AdminPostController {

    private final PostService postService;

    public AdminPostController(PostService postService) {
        this.postService = postService;
    }

    @PutMapping("/{id}")
    public PostDTO updatePost(@PathVariable Long id,
                              @RequestBody UpdatePostRequest request) {
        return postService.updatePost(id, request);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }
}