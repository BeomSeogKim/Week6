package com.example.week6.service;


import com.example.week6.controller.Qualify;
import com.example.week6.controller.request.DuplicateRequestDto;
import com.example.week6.controller.request.LoginRequestDto;
import com.example.week6.controller.request.MemberRequestDto;
import com.example.week6.controller.request.TokenDto;
import com.example.week6.controller.response.ResponseDto;
import com.example.week6.controller.response.DuplicateResponseDto;
import com.example.week6.controller.response.MemberResponseDto;
import com.example.week6.domain.Member;
import com.example.week6.jwt.TokenProvider;
import com.example.week6.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;
  private final Qualify qualify;

  /**
   * 회원 생성
   */
  @Transactional
  public ResponseDto<?> createMember(MemberRequestDto requestDto) {
    // 회원 닉네임 검증
    if (null != qualify.isPresentMember(requestDto.getUsername())) {
      return ResponseDto.fail("DUPLICATED_username",
          "중복된 닉네임 입니다.");
    }

    // 비밀번호 및 비밀번호 확인 유효성 검사
    if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
      return ResponseDto.fail("PASSWORDS_NOT_MATCHED",
          "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
    }

    // 회원 객체 생성
    Member member = Member.builder()
                    .username(requestDto.getUsername())
                    .password(passwordEncoder.encode(requestDto.getPassword()))
                    .build();
    // 회원 저장
    memberRepository.save(member);

    return ResponseDto.success(
        MemberResponseDto.builder()
            .id(member.getId())
            .username(member.getUsername())
            .build()
    );
  }

  /**
   * 로그인
   */
  @Transactional
  public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
    // 회원 id 검사
    Member member = qualify.isPresentMember(requestDto.getUsername());
    if (null == member) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "사용자를 찾을 수 없습니다.");
    }
    // 회원 password 일치 여부 검사
    if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
      return ResponseDto.fail("INVALID_MEMBER", "사용자를 찾을 수 없습니다.");
    }

    // 토큰 발급
    TokenDto tokenDto = tokenProvider.generateTokenDto(member);
    qualify.tokenToHeaders(tokenDto, response);
//    tokenToHeaders(tokenDto, response);

    return ResponseDto.success(
              MemberResponseDto.builder()
              .id(member.getId())
              .username(member.getUsername())
              .build()
    );
  }

  /**
   * 로그아웃
   */
  public ResponseDto<?> logout(HttpServletRequest request) {
    // 토큰 유효성 검사
    if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }
    // 회원 객체 조회
    Member member = tokenProvider.getMemberFromAuthentication();
    if (null == member) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "사용자를 찾을 수 없습니다.");
    }
    return tokenProvider.deleteRefreshToken(member);
  }


  /**
   * 회원 닉네임 중복 검사
   */
  public ResponseDto<?> checkDuplicate(DuplicateRequestDto requestDto) {

    // 회원 이름으로 조회 시 없을 때
    if (memberRepository.findByusername(requestDto.getUsername()).isEmpty()) {
      return ResponseDto.success(
              DuplicateResponseDto.builder()
                      .username(requestDto.getUsername())
                      .build()
      );
    }
    return ResponseDto.fail("ALREADY_EXISTS", "이미 존재하는 회원입니다.");
  }
}
