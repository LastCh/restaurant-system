package com.restaurant.system.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Custom exceptions

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDetails> handleNotFoundException(
            NotFoundException ex,
            WebRequest request) {
        logger.warn("Resource not found: {}", ex.getMessage());
        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                "Not Found",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDetails> handleBadRequestException(
            BadRequestException ex,
            WebRequest request) {
        logger.warn("Bad request: {}", ex.getMessage());
        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                "Bad Request",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorDetails> handleUnauthorizedException(
            UnauthorizedException ex,
            WebRequest request) {
        logger.warn("Unauthorized access attempt: {}", ex.getMessage());
        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                "Unauthorized",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorDetails> handleForbiddenException(
            ForbiddenException ex,
            WebRequest request) {
        logger.warn("Forbidden access attempt: {}", ex.getMessage());
        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                "Forbidden",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorDetails> handleConflictException(
            ConflictException ex,
            WebRequest request) {
        logger.warn("Conflict: {}", ex.getMessage());
        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                "Conflict",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    // Spring Security exceptions

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request) {
        logger.warn("Access denied: {}", ex.getMessage());
        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.FORBIDDEN,
                "You don't have permission to access this resource",
                "Forbidden",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> handleBadCredentialsException(
            BadCredentialsException ex,
            WebRequest request) {
        logger.warn("Bad credentials: {}", ex.getMessage());
        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password",
                "Unauthorized",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ErrorDetails> handleInsufficientAuthenticationException(
            InsufficientAuthenticationException ex,
            WebRequest request) {
        logger.warn("Insufficient authentication: {}", ex.getMessage());
        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.UNAUTHORIZED,
                "Full authentication is required to access this resource",
                "Unauthorized",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDetails> handleAuthenticationException(
            AuthenticationException ex,
            WebRequest request) {
        logger.warn("Authentication failed: {}", ex.getMessage());
        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.UNAUTHORIZED,
                "Authentication failed: " + ex.getMessage(),
                "Unauthorized",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    // JWT exceptions

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorDetails> handleExpiredJwtException(
            ExpiredJwtException ex,
            WebRequest request) {
        logger.warn("JWT token expired: {}", ex.getMessage());
        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.UNAUTHORIZED,
                "JWT token has expired",
                "Unauthorized",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorDetails> handleMalformedJwtException(
            MalformedJwtException ex,
            WebRequest request) {
        logger.error("Malformed JWT token: {}", ex.getMessage());
        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.UNAUTHORIZED,
                "Invalid JWT token format",
                "Unauthorized",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorDetails> handleSignatureException(
            SignatureException ex,
            WebRequest request) {
        logger.error("JWT signature validation failed: {}", ex.getMessage());
        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.UNAUTHORIZED,
                "Invalid JWT signature",
                "Unauthorized",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    // Standard Spring exceptions

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {
        logger.warn("Illegal argument: {}", ex.getMessage());
        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                "Bad Request",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));

        logger.warn("Validation error: {}", message);
        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.BAD_REQUEST,
                message,
                "Validation Error",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDetails> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            WebRequest request) {
        String message = String.format("Parameter '%s' should be of type %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        logger.warn("Type mismatch: {}", message);
        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.BAD_REQUEST,
                message,
                "Bad Request",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDetails> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            WebRequest request) {
        String message = String.format("Required parameter '%s' is missing", ex.getParameterName());

        logger.warn("Missing parameter: {}", message);
        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.BAD_REQUEST,
                message,
                "Bad Request",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDetails> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            WebRequest request) {
        String message = String.format("HTTP method '%s' is not supported for this endpoint", ex.getMethod());

        logger.warn("Method not supported: {}", message);
        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.METHOD_NOT_ALLOWED,
                message,
                "Method Not Allowed",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorDetails> handleNoResourceFound(
            NoResourceFoundException ex,
            WebRequest request) {
        logger.warn("No resource found: {}", ex.getMessage());
        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.NOT_FOUND,
                "The requested resource was not found",
                "Not Found",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDetails> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            WebRequest request) {
        String message = "Data integrity violation";

        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("clients_email_key")) {
                message = "Email already exists";
            } else if (ex.getMessage().contains("clients_phone_key")) {
                message = "Phone already exists";
            } else if (ex.getMessage().contains("users_username_key")) {
                message = "Username already exists";
            } else if (ex.getMessage().contains("duplicate key")) {
                message = "Duplicate value detected";
            } else if (ex.getMessage().contains("foreign key constraint")) {
                message = "Cannot delete record - it is referenced by other records";
            }
        }

        logger.warn("Data integrity violation: {}", ex.getMessage());
        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.CONFLICT,
                message,
                "Conflict",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    // Fallback handler for all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(
            Exception ex,
            WebRequest request) {
        logger.error("Unexpected error occurred", ex);

        String message = "An unexpected error occurred";
        if (ex.getMessage() != null && !ex.getMessage().isEmpty()) {
            message = ex.getMessage();
        }

        ErrorDetails errorDetails = buildErrorDetails(
                HttpStatus.INTERNAL_SERVER_ERROR,
                message,
                "Internal Server Error",
                request
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Helper method to build ErrorDetails consistently
    private ErrorDetails buildErrorDetails(
            HttpStatus status,
            String message,
            String error,
            WebRequest request) {
        return ErrorDetails.builder()
                .status(status.value())
                .message(message)
                .error(error)
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
