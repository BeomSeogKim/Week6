package com.example.week6.service;


import com.example.week6.AWSS3.AwsS3Service;
import com.example.week6.controller.Qualify;
import com.example.week6.controller.response.*;
import com.example.week6.domain.Comment;
import com.example.week6.domain.Member;
import com.example.week6.domain.Post;
import com.example.week6.jwt.TokenProvider;
import com.example.week6.repository.CommentRepository;
import com.example.week6.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final AwsS3Service awsS3Service;
  private final TokenProvider tokenProvider;
  private final Qualify qualify;

  /**
   * 상세 게시글 조회
   */
  @Transactional
  public ResponseDto<?> getPost(Long id) {
    Post post = qualify.isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    /**
     * 해당 게시글의 Comment 가져오기
     */
    List<Comment> commentList = commentRepository.findAllByPost(post);
    List<AllCommentResponseDto> commentResponseDtoList = new ArrayList<>();

    /**
     * 상세 게시글 조회시 조회수 카운트 up
     */
    post.addCount();
    postRepository.save(post);


    for (Comment comment : commentList) {
      commentResponseDtoList.add(
              AllCommentResponseDto.builder()
                      .id(comment.getId())
                      .author(comment.getMember().getUsername())
                      .content(comment.getContent())
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
                      .username(post.getMember().getUsername())
                      .watch(post.getNumberOfWatch())
                      .likes(post.getLikes().size())
                      .build()
      );

    }
    return ResponseDto.success(allPostResponseDtoList);
  }

  /**
   * 게시글 업데이트
   */
    @Transactional
  public ResponseDto<?> updatePost(Long id, MultipartFile multipartFile, String title, String content, HttpServletRequest request) {
    // 회원정보 및 토큰 검증
    if (null == request.getHeader("RefreshToken")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    Member member = qualify.validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }
    // 게시글 존재 여부 검증
    Post post = qualify.isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }
    // 작성자 여부 검증
    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

      Map<String, String> map = awsS3Service.uploadFile(multipartFile);
      post.update(title, content, map.get("url"), map.get("fileName"));

      return ResponseDto.success(
              UpdatePostResponseDto.builder()
                      .postId(post.getId())
                      .title(post.getTitle())
                      .content(post.getContent())
                      .imageUrl(post.getImageUrl())
                      .username(post.getMember().getUsername())
                      .build()
      );

  }

  /**
   * 게시글 삭제
   */
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

    Member member = qualify.validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = qualify.isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
    }

    postRepository.delete(post);
    return ResponseDto.success("delete success");
  }

  /**
   * 게시글 저장
   */
  @Transactional
  public ResponseDto<?> save(MultipartFile multipartFile, String title, String content, HttpServletRequest request) {
    Map<String, String> map = awsS3Service.uploadFile(multipartFile);
    Member member = qualify.validateMember(request);
    if (member == null) {
      return ResponseDto.fail("NOT_VALID_TOKEN", "유효한 토큰이 아닙니다.");
    }
    System.out.println(member.getId());
    Post post = new Post(member,title, content, map.get("url"), map.get("fileName"));
    postRepository.save(post);
    return ResponseDto.success("성공적으로 게시글 작성이 완료되었습니다.");
  }
}
