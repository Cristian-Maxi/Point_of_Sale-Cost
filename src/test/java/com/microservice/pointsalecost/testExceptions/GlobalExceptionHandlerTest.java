package com.microservice.pointsalecost.testExceptions;

import com.microservice.pointsalecost.dtos.ApiResponseDTO;
import com.microservice.pointsalecost.exceptions.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleApplicationException() {
        ApplicationException ex = new ApplicationException("campo", "Mensaje de error");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleApplicationException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getBody().status());
        assertEquals("campo: Mensaje de error", response.getBody().message());
    }

    @Test
    void testHandleHttpClientErrorException() {
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleHttpClientErrorException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().status());
        assertEquals("Bad Request", response.getBody().message());
    }

    @Test
    void testHandleValidationException() {
        ValidationException ex = new ValidationException("Validation failed");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().status());
        assertEquals("Validation failed", response.getBody().message());
    }

    @Test
    void testHandleValidationExceptions() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "defaultMessage");
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().status());
        assertTrue(response.getBody().errors().contains("fieldName: defaultMessage"));
    }

    @Test
    void testHandleEntityNotFoundException() {
        EntityNotFoundException ex = new EntityNotFoundException("Entity not found");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleEntityNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getBody().status());
        assertEquals("Entity not found", response.getBody().message());
    }

    @Test
    void testHandleHttpMessageNotReadableException() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Malformed JSON request");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleHttpMessageNotReadableException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error in JSON format", response.getBody().message());
    }

    @Test
    void testHandlePointOfSaleNotFoundException() {
        PointOfSaleNotFoundException ex = new PointOfSaleNotFoundException("Point of Sale not found");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handlePointOfSaleNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getBody().status());
        assertEquals("Point of Sale not found", response.getBody().message());
    }

    @Test
    void testHandleAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleAccessDeniedException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getBody().status());
        assertEquals("Access denied", response.getBody().message());
    }

    @Test
    void testLambdaHandleValidationExceptions() {
        FieldError error = new FieldError("objectName", "fieldName", "defaultMessage");
        String transformedError = error.getField() + ": " + error.getDefaultMessage();

        assertEquals("fieldName: defaultMessage", transformedError);
    }

    @Test
    void testHandleInactivePointOfSaleException() {
        InactivePointOfSaleException ex = new InactivePointOfSaleException("Point of Sale is inactive");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleInactivePointOfSaleException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getBody().status());
        assertEquals("Point of Sale is inactive", response.getBody().message());
    }

    @Test
    void testHandleRedisMiss() {
        RedisCacheMissException ex = new RedisCacheMissException("Redis cache miss error");

        ResponseEntity<ApiResponseDTO<String>> response = globalExceptionHandler.handleRedisMiss(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isEstado());
        assertEquals("Redis cache miss error", response.getBody().getMessage());
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new Exception("Unexpected error occurred");

        ResponseEntity<ApiResponseDTO<String>> response = globalExceptionHandler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isEstado());
        assertEquals("Unexpected Error: Unexpected error occurred", response.getBody().getMessage());
    }



    @Test
    void testHandleApplicationExceptionWithNullCampo() {
        ApplicationException ex = new ApplicationException(null, "Mensaje de error");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleApplicationException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getBody().status());
        assertEquals("Mensaje de error", response.getBody().message());
    }

    @Test
    void testApplicationExceptionWithMessageOnly() {
        ApplicationException ex = new ApplicationException("Mensaje de error");
        assertNull(ex.getCampo());
        assertEquals("Mensaje de error", ex.getMessage());
    }

    @Test
    void testErrorResponseStructure() {
        ApplicationException ex = new ApplicationException("campo", "Mensaje de error");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleApplicationException(ex);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CONFLICT, response.getBody().status());
        assertEquals("campo: Mensaje de error", response.getBody().message());
        assertNull(response.getBody().errors());
    }
}