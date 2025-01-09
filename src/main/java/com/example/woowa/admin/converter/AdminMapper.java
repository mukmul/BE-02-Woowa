package com.example.woowa.admin.converter;
// MapStruct 인터페이스로, AdminConverter와 비슷한 역할을 하지만, 더 간결하게 DTO와 엔티티 간의 변환을 자동화
import com.example.woowa.admin.dto.AdminCreateRequest;
import com.example.woowa.admin.dto.AdminFindResponse;
import com.example.woowa.admin.entity.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL, componentModel = "spring")
public interface AdminMapper {
  // AdminCreateRequest를 Admin으로 변환
  Admin toAdmin(AdminCreateRequest adminCreateRequest);
  // Admin을 AdminFindResponse로 변환
  AdminFindResponse toAdminDto(Admin admin);
}

// MapStruct 기능 활용: 자동으로 DTO와 엔티티 간의 매핑을 수행