package dev.resumate.service;

import dev.resumate.common.cookie.CookieUtil;
import dev.resumate.common.redis.domain.LogoutToken;
import dev.resumate.common.redis.domain.RefreshToken;
import dev.resumate.common.redis.repository.LogoutTokenRepository;
import dev.resumate.common.redis.repository.RefreshTokenRepository;
import dev.resumate.common.security.CustomUserDetails;
import dev.resumate.common.security.CustomUserDetailsService;
import dev.resumate.common.security.JwtTokenDTO;
import dev.resumate.common.security.JwtUtil;
import dev.resumate.converter.MemberConverter;
import dev.resumate.domain.Member;
import dev.resumate.dto.MemberRequestDTO;
import dev.resumate.apiPayload.exception.BusinessBaseException;
import dev.resumate.apiPayload.exception.ErrorCode;
import dev.resumate.dto.MemberResponseDTO;
import dev.resumate.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final static String COOKIE_NAME = "refresh_token";
    private final static int COOKIE_MAX_AGE = 7 * 24 * 60 * 60;  //쿠키 만료시간 - 1주

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieUtil cookieUtil;
    private final LogoutTokenRepository logoutTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * 회원가입
     * @param request
     */
    public void join(MemberRequestDTO.JoinDto request) {

        if (memberRepository.existsMemberByEmail(request.getEmail())) {
            throw new BusinessBaseException(ErrorCode.MEMBER_ALREADY_EXIST);
        }

        Member newMember = MemberConverter.toMember(request);
        newMember.encodePassword(passwordEncoder.encode(request.getPassword()));  //비밀번호 암호화
        memberRepository.save(newMember);
    }

    /**
     * 로그인
     * @param request
     * @param response
     * @return
     */
    public MemberResponseDTO.TokenDTO login(MemberRequestDTO.LoginDto request, HttpServletResponse response) {

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication auth = authenticationManager.authenticate(authToken);

        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();

        return createToken(customUserDetails, response);
    }

    /**
     * 로그아웃
     * @param request
     * @param member
     */
    public void logout(HttpServletRequest request, Member member) {
        String accessToken = request.getHeader("Authorization").split(" ")[1];

        LogoutToken logoutToken = LogoutToken.builder()
                .accessToken(accessToken)
                .memberId(member.getId())
                .build();
        logoutTokenRepository.save(logoutToken);

        String refreshToken = cookieUtil.getCookie(COOKIE_NAME, request);
        refreshTokenRepository.deleteById("RefreshToken:" + refreshToken);
    }

    //role 추출
    private String getRole(Collection<? extends GrantedAuthority> authorities) {

        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority authority = iterator.next();
        return authority.getAuthority();
    }

    private MemberResponseDTO.TokenDTO createToken(CustomUserDetails customUserDetails, HttpServletResponse response) {
        String username = customUserDetails.getUsername();
        String role = getRole(customUserDetails.getAuthorities());
        JwtTokenDTO jwtTokenDTO = jwtUtil.generateToken(username, role);

        //redis에 refresh토큰 저장
        saveRefreshToken(jwtTokenDTO.getRefreshToken(), customUserDetails.getUserId(), username);

        //쿠키에 추가
        cookieUtil.addCookie(response, COOKIE_NAME, jwtTokenDTO.getRefreshToken(), COOKIE_MAX_AGE);

        return MemberResponseDTO.TokenDTO.builder()
                .grantType(jwtTokenDTO.getGrantType())
                .accessToken(jwtTokenDTO.getAccessToken())
                .build();
    }

    private void saveRefreshToken(String refreshToken, Long memberId, String email) {

        RefreshToken token = RefreshToken.builder()
                .refreshToken(refreshToken)
                .memberId(memberId)
                .email(email)
                .build();
        refreshTokenRepository.save(token);
    }

    /**
     * 토큰 재발급
     * @param request
     * @param response
     * @return
     */
    public MemberResponseDTO.TokenDTO reissueToken(HttpServletRequest request, HttpServletResponse response) {

        //쿠키의 refresh토큰 꺼내고 검사
        String refreshToken = cookieUtil.getCookie(COOKIE_NAME, request);
        if (refreshToken == null) {
            throw new BusinessBaseException(ErrorCode.MEMBER_JWT_TOKEN_NULL);
        }

        //꺼낸 토큰 자체의 유효성 검증
        jwtUtil.validateToken(refreshToken);

        //redis에 해당 토큰이 있는지 체크
        RefreshToken redisRefreshToken = refreshTokenRepository.findById("RefreshToken:" + refreshToken).orElseThrow(() -> new BusinessBaseException(ErrorCode.MEMBER_REDIS_TOKEN_NOT_FOUND));

        //토큰 발급을 위한 customUserDetails 찾기
        CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(redisRefreshToken.getEmail());

        //기존 refresh 토큰 삭제
        refreshTokenRepository.deleteById("RefreshToken:" + refreshToken);
        //토큰 재발급
        return createToken(customUserDetails, response);
    }

    public MemberResponseDTO.NameDTO getName(Member member) {

        return MemberResponseDTO.NameDTO.builder()
                .memberId(member.getId())
                .name(member.getName())
                .build();
    }
}
