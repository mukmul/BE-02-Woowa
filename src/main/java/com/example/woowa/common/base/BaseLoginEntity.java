package com.example.woowa.common.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class BaseLoginEntity extends BaseTimeEntity {

    @Column(unique = true,
        nullable = false,
        updatable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    public BaseLoginEntity(String loginId, String password, String name, String phoneNumber) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public BaseLoginEntity(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

//    public void changePassword(String password) {
//        this.password = password;
//    }

    // 비밀번호 변경 책임을 DTP->엔티티로 이전
    // BaseLoginEntity는 공통 로그인 엔티티이기 때문에 Admin, User, Customer 등 다양한 하위 엔티티에서도 재사용할 수 있음
    public void changePassword(String newPassword) {
        // 비밀번호 길이 검증 추가
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
        }
        this.password = newPassword;
    }

}
