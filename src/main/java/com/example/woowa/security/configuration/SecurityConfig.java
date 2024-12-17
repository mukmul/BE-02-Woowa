package com.example.woowa.security.configuration;

import com.example.woowa.security.filter.CustomerAuthenticationFilter;
import com.example.woowa.security.filter.CustomerAuthorizationFilter;
import com.example.woowa.security.user.UserRole;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager getAuthenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        CustomerAuthenticationFilter customerAuthenticationFilter = new CustomerAuthenticationFilter(authenticationManager);
        customerAuthenticationFilter.setFilterProcessesUrl("/baemin/v1/login");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                //회원가입, 개인정보 동의, 로그인, 로그아웃, 이메일 인증, 아이디 중복 확인, 아이디 및 비밀번호 찾기, 파비콘
                                .requestMatchers("/baemin/v1/login/**", "/baemin/v1/owners").permitAll()
                                .requestMatchers("/baemin/v1/owners/**")
                                .hasAnyAuthority(UserRole.ROLE_OWNER.toString())
                                .anyRequest().authenticated()
                )
                .addFilter(customerAuthenticationFilter)
                .addFilterBefore(new CustomerAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
