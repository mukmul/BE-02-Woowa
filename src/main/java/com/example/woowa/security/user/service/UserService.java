package com.example.woowa.security.user.service;

import com.example.woowa.common.base.BaseLoginEntity;
import com.example.woowa.security.role.service.RoleService;
import com.example.woowa.security.user.repository.UserRepository;
import com.example.woowa.security.role.entity.Role;
import com.example.woowa.security.user.entity.User;
import java.util.HashSet;
import java.util.Set;

import com.example.woowa.security.user.entity.UserRole;
import com.example.woowa.security.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final UserMapper userMapper;

    public User findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
            .orElseThrow(() -> new UsernameNotFoundException("해당 아이디를 가진 유저를 찾을 수 없습니다."));
    }

    @Transactional
    public User createUser(BaseLoginEntity baseLoginEntity, UserRole userRole) {
        Role role = roleService.findRole(userRole);
        User user = userMapper.toUser(baseLoginEntity, role);
        return userRepository.save(user);
    }

    @Transactional
    public void syncUser(BaseLoginEntity baseLoginEntity) {
        User user = userRepository.findByLoginId(baseLoginEntity.getLoginId())
                .orElseThrow(() -> new RuntimeException("User not found with loginId: " + baseLoginEntity.getLoginId()));

        user.sync(baseLoginEntity.getPassword(), baseLoginEntity.getName(), baseLoginEntity.getPhoneNumber());
    }

    @Transactional
    public void deleteUser(String loginId) {
        userRepository.deleteByLoginId(loginId);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByLoginId(username);
        user.login();
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
//        user.getRoles().forEach(role ->
//                authorities.add(new SimpleGrantedAuthority(role.getName())));
        authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));

        return new org.springframework.security.core.userdetails.User(user.getLoginId(), user.getPassword(), authorities);
    }

}
