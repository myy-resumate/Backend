package dev.resumate.controller;

import dev.resumate.common.auth.AuthUser;
import dev.resumate.domain.Member;
import dev.resumate.dto.MemberRequestDTO;
import dev.resumate.apiPayload.response.ApiResponseDTO;
import dev.resumate.dto.MemberResponseDTO;
import dev.resumate.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입
     * @param request
     * @return
     */
    @PostMapping
    public ApiResponseDTO<String> join(@RequestBody MemberRequestDTO.JoinDto request) {
        memberService.join(request);
        return ApiResponseDTO.onSuccess("회원가입 성공");
    }

    /**
     * 로그인
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/login")
    public ApiResponseDTO<MemberResponseDTO.TokenDTO> login(@RequestBody MemberRequestDTO.LoginDto request, HttpServletResponse response) {
        return ApiResponseDTO.onSuccess(memberService.login(request, response));
    }

    /**
     * 로그아웃
     * @param request
     * @param member
     * @return
     */
    @PostMapping("/logout")
    public ApiResponseDTO<String> logout(HttpServletRequest request, @AuthUser Member member) {
        memberService.logout(request, member);
        return ApiResponseDTO.onSuccess("로그아웃 성공");
    }

    /**
     * 토큰 재발급
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/reissue")
    public ApiResponseDTO<MemberResponseDTO.TokenDTO> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        return ApiResponseDTO.onSuccess(memberService.reissueToken(request, response));
    }

    /**
     * 이름 조회
     * @param member
     * @return
     */
    @GetMapping("/names")
    public ApiResponseDTO<MemberResponseDTO.NameDTO> getName(@AuthUser Member member) {
        return ApiResponseDTO.onSuccess(memberService.getName(member));
    }
}
