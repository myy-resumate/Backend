package dev.resumate.controller;

import dev.resumate.apiPayload.exception.BusinessBaseException;
import dev.resumate.apiPayload.exception.ErrorCode;
import dev.resumate.apiPayload.response.ApiResponseDTO;
import dev.resumate.common.auth.AuthUser;
import dev.resumate.domain.Member;
import dev.resumate.domain.Resume;
import dev.resumate.dto.ResumeRequestDTO;
import dev.resumate.dto.ResumeResponseDTO;
import dev.resumate.repository.ResumeRepository;
import dev.resumate.service.AttachmentService;
import dev.resumate.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final AttachmentService attachmentService;
    private final ResumeRepository resumeRepository;

    /**
     * 지원서 저장
     *
     * @param member
     * @param request
     * @param files
     * @return
     */
    @PostMapping
    public ApiResponseDTO<ResumeResponseDTO.CreateResultDTO> createResume(@AuthUser Member member,
                                                                          @RequestPart(value = "request") ResumeRequestDTO.CreateDTO request,
                                                                          @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {
        return ApiResponseDTO.onSuccess(resumeService.saveResume(member, request, files));
    }

    /**
     * 지원서 수정
     *
     * @param resumeId
     * @param request
     * @param files
     * @return
     */
    @PatchMapping("/{resumeId}")
    public ApiResponseDTO<ResumeResponseDTO.UpdateResultDTO> updateResume(@AuthUser Member member,
                                                                          @PathVariable Long resumeId,
                                                                          @RequestPart(value = "request") ResumeRequestDTO.UpdateDTO request,
                                                                          @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {
        return ApiResponseDTO.onSuccess(resumeService.updateResume(member, resumeId, request, files));
    }

    @DeleteMapping("/{resumeId}")
    public ApiResponseDTO<String> deleteResume(@PathVariable Long resumeId) {
        resumeService.deleteResume(resumeId);
        return ApiResponseDTO.onSuccess("지원서 삭제 성공");
    }
}
