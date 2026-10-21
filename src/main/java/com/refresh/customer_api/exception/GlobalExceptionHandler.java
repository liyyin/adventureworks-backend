package com.refresh.customer_api.exception;

import com.refresh.customer_api.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404: data tidak ditemukan
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request){
        return build(HttpStatus.NOT_FOUND, ex.getMessage(),request);
    }

    // 404: URL tidak ada
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex, HttpServletRequest request){
        return build(HttpStatus.NOT_FOUND, "Endpoint tidak ditemukan", request);
    }

    // 400: tipe parameter salah, misalnya /products/abc
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {
        String message = "Parameter '" + ex.getName() + "' tidak valid: " + ex.getValue();
        return build(HttpStatus.BAD_REQUEST, message, request);
    }

    // 400: field sort tidak dikenal, misalnya ?sort=harga
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErrorResponse> handleBadSort(PropertyReferenceException ex,
                                                       HttpServletRequest request) {
        String message = "Field '" + ex.getPropertyName() + "' tidak dikenal";
        return build(HttpStatus.BAD_REQUEST, message, request);
    }

    // 500: semua error lain yang tidak terduga
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex,
                                                          HttpServletRequest request) {
        log.error("Unexpected error at {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request){
        ErrorResponse body = ErrorResponse.of(
                status.value(),status.getReasonPhrase(),message,request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
