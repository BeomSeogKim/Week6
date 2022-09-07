package com.example.week6.controller;


import com.example.week6.service.FileUploadService;
import com.example.week6.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class FileUploadController {

    private final FileUploadService fileUploadService;
    private final PostService postService;

    @PostMapping("/api/upload")
    public String uploadImage(@RequestPart MultipartFile file) {
        return fileUploadService.uploadImage(file);
    }

    @PostMapping("/api/upload/multi")
    public ResponseEntity postSave(@RequestPart("file") MultipartFile multipartFile,
                                   @RequestPart("title") String title,
                                   @RequestPart("content") String content,
                                   HttpServletRequest request) {

        if (multipartFile == null) {
            System.out.println("Multipart가 Null 입니다.");
        }
        if (title == null) {
            System.out.println("title이 Null 입니다.");
        }
        if (content == null) {
            System.out.println("content가 Null 입니다.");
        }
        if (request == null) {
            System.out.println("request가 Null 입니다. ");
        }

//        return postService.save(multipartFile, requestDto, request);
        return postService.save(multipartFile, title, content, request);
    }

}