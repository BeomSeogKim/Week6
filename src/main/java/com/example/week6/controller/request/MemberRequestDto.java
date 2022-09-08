package com.example.week6.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {

  @NotBlank
  @Size(min = 4, max = 12,message = "아이디 양식을 다시 확인해주세요")
  @Pattern(regexp = "^[A-Za-z0-9+]{4,10}$", message = "아이디 양식을 다시 확인해주세요")
  private String username;

  @NotBlank
  @Size(min = 8, max = 20 ,message = "비밀번호 양식을 다시 확인해주세요")
  @Pattern(regexp = "^[A-Za-z0-9+]{8,20}$", message = "비밀번호 양식을 다시 확인해주세요")
  private String password;

  @NotBlank
  private String passwordConfirm;
}
