package com.Mybuddy.Myb.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando um recurso solicitado (pet, pedido, usuário, etc.) não é encontrado.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Retorna 404 Not Found
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}