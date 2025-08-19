package com.github.seungjae97.alyak.alyakapiserver.global.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1시간

    /**
     * @param userId : 유저 식별하는 userId
     * @param role : 약사, 일반인, 가족 역할 정보를 갖는 role
     * @return jwtToken
     * */
    public String generateToken(Long userId, User.Role role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_TIME);

        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("role", role.ordinal())
                .withIssuedAt(now)
                .withExpiresAt(expiry)
                .sign(Algorithm.HMAC256(secretKey));
    }

    /**
     * 만료된 토큰에 대해서 갱신
     * */
    public String refreshToekn(Long userId, User.Role role){
        return null;
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    // 토큰에서 정보 추출
    public Long getUserIdFromToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
        return Long.valueOf(decodedJWT.getSubject());
    }

    public String getRoleFromToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
        return decodedJWT.getClaim("role").asString();
    }
}
