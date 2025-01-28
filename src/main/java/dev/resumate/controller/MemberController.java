package dev.resumate.controller;

import dev.resumate.config.security.JwtTokenDTO;
import dev.resumate.dto.MemberRequestDTO;
import dev.resumate.apiPayload.response.ApiResponseDTO;
import dev.resumate.dto.MemberResponseDTO;
import dev.resumate.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ApiResponseDTO<String> join(@RequestBody MemberRequestDTO.JoinDto request) {
        memberService.join(request);
        return ApiResponseDTO.onSuccess("회원가입 성공");
    }

    @PostMapping("/login")
    public ApiResponseDTO<MemberResponseDTO.TokenDTO> login(@RequestBody MemberRequestDTO.LoginDto request, HttpServletResponse response) {
        return ApiResponseDTO.onSuccess(memberService.login(request, response));
    }
}
