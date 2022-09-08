package com.example.week6.controller;

import com.example.week6.controller.response.ResponseDto;
import com.example.week6.service.CommentLikeService;
import com.example.week6.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class LikeController {

    private final PostLikeService postLikeService;
    private final CommentLikeService commentLikeService;

    /**
     * 게시글 좋아요
     */
    @PostMapping("/api/like/post/{id}")
    public ResponseDto<?> likePost(@PathVariable Long id, HttpServletRequest request) {
        return postLikeService.likePost(id, request);
    }

    /**
     * 게시글 좋아요 취소
     */
    @PostMapping("/api/dislike/post/{id}")
    public ResponseDto<?> dislikePost(@PathVariable Long id, HttpServletRequest request) {
        return postLikeService.dislikePost(id, request);
    }

    /**
     * 댓글 좋아요
     */
    @PostMapping("/api/like/comment/{id}")
    public ResponseDto<?> likeComment(@PathVariable Long id, HttpServletRequest request) {
        return commentLikeService.likeComment(id, request);
    }

    /**
     * 댓글 좋아요 취소
     */
    @PostMapping("/api/dislike/comment/{id}")
    public ResponseDto<?> dislikeComment(@PathVariable Long id, HttpServletRequest request) {
        return commentLikeService.dislikeComment(id, request);
    }

}
