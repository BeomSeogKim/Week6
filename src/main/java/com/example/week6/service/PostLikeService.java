package com.example.week6.service;

import com.example.week6.controller.Qualify;
import com.example.week6.controller.response.ResponseDto;
import com.example.week6.domain.Member;
import com.example.week6.domain.Post;
import com.example.week6.domain.PostLike;
import com.example.week6.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final Qualify qualify;

    /**
     * 게시글 좋아요
     */
    @Transactional
    public ResponseDto<?> likePost(Long id, HttpServletRequest request) {

        Post post = qualify.isPresentPost(id);
        Member member = qualify.validateMember(request);

        // 게시글 검증
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id입니다.");
        }

        // 좋아요 중복 검증
        if (isNotAlreadyLike(member, post)) {
            postLikeRepository.save(new PostLike(post, member));
            return ResponseDto.success("좋아요가 성공적으로 반영되었습니다.");
        }
        return ResponseDto.fail("ALREADY_LIKE","이미 좋아요를 누르셨습니다.");
    }

    /**
     * 게시글 좋아요 취소
     */
    public ResponseDto<?> dislikePost(Long id, HttpServletRequest request) {
        Post post = qualify.isPresentPost(id);
        Member member = qualify.validateMember(request);

        // 게시글 검증
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id입니다.");
        }

        if (!isNotAlreadyLike(member, post)) {
            postLikeRepository.removeByMemberId(member.getId());
            return ResponseDto.success("좋아요 취소.");
        }
        return ResponseDto.fail("DIDNT_LIKE", "좋아요를 누르시지 않으셨습니다. ");
    }


    // 게시글 좋아요를 눌렀는지 판단하는 method
    private boolean isNotAlreadyLike(Member member, Post post) {
        return postLikeRepository.findByMemberAndPost(member, post).isEmpty();
    }

}
