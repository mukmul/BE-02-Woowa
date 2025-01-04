package com.example.woowa.security.login.service;

import com.example.woowa.security.token.dto.request.LoginRequest;
import com.example.woowa.security.token.dto.response.LoginResponse;
import com.example.woowa.security.token.service.TokenProvider;
import com.example.woowa.security.user.entity.User;
import com.example.woowa.security.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;


    public LoginResponse login(final LoginRequest loginRequest) {

        User findUser = userService.findByLoginId(loginRequest.getLoginId());

        // password 일치 여부 체크
        if (!passwordEncoder.matches(loginRequest.getPassword(), findUser.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String token = tokenProvider.generateToken(findUser, Duration.ofHours(24)); // 하루 치 토큰 생성

        return LoginResponse.builder()
                .accessToken(token)
                .build();
    }


}
