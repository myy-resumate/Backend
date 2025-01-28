package dev.resumate.apiPayload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter  //getter가 있어야 private필드를 알 수 있어서 json 직렬화를 할 수 있다.
@AllArgsConstructor
@JsonPropertyOrder({"code", "message", "result"})  //json property의 순서를 지정
public class ApiResponseDTO<T> {

    private String code;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    //요청 성공 응답
    public static <T> ApiResponseDTO<T> onSuccess(T result) {
        return new ApiResponseDTO<>("COMMON200", "성공", result);
    }

    //요청 실패 응답
    public static <T> ApiResponseDTO<T> onFailure(String code, String message) {
        return new ApiResponseDTO<>(code, message, null);
    }
}
