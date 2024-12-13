package com.example.woowa.admin.entity;
// 관리자 엔티티로, 데이터베이스의 admin 테이블과 연결됨

import com.example.woowa.common.base.BaseLoginEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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

// 엔티티 변경 메서드 추가로 가독성 향상 / 비밀번호 수정 메서드 추가
//    public void changePassword(String newPassword) {
//        this.setPassword(newPassword);
//    }
}

