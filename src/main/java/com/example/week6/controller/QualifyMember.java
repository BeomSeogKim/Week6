package com.example.week6.controller;

import com.example.week6.controller.request.member.DuplicateRequestDto;
import com.example.week6.controller.request.token.TokenDto;
import com.example.week6.controller.response.ResponseDto;
import com.example.week6.controller.response.member.DuplicateResponseDto;
import com.example.week6.domain.Member;
import com.example.week6.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QualifyMember {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Member isPresentMember(String username) {
        Optional<Member> optionalMember = memberRepository.findByusername(username);
        return optionalMember.orElse(null);
    }

    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("RefreshToken", tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }

}
