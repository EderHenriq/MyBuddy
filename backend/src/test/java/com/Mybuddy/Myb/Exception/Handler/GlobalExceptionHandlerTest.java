package com.Mybuddy.Myb.Exception.Handler;

import com.Mybuddy.Myb.Exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.dao.OptimisticLockingFailureException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        when(webRequest.getDescription(false)).thenReturn("uri=/test");
    }

    @Test
    void handleConflictException_DeveRetornar409() {
        ConflictException ex = new ConflictException("Registro já existe");

        ResponseEntity<?> response = handler.handleConflictException(ex, webRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void handleOptimisticLockingFailureException_DeveRetornar409ComMensagemGenerica() {
        OptimisticLockingFailureException ex = new OptimisticLockingFailureException("versão desatualizada");

        ResponseEntity<?> response = handler.handleOptimisticLockingFailureException(ex, webRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}
