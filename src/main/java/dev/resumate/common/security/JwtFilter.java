package dev.resumate.common.security;

import dev.resumate.apiPayload.exception.BusinessBaseException;
import dev.resumate.apiPayload.exception.ErrorCode;
import dev.resumate.common.redis.repository.LogoutTokenRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final LogoutTokenRepository logoutTokenRepository;

    //JWT 토큰 검증
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        boolean permitPass = Arrays.stream(SecurityConfig.PERMIT_ALL_URL)
                .anyMatch(str -> new AntPathMatcher().match(str, request.getServletPath()));

        if (permitPass) {  //permitAll인 요청은 토큰 검증x
            filterChain.doFilter(request, response);
            return;
        }

        //요청 헤더에서 "Authorization" 키의 값을 추출
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessBaseException(ErrorCode.MEMBER_JWT_TOKEN_NULL);
        }

        String token = authorization.split(" ")[1];
        jwtUtil.validateToken(token);

        //블랙 리스트 토큰 검사
        if (logoutTokenRepository.findById(token).isPresent()) {
            throw new BusinessBaseException(ErrorCode.UNAUTHORIZED);
        }

        Authentication authToken = jwtUtil.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }

}
