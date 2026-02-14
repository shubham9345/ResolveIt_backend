package com.org.ResolveIt.security.Handler;

import com.org.ResolveIt.Exception.ComplaintNotFoundException;
import com.org.ResolveIt.Exception.UserNotFoundException;
import com.org.ResolveIt.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse();
        error.setMessage("Invalid username or password");
        error.setTimestamp(LocalDateTime.now());
        error.setPath(request.getRequestURI());
        error.setStatus(HttpStatus.UNAUTHORIZED.value());

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException e,
            HttpServletRequest request) {

        return new ResponseEntity<>(
                createNotFoundResponse(e.getMessage(), request),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ComplaintNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleComplaintNotFound(
            ComplaintNotFoundException e,
            HttpServletRequest request) {

        return new ResponseEntity<>(
                createNotFoundResponse(e.getMessage(), request),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobal(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse();
        error.setMessage("Something went wrong. Please try again later.");
        error.setTimestamp(LocalDateTime.now());
        error.setPath(request.getRequestURI());
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse createNotFoundResponse(String message, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(message);
        error.setTimestamp(LocalDateTime.now());
        error.setPath(request.getRequestURI());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        return error;
    }
}
