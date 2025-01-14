package com.example.woowa.admin.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import jakarta.validation.constraints.NotBlank;


@Getter
@AllArgsConstructor
public class AdminCreateRequest {
  // 입력 유효성 검사 추가
  @NotBlank(message = "Login ID must not be empty")
  @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-z0-9]{5,10}$", message = "최소 5글자에서 10글자, 특수문자를 제외한 영숫자가 포함된 아이디가 아닙니다.")
  private String loginId;
  @NotBlank(message = "Password must not be empty")
  @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{10,}$", message = "최소 8글자, 영어 대소문자와 숫자가 최소 1개씩 포함된 비밀번호가 아닙니다.")
  private String loginPassword;
}