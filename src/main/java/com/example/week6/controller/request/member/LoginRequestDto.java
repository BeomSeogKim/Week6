package com.example.week6.controller.request.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

  @NotBlank
  private String username;

  @NotBlank
  private String password;

//  @NotBlank
//  private String passwordConfirm;

}
