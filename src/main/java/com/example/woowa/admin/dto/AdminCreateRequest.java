package com.example.woowa.admin.dto;
// 관리자를 생성할 때 요청 본문(Request Body)으로 받는 데이터 구조를 정의
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminCreateRequest {
  @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-z0-9]{5,10}$", message = "최소 5글자에서 10글자, 특수문자를 제외한 영숫자가 포함된 아이디가 아닙니다.")
  private String loginId;
  @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{10,}$", message = "최소 8글자, 영어 대소문자와 숫자가 최소 1개씩 포함된 비밀번호가 아닙니다.")
  private String loginPassword;
}

//loginId: 로그인 아이디 — 5~10자의 영숫자로 제한됩니다.
//loginPassword: 비밀번호 — 8자 이상의 대소문자 및 숫자를 포함해야 합니다.
//유효성 검사: @Pattern 어노테이션으로 입력 형식을 검증합니다.