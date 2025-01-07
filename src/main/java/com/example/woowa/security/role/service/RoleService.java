package com.example.woowa.security.role.service;

import com.example.woowa.security.role.repository.RoleRepository;
import com.example.woowa.security.role.entity.Role;
import com.example.woowa.security.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role findRole(UserRole userRole) {
        return roleRepository.findByUserRole(userRole.ROLE_OWNER)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 역할입니다."));
    }

}
