package dev.resumate.apiPayload.exception;

import lombok.Getter;

@Getter
//비즈니스 로직 관련 exception들은 이 클래스를 사용. 이외의 exception은 이 클래스처럼 새로 만들기
public class BusinessBaseException extends RuntimeException{

    private final ErrorCode errorCode;

    public BusinessBaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessBaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
