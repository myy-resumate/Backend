package dev.resumate.controller;

import dev.resumate.apiPayload.response.ApiResponseDTO;
import dev.resumate.common.auth.AuthUser;
import dev.resumate.domain.Member;
import dev.resumate.dto.ResumeRequestDTO;
import dev.resumate.dto.ResumeResponseDTO;
import dev.resumate.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    /**
     * 지원서 저장
     * @param member
     * @param request
     * @param files
     * @return
     */
    @PostMapping
    public ApiResponseDTO<ResumeResponseDTO.CreateResultDTO> createResume(@AuthUser Member member,
                                                                          @RequestPart(value = "request") ResumeRequestDTO.CreateDTO request,
                                                                          @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return ApiResponseDTO.onSuccess(resumeService.saveResume(member, request, files));
    }
}
