package ru.practicum;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.dto.ApiError;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiError handleIllegalArgumentException(ConstraintViolationException e) {
        return new ApiError(
                HttpStatus.CONFLICT,
                e.getCause().toString(),
                e.getMessage(),
                LocalDateTime.now(),
                null);
    }

    @ExceptionHandler(NotFoundException.class)
    public ApiError handleIllegalArgumentException(NotFoundException e) {
        return new ApiError(
                HttpStatus.NOT_FOUND,
                "The required object was not found.",
                e.getMessage(),
                LocalDateTime.now(),
                null);
    }

    @ExceptionHandler(ConditionNotMetException.class)
    public ApiError handleIllegalArgumentException(ConditionNotMetException e) {
        return new ApiError(
                HttpStatus.CONFLICT,
                "For the requested operation the conditions are not met.",
                e.getMessage(),
                LocalDateTime.now(),
                null);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ApiError handleDtoValidationException(ConstraintViolationException e) {
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now(),
                null);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiError handleMethodValidationException(MethodArgumentNotValidException e) {
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now(),
                null);
    }
    @ExceptionHandler(NumberFormatException.class)
    public ApiError handleMethodValidationException(NumberFormatException e) {
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now(),
                null);
    }



    @ExceptionHandler(Exception.class)
    public ApiError handleConflictException(Exception e) {
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now(),
                null);
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiError handleNotFoundException(RuntimeException e) {
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now(),
                null);
    }
}

