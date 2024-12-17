package com.example.woowa.admin.entity;

import com.example.woowa.common.base.BaseLoginEntity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "admin")
@Entity
public class Admin extends BaseLoginEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Admin(String loginId, String loginPassword) {
        super(loginId, loginPassword, "", "");
    }

}
