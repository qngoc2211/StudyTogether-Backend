package com.example.demo.controller.publics;

import com.example.demo.dto.PostDTO;
import com.example.demo.dto.request.CreatePostRequest;
import com.example.demo.dto.request.UpdatePostRequest;
import com.example.demo.service.PostService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public PostDTO create(@RequestBody CreatePostRequest request) {
        return postService.create(request);
    }

    @PutMapping("/{id}")
    public PostDTO update(@PathVariable Long id,
                          @RequestBody UpdatePostRequest request) {
        return postService.update(id, request);
    }
}