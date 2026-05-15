package com.Mybuddy.Myb.Exception.Handler;

import com.Mybuddy.Myb.Controller.OrganizacaoController;
import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(OrganizacaoController.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        logger.error("Erro de validação: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logger.error("Recurso não encontrado: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false)),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorDetails> handleConflictException(ConflictException ex, WebRequest request) {
        logger.error("Conflito de dados: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false)),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAuthorizationDeniedException(AuthorizationDeniedException ex, WebRequest request) {
        logger.warn("Acesso negado: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorDetails(LocalDateTime.now(), "Acesso negado", request.getDescription(false)),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Erro interno do servidor: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(
                new ErrorDetails(LocalDateTime.now(), "Ocorreu um erro interno no servidor: " + ex.getMessage(), request.getDescription(false)),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    record ErrorDetails(LocalDateTime timestamp, String message, String details) {}
}