package com.example.woowa.security.user.entity;

import com.example.woowa.common.base.BaseLoginEntity;
import java.time.LocalDateTime;

import com.example.woowa.security.role.entity.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class User extends BaseLoginEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private LocalDateTime lastLoginAt;

    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;

    public User(String loginId, String password, String name, String phoneNumber, Role role) {
        super(loginId, password, name, phoneNumber);
        this.role = role;
    }

    public void sync(String password, String name, String phoneNumber) {
        if (!this.getPassword().equals(password)) {
            changePassword(password);
        }
        if (!this.getName().equals(name)) {
            changeName(name);
        }
        if (!this.getPhoneNumber().equals(phoneNumber)) {
            changePhoneNumber(phoneNumber);
        }
    }

    public void login() {
        this.lastLoginAt = LocalDateTime.now();
    }

}
