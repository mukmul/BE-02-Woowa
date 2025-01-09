package com.example.woowa.admin.converter;
// DTO와 엔티티 간의 변환 로직을 처리

import com.example.woowa.admin.dto.AdminCreateRequest;
import com.example.woowa.admin.dto.AdminFindResponse;
import com.example.woowa.admin.entity.Admin;
import java.util.regex.Pattern;

public class AdminConverter {
  // AdminCreateRequest DTO를 Admin 엔티티로 변환
  public static Admin toAdmin(AdminCreateRequest adminCreateRequest) {
    return new Admin(adminCreateRequest.getLoginId(), adminCreateRequest.getLoginPassword());
  }
  // Admin 엔티티를 AdminFindResponse DTO로 변환
  public static AdminFindResponse toAdminDto(Admin admin) {
    return new AdminFindResponse(admin.getLoginId());
  }
}

// 주요 역할: DTO → Entity, Entity → DTO 변환을 담당