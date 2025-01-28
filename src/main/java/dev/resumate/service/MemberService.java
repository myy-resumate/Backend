package dev.resumate.service;

import dev.resumate.config.redis.domain.RefreshToken;
import dev.resumate.config.redis.repository.RefreshTokenRepository;
import dev.resumate.config.security.CustomUserDetails;
import dev.resumate.config.security.JwtTokenDTO;
import dev.resumate.config.security.JwtUtil;
import dev.resumate.converter.MemberConverter;
import dev.resumate.domain.Member;
import dev.resumate.dto.MemberRequestDTO;
import dev.resumate.apiPayload.exception.BusinessBaseException;
import dev.resumate.apiPayload.exception.ErrorCode;
import dev.resumate.dto.MemberResponseDTO;
import dev.resumate.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

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
        saveRefreshToken(customUserDetails.getUserId(), jwtTokenDTO.getRefreshToken());

        return MemberResponseDTO.TokenDTO.builder()
                .grantType(jwtTokenDTO.getGrantType())
                .accessToken(jwtTokenDTO.getAccessToken())
                .refreshToken(jwtTokenDTO.getRefreshToken())
                .build();
    }

    private void saveRefreshToken(Long userId, String refreshToken) {

        RefreshToken token = RefreshToken.builder()
                .id(userId)
                .refreshToken(refreshToken)
                .build();
        refreshTokenRepository.save(token);
    }
}
