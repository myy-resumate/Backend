package dev.resumate.controller;

import dev.resumate.apiPayload.response.ApiResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("/api/test"))
public class TestController {

    @GetMapping("/hello")
    public ApiResponseDTO<String> test() {
        //throw new BusinessBaseException(ErrorCode.UNAUTHORIZED);

        return ApiResponseDTO.onSuccess("테스트 성공");
    }
}
