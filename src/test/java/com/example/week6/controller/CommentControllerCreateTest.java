package com.example.week6.controller;

import com.example.week6.controller.request.CommentRequestDto;
import com.example.week6.controller.response.CommentResponseDto;
import com.example.week6.controller.response.ResponseDto;
import com.example.week6.domain.Comment;
import com.example.week6.domain.Member;
import com.example.week6.domain.Post;
import com.example.week6.jwt.TokenProvider;
import com.example.week6.repository.CommentRepository;
import com.example.week6.service.CommentService;
import com.example.week6.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.conversions.LongConversion;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CommentControllerCreateTest {

    @Mock
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private PostService postService;

    @Mock
    private ResponseDto responseDto;


    private CommentRequestDto requestDto= new CommentRequestDto(0L, "test");

    @Nested
    @DisplayName("fail test")
    class CreateComment{
        @Test
        @DisplayName("Header Null")
        void A() {
            //given
            ResponseDto<?> expect_data= ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
            MockHttpServletRequest mockHttpServletRequest= new MockHttpServletRequest();
            mockHttpServletRequest.addHeader("RefreshToken", "RefreshToken");

            //when
//            null == request.getHeader("RefreshToken")
            var result= commentService.createComment(requestDto, mockHttpServletRequest);

            //then
            assertEquals(expect_data.getClass(), result);
        }

        @Test
        @DisplayName("member is null")
        void B() {
            //given
            Member member= new Member();
            ResponseDto expect_data= ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
            MockHttpServletRequest mockHttpServletRequest= new MockHttpServletRequest();
            mockHttpServletRequest.addHeader(null, null);

            //when
            var result= commentService.createComment(requestDto, mockHttpServletRequest);

            //then
            assertEquals(expect_data, result);
        }

        @Test
        @DisplayName("post is null")
        void C() {
            //given
            Post post= new Post();
            ResponseDto expect_data= ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
            MockHttpServletRequest mockHttpServletRequest= new MockHttpServletRequest();

            //when
//            null == post
            var result= commentService.createComment(requestDto, mockHttpServletRequest);

            //then
            assertEquals(expect_data, result);
        }

    }
}
