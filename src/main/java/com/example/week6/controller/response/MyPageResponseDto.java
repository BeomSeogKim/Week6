package com.example.week6.controller.response.mypage;


import com.example.week6.controller.response.comment.CommentResponseDto;
import com.example.week6.controller.response.post.AllPostResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageResponseDto {

    private List<AllPostResponseDto> postList;
    private List<CommentResponseDto> commentList;
    private List<AllPostResponseDto> postLikeList;
    private List<CommentResponseDto> commentLikeList;
}
