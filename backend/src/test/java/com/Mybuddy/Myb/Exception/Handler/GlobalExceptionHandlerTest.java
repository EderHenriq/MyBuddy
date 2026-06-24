package com.Mybuddy.Myb.Exception.Handler;

import com.Mybuddy.Myb.Exception.ConflictException;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
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

    @Test
    void handleAuthorizationDeniedException_DeveRetornar403() {
        AuthorizationDeniedException ex = mock(AuthorizationDeniedException.class);
        when(ex.getMessage()).thenReturn("Usuário sem permissão");

        ResponseEntity<?> response = handler.handleAuthorizationDeniedException(ex, webRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void handleBadRequestException_ComIllegalArgumentException_DeveRetornar400() {
        IllegalArgumentException ex = new IllegalArgumentException("Argumento inválido");

        ResponseEntity<?> response = handler.handleBadRequestException(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleBadRequestException_ComIllegalStateException_DeveRetornar400() {
        IllegalStateException ex = new IllegalStateException("Estado inválido da operação");

        ResponseEntity<?> response = handler.handleBadRequestException(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleMercadoPagoException_ComMPException_DeveRetornar502() throws Exception {
        MPException ex = new MPException("Falha na integração com Mercado Pago");

        ResponseEntity<?> response = handler.handleMercadoPagoException(ex, webRequest);

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
    }

    @Test
    void handleMercadoPagoException_ComMPApiException_DeveRetornar502() throws Exception {
        MPApiException ex = mock(MPApiException.class);
        when(ex.getMessage()).thenReturn("Erro na API do Mercado Pago");

        ResponseEntity<?> response = handler.handleMercadoPagoException(ex, webRequest);

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
    }

    @Test
    void handleGlobalException_DeveRetornar500() {
        Exception ex = new RuntimeException("Erro inesperado no servidor");

        ResponseEntity<?> response = handler.handleGlobalException(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
