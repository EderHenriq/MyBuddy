package com.Mybuddy.Myb.Exception.Handler;

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
@SuppressWarnings("null")
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Trata falhas de validação de campos de requisição (Bean Validation), retornando
     * um mapa de nome do campo para a mensagem de erro correspondente.
     */
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

    /** Trata recursos não encontrados, retornando 404. */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logger.error("Recurso não encontrado: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false)),
                HttpStatus.NOT_FOUND
        );
    }

    /** Trata conflitos de regra de negócio (ex: dado duplicado), retornando 409. */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorDetails> handleConflictException(ConflictException ex, WebRequest request) {
        logger.error("Conflito de dados: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false)),
                HttpStatus.CONFLICT
        );
    }

    /** Trata conflitos de concorrência otimista (ex: dois usuários editando o mesmo registro), retornando 409. */
    @ExceptionHandler(org.springframework.dao.OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorDetails> handleOptimisticLockingFailureException(org.springframework.dao.OptimisticLockingFailureException ex, WebRequest request) {
        logger.error("Concorrência de dados detectada: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorDetails(LocalDateTime.now(), "O produto selecionado foi atualizado por outro usuário. Por favor, tente novamente.", request.getDescription(false)),
                HttpStatus.CONFLICT
        );
    }

    /** Trata acessos negados por falta de permissão, retornando 403. */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAuthorizationDeniedException(AuthorizationDeniedException ex, WebRequest request) {
        logger.warn("Acesso negado: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorDetails(LocalDateTime.now(), "Acesso negado: " + ex.getMessage(), request.getDescription(false)),
                HttpStatus.FORBIDDEN
        );
    }

    /** Trata argumentos inválidos ou estado inconsistente na requisição, retornando 400. */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorDetails> handleBadRequestException(RuntimeException ex, WebRequest request) {
        logger.error("Requisição inválida: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false)),
                HttpStatus.BAD_REQUEST
        );
    }

    /** Trata falhas de comunicação com a API do Mercado Pago, retornando 502. */
    @ExceptionHandler({com.mercadopago.exceptions.MPException.class, com.mercadopago.exceptions.MPApiException.class})
    public ResponseEntity<ErrorDetails> handleMercadoPagoException(Exception ex, WebRequest request) {
        logger.error("Erro na API do Mercado Pago: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(
                new ErrorDetails(LocalDateTime.now(), "Serviço de pagamento temporariamente indisponível. Por favor, tente novamente mais tarde.", request.getDescription(false)),
                HttpStatus.BAD_GATEWAY
        );
    }

    /** Trata qualquer exceção não mapeada pelos handlers anteriores, retornando 500. */
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