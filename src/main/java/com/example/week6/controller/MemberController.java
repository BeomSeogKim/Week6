package com.example.week6.controller;


import com.example.week6.controller.request.DuplicateRequestDto;
import com.example.week6.controller.request.LoginRequestDto;
import com.example.week6.controller.request.MemberRequestDto;
import com.example.week6.controller.response.ResponseDto;
import com.example.week6.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class MemberController {

  private final MemberService memberService;

  /**
   * 회원 가입
   * MemberRequestDto : 가입 신청서
   */
  @RequestMapping(value = "/api/member/signup", method = RequestMethod.POST)
  public ResponseDto<?> signup(@RequestBody @Valid MemberRequestDto requestDto) {
    return memberService.createMember(requestDto);
  }

  /**
   * 닉네임 중복 검사
   * @param : 닉네임만
   */
  @RequestMapping(value = "/api/member/signup/duplicate", method = RequestMethod.POST)
  public ResponseDto<?> checkDuplicate(@RequestBody @Valid DuplicateRequestDto requestDto) {
    return memberService.checkDuplicate(requestDto);
  }

  /**
   * 로그인
   * @param : 닉네임 및 비밀번호
   */
  @RequestMapping(value = "/api/member/login", method = RequestMethod.POST)
  public ResponseDto<?> login(@RequestBody @Valid LoginRequestDto requestDto,
      HttpServletResponse response
  ) {
    return memberService.login(requestDto, response);
  }

  /**
   * 로그아웃
   */
  @RequestMapping(value = "/api/member/logout", method = RequestMethod.POST)
  public ResponseDto<?> logout(HttpServletRequest request) {
    return memberService.logout(request);
  }
}
