package dev.resumate.service;

import dev.resumate.common.cookie.CookieUtil;
import dev.resumate.common.redis.domain.RefreshToken;
import dev.resumate.common.redis.repository.LogoutTokenRepository;
import dev.resumate.common.redis.repository.RefreshTokenRepository;
import dev.resumate.common.security.CustomUserDetails;
import dev.resumate.common.security.CustomUserDetailsService;
import dev.resumate.common.security.JwtTokenDTO;
import dev.resumate.common.security.JwtUtil;
import dev.resumate.domain.Member;
import dev.resumate.domain.enums.Role;
import dev.resumate.dto.MemberRequestDTO;
import dev.resumate.dto.MemberResponseDTO;
import dev.resumate.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class MemberServiceTest {

    @InjectMocks  //테스트 대상의 가짜 객체
    private MemberService memberService;
    @Mock  //주입할 가짜 객체
    private MemberRepository memberRepository;
    @Spy  //주입할 진짜 객체
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private LogoutTokenRepository logoutTokenRepository;
    @Mock
    private CookieUtil cookieUtil;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private UsernamePasswordAuthenticationToken authToken;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private Authentication auth;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private CustomUserDetailsService customUserDetailsService;
    private Member member;
    private JwtTokenDTO jwtTokenDTO;

    @BeforeEach
    void init() {
        this.member = createTestMember();
        this.jwtTokenDTO = JwtTokenDTO.builder()
                .grantType("Bearer")
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
    }

    @Test
    @DisplayName("회원 가입 성공")
    void join() {
        //given
        MemberRequestDTO.JoinDto request = MemberRequestDTO.JoinDto.builder()
                .name(member.getName())
                .email(member.getEmail())
                .password(member.getPassword())
                .build();
        when(memberRepository.existsMemberByEmail(member.getEmail())).thenReturn(false);
        when(memberRepository.save(member)).thenReturn(member);

        //when&then
        //예외 발생 안해야 통과
        assertDoesNotThrow(() -> memberService.join(request));

        //호출 잘 됐는지
        verify(memberRepository).existsMemberByEmail(member.getEmail());
        verify(memberRepository).save(any());
    }

    @Test
    @DisplayName("로그인 성공")
    void login() {
        //given
        MemberRequestDTO.LoginDto request = MemberRequestDTO.LoginDto.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .build();
        CustomUserDetails customUserDetails = new CustomUserDetails(member);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(customUserDetails);
        when(jwtUtil.generateToken(any(), any())).thenReturn(jwtTokenDTO);

        //when
        MemberResponseDTO.TokenDTO tokenDTO = memberService.login(request, httpServletResponse);

        //then
        assertThat(tokenDTO.getGrantType()).isEqualTo("Bearer");
        assertThat(tokenDTO.getAccessToken()).isEqualTo("accessToken");
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout() {
        //given
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer accessToken");
        when(cookieUtil.getCookie("refresh_token", httpServletRequest)).thenReturn("refreshToken");

        //when&then
        assertDoesNotThrow(() -> memberService.logout(httpServletRequest, member));
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    void reissueToken() {
        //given
        when(cookieUtil.getCookie(any(), any())).thenReturn("refreshToken");
        when(refreshTokenRepository.findById(any())).thenReturn(Optional.ofNullable(RefreshToken.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .refreshToken("refreshToken")
                .build()));
        when(customUserDetailsService.loadUserByUsername(any())).thenReturn(new CustomUserDetails(member));
        when(jwtUtil.generateToken(any(), any())).thenReturn(jwtTokenDTO);

        //when
        MemberResponseDTO.TokenDTO tokenDTO = memberService.reissueToken(httpServletRequest, httpServletResponse);

        //then
        assertThat(tokenDTO.getGrantType()).isEqualTo("Bearer");
        assertThat(tokenDTO.getAccessToken()).isEqualTo("accessToken");
    }

    @Test
    @DisplayName("멤버 NameDTO 반환 성공")
    void getName() {
        //when
        MemberResponseDTO.NameDTO nameDTO = memberService.getName(member);

        //then
        assertThat(nameDTO.getMemberId()).isEqualTo(member.getId());
        assertThat(nameDTO.getName()).isEqualTo(member.getName());
    }

    private Member createTestMember() {
        return Member.builder()
                .id(1L)
                .name("테스트")
                .email("test@email.com")
                .password("testPassword")
                .role(Role.MEMBER)
                .build();
    }
}