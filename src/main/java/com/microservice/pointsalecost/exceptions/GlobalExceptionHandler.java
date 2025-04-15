package com.microservice.pointsalecost.exceptions;

import com.microservice.pointsalecost.dtos.ApiResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errores = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        logger.error("Validación fallida: {}", errores);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Validación fallida", errores);
        return new ResponseEntity<>(errorResponse, errorResponse.status());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        logger.error("Entidad no encontrada: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(errorResponse, errorResponse.status());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.error("Error en el formato del JSON: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Error en el formato del JSON");
        return new ResponseEntity<>(errorResponse, errorResponse.status());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        logger.error("Excepción de validación: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(errorResponse, errorResponse.status());
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException ex) {
        String message = ex.getCampo() != null ? ex.getCampo() + ": " + ex.getMessage() : ex.getMessage();
        logger.error("Excepción de aplicación: Campo={}, Mensaje={}", ex.getCampo(), ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT, message);
        return new ResponseEntity<>(errorResponse, errorResponse.status());
    }

    @ExceptionHandler(PointOfSaleNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<String>> handlePointOfSaleNotFound(PointOfSaleNotFoundException ex) {
        return new ResponseEntity<>(new ApiResponseDTO<>(false, ex.getMessage(), null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RedisCacheMissException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleRedisMiss(RedisCacheMissException ex) {
        return new ResponseEntity<>(new ApiResponseDTO<>(false, ex.getMessage(), null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<String>> handleGenericException(Exception ex) {
        return new ResponseEntity<>(new ApiResponseDTO<>(false, "Unexpected Error: " + ex.getMessage(), null),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(HttpClientErrorException ex) {
        logger.error("Error del cliente HTTP: Código={}, Texto={}", ex.getStatusCode(), ex.getStatusText());

        ErrorResponse errorResponse = new ErrorResponse((HttpStatus) ex.getStatusCode(), ex.getStatusText());
        return new ResponseEntity<>(errorResponse, errorResponse.status());
    }

    public record ErrorResponse(HttpStatus status, String message, List<String> errors) {
        public ErrorResponse(HttpStatus status, String message) {
            this(status, message, null);
        }
    }
}
