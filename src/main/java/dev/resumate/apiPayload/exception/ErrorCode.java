package dev.resumate.apiPayload.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //일반적인 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "COMMON402", "Validation Error입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "요청한 정보를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON405", "잘못된 HTTP 메서드를 호출했습니다."),

    //Member 관련
    MEMBER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "MEMBER400", "이미 가입된 회원입니다."),
    MEMBER_JWT_TOKEN_EXPIRE(HttpStatus.BAD_REQUEST, "MEMBER401", "토큰이 만료되었습니다."),
    MEMBER_JWT_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "MEMBER402", "유효하지 않은 토큰입니다."),
    MEMBER_JWT_TOKEN_NULL(HttpStatus.BAD_REQUEST, "MEMBER403", "토큰이 존재하지 않습니다."),
    MEBER_LOGOUT_STATUS(HttpStatus.BAD_REQUEST, "MEMBER404", "이미 로그아웃된 상태입니다."),


    ;



    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
