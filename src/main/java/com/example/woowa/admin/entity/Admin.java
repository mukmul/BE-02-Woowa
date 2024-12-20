package com.example.woowa.admin.entity;
// 관리자 엔티티로, 데이터베이스의 admin 테이블과 연결됨

import com.example.woowa.common.base.BaseLoginEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "admin")
@Entity
// BaseLoginEntity를 상속받아 로그인 관련 정보를 관리
public class Admin extends BaseLoginEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 주요 생성자: 새 관리자를 생성할 때 사용
    public Admin(String loginId, String loginPassword) {
        super(loginId, loginPassword, "", "");
    }

}

