package dev.resumate.common.security;

import dev.resumate.apiPayload.exception.BusinessBaseException;
import dev.resumate.apiPayload.exception.ErrorCode;
import dev.resumate.common.security.CustomUserDetailsService;
import dev.resumate.common.security.JwtTokenDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private static final Long EXPIRED_MS_ACCESS_TOKEN = 60 * 60 * 1000L;  //토큰 만료 시간(1시간)
    private static final Long EXPIRED_MS_REFRESH_TOKEN = 7 * 24 * 60 * 60 * 1000L; //1주
    private final CustomUserDetailsService customUserDetailsService;

    public JwtUtil(@Value("${spring.jwt.secret}") String secretKey, CustomUserDetailsService customUserDetailsService) {
        this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.customUserDetailsService = customUserDetailsService;
    }

    //AccessToken, RefreshToken 생성하는 메서드
    public JwtTokenDTO generateToken(String username, String role) {

        //access 토큰 생성
        String accessToken = Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRED_MS_ACCESS_TOKEN))
                .signWith(secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .expiration(new Date(System.currentTimeMillis() + EXPIRED_MS_REFRESH_TOKEN))
                .signWith(secretKey)
                .compact();

        return JwtTokenDTO.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //토큰이 유효한지 검증
    public boolean validateToken(String token) {  //try-catch문 대신 GlobalExceptionHandler로 처리하기
        try {
            parseJwt(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new BusinessBaseException(ErrorCode.MEMBER_JWT_TOKEN_EXPIRE);
        } catch (JwtException e) {
            throw new BusinessBaseException(ErrorCode.MEMBER_JWT_TOKEN_INVALID);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
        }
        return false;
    }

    //토큰에서 Authentication 객체 가져오기
    public Authentication getAuthentication(String token) {
        Claims claims = parseJwt(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(claims.get("username", String.class));
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    //jwt 파싱
    public Claims parseJwt(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
