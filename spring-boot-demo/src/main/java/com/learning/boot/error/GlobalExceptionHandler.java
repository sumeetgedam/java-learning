package com.learning.boot.error;

import com.learning.boot.user.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(
                        error.getField(),
                        error.getDefaultMessage()
                ));

        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Constraint Violation",
                Map.of(
                        "request",
                        exception.getMessage()
                )
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(
            UserNotFoundException exception
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(response);
    }


//    @ExceptionHandler(UserNotFoundException.class)
//    public ProblemDetail handleUserNotFound(
//            UserNotFoundException exception
//    ) {
//        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
//                HttpStatus.NOT_FOUND,
//                exception.getMessage()
//        );
//
//        problem.setTitle("User not found");
//        problem.setProperty(
//                "errorCode",
//                "USER_NOT_FOUND"
//        );
//
//        return problem;
//    }
}
