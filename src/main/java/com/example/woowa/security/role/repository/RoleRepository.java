package com.example.woowa.security.role.repository;

import com.example.woowa.security.role.entity.Role;
import com.example.woowa.security.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    default Optional<Role> findByUserRole(UserRole userRole) {
        return findByName(userRole.getRoleName());
    }

}
