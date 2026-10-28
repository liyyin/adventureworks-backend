package com.refresh.customer_api.exception;

import com.refresh.customer_api.dto.ErrorResponse;
import com.refresh.customer_api.dto.ValidationErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

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

    // 400: Validasi @Valid Gagal
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request){
        Map<String,String> errors = new LinkedHashMap<>();
        for(FieldError fe : ex.getBindingResult().getFieldErrors()){
            errors.putIfAbsent(fe.getField(),fe.getDefaultMessage());
        }
        ValidationErrorResponse body = new ValidationErrorResponse(LocalDateTime.now(),400,"Bad Request","Validasi Gagal",request.getRequestURI(),errors);
        return ResponseEntity.badRequest().body(body);
    }

    // 400: JSON rusak atau format salah (misalnya format tanggal tidak valid)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex,
                                                                    HttpServletRequest request){
        return build(HttpStatus.BAD_REQUEST,"Format Request ada yang tidak valid",request);
    }

    // 400: aturan bisnis dilanggar
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex,
                                                          HttpServletRequest request){
        return build(HttpStatus.BAD_REQUEST,ex.getMessage(),request);
    }

    // 409: data bentrok (misalnya nama sudah dipakai)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex,
                                                        HttpServletRequest request){
        return build(HttpStatus.CONFLICT,ex.getMessage(),request);
    }
    // foreign key tidak dapat di table yang di join relasi
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex,
                                                             HttpServletRequest request){
        log.warn("Data Integrity Violation at {}: {}",
                request.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT,
                "Data tidak dapat disimpan karena melanggar aturan database (misalnya dipakai data lain)",
                request);
    }

    // 500: semua error lain yang tidak terduga
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex,
                                                          HttpServletRequest request) {
        log.error("Unexpected error at {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Terjadi kesalahan pada server", request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request){
        ErrorResponse body = ErrorResponse.of(
                status.value(),status.getReasonPhrase(),message,request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
