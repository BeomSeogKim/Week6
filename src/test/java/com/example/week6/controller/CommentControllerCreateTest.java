package com.example.week6.controller;

import com.example.week6.controller.request.CommentRequestDto;
import com.example.week6.controller.response.ResponseDto;
import com.example.week6.domain.Member;
import com.example.week6.domain.Post;
import com.example.week6.jwt.TokenProvider;
import com.example.week6.repository.CommentRepository;
import com.example.week6.service.CommentService;
import com.example.week6.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

class CommentControllerCreateTest {

    private CommentService commentService;
    private CommentRepository commentRepository;
    private TokenProvider tokenProvider;
    private PostService postService;

    @Nested
    @DisplayName("생성 테스트")
    class CreateComment{
        CommentRequestDto requestDto;
        ResponseDto responseDto;

        @Test
        @DisplayName("Refresh-Token")
        void A() {
            //given
            ResponseDto expect_data= ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
            MockHttpServletRequest mockHttpServletRequest= new MockHttpServletRequest();

            //when
//            null == request.getHeader("Refresh-Token")
            var result= commentService.createComment(requestDto, mockHttpServletRequest);

            //then
            assert(expect_data, result);
        }

        @Test
        @DisplayName("Authorization")
        void B() {
            //given
            ResponseDto expect_data= ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
            MockHttpServletRequest mockHttpServletRequest= new MockHttpServletRequest();

            //when
            var result= commentService.createComment(requestDto, mockHttpServletRequest);

            //then
            assert(expect_data, result);
        }

        @Test
        @DisplayName("member is null")
        void C() {
            //given
            Member member= new Member();
            ResponseDto expect_data= ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
            MockHttpServletRequest mockHttpServletRequest= new MockHttpServletRequest();

            //when
            var result= commentService.createComment(requestDto, mockHttpServletRequest);

            //then
            assert(expect_data, result);
        }

        @Test
        @DisplayName("post is null")
        void D() {
            //given
            Post post= new Post();
            ResponseDto expect_data= ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
            MockHttpServletRequest mockHttpServletRequest= new MockHttpServletRequest();

            //when
//            null == post
            var result= commentService.createComment(requestDto, mockHttpServletRequest);

            //then
            assert(expect_data, result);
        }

    }

    @Test
    @DisplayName("Success- Comment")
    void A() {
        //given
        CommentRequestDto requestDto;
        ResponseDto responseDto;
        MockHttpServletRequest mockHttpServletRequest= new MockHttpServletRequest();

        //when
        var result= commentService.createComment(requestDto, mockHttpServletRequest);


        //then
    }
}