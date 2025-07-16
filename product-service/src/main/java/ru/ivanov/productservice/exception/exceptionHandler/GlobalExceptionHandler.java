package ru.ivanov.productservice.exception.exceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.ivanov.productservice.dto.response.ErrorResponse;
import ru.ivanov.productservice.exception.ProductNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ProductNotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                request.getRequestURI(),
                ex.getMessage(),
                NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<String> errors = ex.getFieldErrors().stream()
                .map(error -> {
                    String message = error.getDefaultMessage();
                    return message != null ? message
                            : "validation failed on field %s"
                            .formatted(error.getField());
                })
                .toList();

        String detailedMessage = "Validation failed: " + errors;

        ErrorResponse errorResponse = new ErrorResponse(
                request.getRequestURI(),
                detailedMessage,
                BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                request.getRequestURI(),
                ex.getMessage(),
                BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("error", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                request.getRequestURI(),
                ex.getMessage(),
                INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }
}