package com.example.woowa.security.configuration;

import com.example.woowa.security.filter.AuthenticationFilter;
import com.example.woowa.security.token.service.TokenProvider;
import com.example.woowa.security.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager getAuthenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .addFilterBefore(
                        new AuthenticationFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                )

                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                //회원가입, 개인정보 동의, 로그인, 로그아웃, 이메일 인증, 아이디 중복 확인, 아이디 및 비밀번호 찾기, 파비콘
                                .requestMatchers("/baemin/v1/login/**", "/baemin/v1/owners",
                                        "/swagger-ui/**", "/swagger-resources/**",
                                        "/v3/api-docs/**", "/webjars/**",
                                        "/api/v1/customers", "/api/v1/rider",
                                        "/api/v1/owner", "/api/v1/admins", "/api/v1/areaCode").permitAll()
                                .requestMatchers("/baemin/v1/owners/**")
                                .hasAnyAuthority(UserRole.ROLE_OWNER.getRoleName())
                                .anyRequest().authenticated()
                );

        return httpSecurity.build();
    }

}
