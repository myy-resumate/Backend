package dev.resumate.controller;

import dev.resumate.apiPayload.response.ApiResponseDTO;
import dev.resumate.common.auth.AuthUser;
import dev.resumate.domain.Member;
import dev.resumate.dto.AiRequestDTO;
import dev.resumate.dto.AiResponseDTO;
import dev.resumate.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * AI 유사 질문 검색
     * @param member
     * @param request
     * @return
     */
    @PostMapping
    public ApiResponseDTO<AiResponseDTO.SimilarQuestionDTO> aiTool(@AuthUser Member member, @RequestBody AiRequestDTO.QuestionDTO request) {
        return ApiResponseDTO.onSuccess(aiService.similaritySearch(member, request));
    }
}
