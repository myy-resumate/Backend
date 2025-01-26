package dev.resumate.controller;

import dev.resumate.dto.MemberRequestDTO;
import dev.resumate.global.response.ApiResponseDTO;
import dev.resumate.service.MemberService;
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
    public ApiResponseDTO join(@RequestBody MemberRequestDTO.MemberJoinDto request) {
        memberService.join(request);
        return ApiResponseDTO.onSuccess("회원가입 성공");
    }
}
