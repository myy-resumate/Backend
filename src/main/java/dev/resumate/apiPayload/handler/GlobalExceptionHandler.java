package dev.resumate.apiPayload.handler;

import dev.resumate.apiPayload.exception.BusinessBaseException;
import dev.resumate.apiPayload.exception.ErrorCode;
import dev.resumate.apiPayload.response.ApiResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ApiResponseDTO> handle(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException - " + e.getMessage(), e);
        return createErrorResponseEntity(ErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(BusinessBaseException.class)
    protected ResponseEntity<ApiResponseDTO> handle(BusinessBaseException e) {
        log.error("BusinessException - " + e.getMessage(), e);
        return createErrorResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponseDTO> handle(Exception e) {
        e.printStackTrace();
        log.error("Exception - " + e.getMessage(), e);
        return createErrorResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiResponseDTO> createErrorResponseEntity(ErrorCode errorCode) {
        return new ResponseEntity<>(
                ApiResponseDTO.onFailure(errorCode.getCode(), errorCode.getMessage()),
                errorCode.getHttpStatus()
        );
    }

}
