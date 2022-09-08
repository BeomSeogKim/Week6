package com.example.week6.controller;

import com.example.week6.controller.request.TokenDto;
import com.example.week6.domain.Comment;
import com.example.week6.domain.Member;
import com.example.week6.domain.Post;
import com.example.week6.jwt.TokenProvider;
import com.example.week6.repository.CommentRepository;
import com.example.week6.repository.MemberRepository;
import com.example.week6.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class Qualify {

    // == Dependency Injection ==//
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final TokenProvider tokenProvider;
    private final CommentRepository commentRepository;

    /**
     * 게시글 존재 여부 검사
     */
    @Transactional(readOnly = true)
    public Member isPresentMember(String username) {
        Optional<Member> optionalMember = memberRepository.findByusername(username);
        return optionalMember.orElse(null);
    }

    /**
     * 회원 유효 여부 검사
     */
    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

    /**
     * 토큰 Header에 담아서 넘겨주기
     */
    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("RefreshToken", tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }

    /**
     * 게시글 존재여부 검사
     */
    @Transactional(readOnly = true)
    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }

    /**
     * 댓글 존재여부 검사
     */
    @Transactional(readOnly = true)
    public Comment isPresentComment(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElse(null);
    }
}
