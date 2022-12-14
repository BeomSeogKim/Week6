package com.example.week6.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TemporaryPostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String author;
    private String imageUrl;
    private List<AllCommentResponseDto> commentList;
}
