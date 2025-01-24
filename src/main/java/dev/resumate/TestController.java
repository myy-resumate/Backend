package dev.resumate;

import dev.resumate.global.exception.BusinessBaseException;
import dev.resumate.global.exception.ErrorCode;
import dev.resumate.global.response.ApiResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("/api/test"))
public class TestController {

    @GetMapping("/hello")
    public ApiResponseDTO<String> test() {
        //throw new BusinessBaseException(ErrorCode.UNAUTHORIZED);

        return ApiResponseDTO.onSuccess("테스트 성공성공성공성공");
    }
}
