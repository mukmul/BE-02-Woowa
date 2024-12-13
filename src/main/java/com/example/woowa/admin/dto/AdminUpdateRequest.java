package com.example.woowa.admin.dto;
// 관리자의 정보를 수정할 때 요청 본문(Request Body)으로 받는 데이터 구조를 정의
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdminUpdateRequest {
  @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$", message = "최소 8글자, 영어 대소문자와 숫자가 최소 1개씩 포함된 비밀번호가 아닙니다.")
  private String loginPassword;
}

//loginPassword: 새 비밀번호 — 8자 이상의 대소문자 및 숫자를 포함해야 합니다.
//유효성 검사: @Pattern 어노테이션으로 입력 형식을 검증합니다.
