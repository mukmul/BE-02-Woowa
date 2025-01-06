package com.example.woowa.security.user.repository;

import com.example.woowa.security.role.entity.Role;
import com.example.woowa.security.user.entity.User;

import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.loginId = :loginId")
    Optional<User> findByLoginId(@Param("loginId") String loginId);

    void deleteByLoginId(String loginId);

    @Query("SELECT u.role FROM User u WHERE u.loginId = :loginId")
    Role findRoleByLoginId(String loginId);

}
