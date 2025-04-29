package dev.resumate.controller;

import dev.resumate.apiPayload.response.ApiResponseDTO;
import dev.resumate.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/test")
    public ApiResponseDTO<?> aiTest() {
        return ApiResponseDTO.onSuccess(aiService.pineconeTest());
    }
}
