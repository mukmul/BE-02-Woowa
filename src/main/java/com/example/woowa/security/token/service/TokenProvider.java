package com.example.woowa.security.token.service;

import com.example.woowa.security.user.repository.UserRepository;
import com.example.woowa.security.role.entity.Role;
import com.example.woowa.security.user.entity.User;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Duration;
import java.util.*;

@Service
@Slf4j
public class TokenProvider {

    private final UserRepository userRepository;
    private final Key secretKey;

    public TokenProvider(UserRepository userRepository,
                         @Value("${jwt.secret:default_base64_key}") String strSecretKey) {
        this.userRepository = userRepository;
        this.secretKey = new SecretKeySpec(Base64.getDecoder().decode(strSecretKey), SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    // JWT 토큰 생성 메서드
    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now) // 토큰이 발급된 시간
                .setExpiration(expiry) // 토큰 만료 시간
                .claim("id", user.getLoginId()) // 클레임 id: 유저 loginId
                // 서명. 비밀값과 함께 해시값을 HS256 방식으로 암호화
                .signWith(secretKey)
                .compact();
    }

    public boolean validToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey) // 비밀키로 복호화
                    .build()
                    .parseClaimsJws(token); // 토큰 파싱 및 서명 검증
            return true;
        } catch (SecurityException e) { // 복호화 과정에서 에러가 나면 유효하지 않은 토큰이다.
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    /**
     * 토큰을 기반으로 사용자 인증 정보를 가져오는 메서드
     *
     * @param token
     * @return Authentication
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        String loginId = getLoginId(token);

        Role role = userRepository.findRoleByLoginId(loginId);
        Set<SimpleGrantedAuthority> authority = new HashSet<>();
        authority.add(new SimpleGrantedAuthority(role.getName()));

        org.springframework.security.core.userdetails.User user
                = new org.springframework.security.core.userdetails.User(loginId, "", authority);

        return new UsernamePasswordAuthenticationToken(user, token, authority);
    }

    private String getLoginId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
