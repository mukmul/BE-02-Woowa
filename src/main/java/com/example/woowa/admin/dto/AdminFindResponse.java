package com.example.woowa.admin.dto;
// 관리자의 정보를 응답(Response Body)으로 반환할 때의 데이터 구조를 정의
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminFindResponse {
  private String loginId; // 특정 엔드포인트의 응답 데이터로 사용됨
}
