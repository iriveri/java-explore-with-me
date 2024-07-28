package ru.practicum;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.dto.ApiError;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException e) {
        ApiError apiError = new ApiError(e.getStackTrace(),e.getMessage(),"Incorrectly made request.", HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleConflictException(Exception e) {
        ApiError apiError = new ApiError(e.getStackTrace(),e.getMessage(),"Integrity constraint has been violated.", HttpStatus.CONFLICT, LocalDateTime.now());
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleNotFoundException(RuntimeException e) {
        ApiError apiError = new ApiError(e.getStackTrace(),e.getMessage(),"The required object was not found.", HttpStatus.NOT_FOUND, LocalDateTime.now());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }
}

