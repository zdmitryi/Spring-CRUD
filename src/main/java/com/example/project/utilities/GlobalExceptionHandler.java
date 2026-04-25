package com.example.project.utilities;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;


@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception e){
        log.error("Handle exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(
                "Internal server error", e.getMessage(), LocalDateTime.now()
        ));
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(EntityNotFoundException e){
        log.error("Handle entity not found exception", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(
                "Entity not found", e.getMessage(), LocalDateTime.now()
        ));
    }


    @ExceptionHandler(exception = {
            IllegalArgumentException.class,
            IllegalStateException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(Exception e){
        log.error("Handle bad request exception", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
                "Bad request", e.getMessage(), LocalDateTime.now()
        ));
    }


    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponseDto> handleNoSuchElementException(NoSuchElementException e){
        log.error("Handle no such element exception", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
                "No such element", e.getMessage(), LocalDateTime.now()
        ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException e){
        log.error("Handle access denied exception", e);
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorResponseDto(
                "Permission denied", e.getMessage(), LocalDateTime.now()
        ));
    }


    public static record ErrorResponseDto(
        String message,
        String detailedMessage,
        LocalDateTime errorTime
    ) {}
}
