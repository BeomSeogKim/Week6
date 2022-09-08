package com.example.week6.controller;

import com.example.week6.controller.response.ResponseDto;
import com.example.week6.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping("/api/post")
    public ResponseDto<?> postSave(@RequestPart("file") MultipartFile multipartFile,
                                   @RequestPart("title") String title,
                                   @RequestPart("content") String content,
                                   HttpServletRequest request) {
        return postService.save(multipartFile, title, content, request);
    }

    /**
     * 상세 게시글 조회
     */
    @RequestMapping(value = "/api/post/{id}", method = RequestMethod.GET)
    public ResponseDto<?> getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    /**
     * 전체 게시글 조회
     */
    @RequestMapping(value = "/api/list", method = RequestMethod.GET)
    public ResponseDto<?> getAllPosts() {
        return postService.getAllPost();
    }

    /**
     * 게시글 수정
     */
    @PutMapping(value = "/api/post/{id}")
    public ResponseDto<?> updatePost(@PathVariable Long id,
                                     @RequestPart("file") MultipartFile multipartFile,
                                     @RequestPart("title") String title,
                                     @RequestPart("content") String content,
                                     HttpServletRequest request) {
      return postService.updatePost(id, multipartFile, title, content, request);
    }

    /**
     * 게시글 삭제
     */
    @RequestMapping(value = "/api/post/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> deletePost(@PathVariable Long id,
                                     HttpServletRequest request) {
        return postService.deletePost(id, request);
    }

}
