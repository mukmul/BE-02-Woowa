package com.example.woowa.admin.repository;
// JPA 레포지토리로, 관리자의 데이터를 데이터베이스와 직접적으로 연결하는 역할

import com.example.woowa.admin.entity.Admin;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
// JPA 기능 활용: 기본 CRUD 기능은 JpaRepository로부터 상속받기 때문에 별도의 구현 없이 CRUD 메서드를 사용할 수 있음
public interface AdminRepository extends JpaRepository<Admin, Long> {
  // 로그인 아이디로 관리자를 조회
  Optional<Admin> findByLoginId(String loginId);
  Boolean existsAdminByLoginId(String loginId);
}
