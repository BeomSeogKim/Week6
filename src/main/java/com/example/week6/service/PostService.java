package com.example.week6.service;


import com.example.week6.AWSS3.AwsS3Service;
import com.example.week6.controller.request.post.PostRequestDto;
import com.example.week6.controller.response.*;
import com.example.week6.controller.response.comment.CommentResponseDto;
import com.example.week6.controller.response.post.AllPostResponseDto;
import com.example.week6.domain.Comment;
import com.example.week6.domain.Member;
import com.example.week6.domain.Post;
import com.example.week6.jwt.TokenProvider;
import com.example.week6.repository.CommentRepository;
import com.example.week6.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  private final AwsS3Service awsS3Service;


  private final TokenProvider tokenProvider;

  @Transactional
  public ResponseDto<?> createPost(PostRequestDto requestDto, HttpServletRequest request) {
    // 작성자 검증
    if (null == request.getHeader("RefreshToken")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    Member member = validateMember(request);

    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = Post.builder()
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .imageUrl(requestDto.getImageUrl())
            .member(member)
            .build();
    postRepository.save(post);
    return ResponseDto.success("성공적으로 게시글 작성이 완료 되었습니다.");
  }


  @Transactional
  public ResponseDto<?> getPost(Long id) {
    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    /**
     * 해당 게시글의 Comment 가져오기
     */
    List<Comment> commentList = commentRepository.findAllByPost(post);
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

    /**
     * 상세 게시글 조회시 조회수 카운트 up
     */
    post.addCount();
    postRepository.save(post);


    for (Comment comment : commentList) {
      commentResponseDtoList.add(
              CommentResponseDto.builder()
                      .id(comment.getId())
                      .author(comment.getMember().getUsername())
                      .content(comment.getContent())
                      .createdAt(comment.getCreatedAt())
                      .modifiedAt(comment.getModifiedAt())
                      .build()
      );
    }

    return ResponseDto.success(
            TemporaryPostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .commentList(commentResponseDtoList)
                    .author(post.getMember().getUsername())
                    .imageUrl((post.getImageUrl()))
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllPost() {

    List<Post> postList = postRepository.findAll();
    List<AllPostResponseDto> allPostResponseDtoList = new ArrayList<>();

    for (Post post : postList) {
      allPostResponseDtoList.add(
              AllPostResponseDto.builder()
                      .postId(post.getId())
                      .title(post.getTitle())
                      .imageUrl(post.getImageUrl())
                      .createdTime(post.getCreatedAt())
                      .username(post.getMember().getUsername())
                      .watch(post.getNumberOfWatch())
                      .likes(post.getLikes().size())
                      .build()
      );

    }
    return ResponseDto.success(allPostResponseDtoList);
//    return ResponseDto.success(postRepository.findAllByOrderByModifiedAtDesc());
  }

  @Transactional
  public ResponseDto<String> updatePost(Long id, PostRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("RefreshToken")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    post.update(requestDto);

    return ResponseDto.success("성공적으로 수정되었습니다.");
  }

  @Transactional
  public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {
    if (null == request.getHeader("RefreshToken")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
    }

    postRepository.delete(post);
    return ResponseDto.success("delete success");
  }

  @Transactional(readOnly = true)
  public Post isPresentPost(Long id) {
    Optional<Post> optionalPost = postRepository.findById(id);
    return optionalPost.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }

  @Transactional
  public ResponseEntity save(MultipartFile multipartFile, String title, String content, HttpServletRequest request) {
    System.out.println("=========1=============");
    Map<String, String> map = awsS3Service.uploadFile(multipartFile);
    System.out.println("=========2=============");
    Member member = validateMember(request);
    if (member == null) {
      System.out.println("member가 유효하지 않아!!!!!!");
      return new ResponseEntity("유효하지 않은 토큰입니다.", HttpStatus.BAD_REQUEST);
    }
    System.out.println("=========3=============");
    System.out.println(member.getId());
//    Post post = new Post(member,requestDto, map.get("url"), map.get("fileName"));
    Post post = new Post(member,title, content, map.get("url"), map.get("fileName"));
    System.out.println("=========4=============");
    postRepository.save(post);
    return new ResponseEntity("게시글 저장이 정상적으로 완료되었습니다.", HttpStatus.OK);
  }
}
