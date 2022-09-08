package com.example.week6.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostResponseDto {
    private Long postId;
    private String title;
    private String content;
    private String imageUrl;
    private String username;
}
