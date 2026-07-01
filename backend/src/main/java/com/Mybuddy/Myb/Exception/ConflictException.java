package com.Mybuddy.Myb.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando uma operação viola uma regra de unicidade ou conflita
 * com o estado atual de um recurso (ex: CNPJ já cadastrado, cupom duplicado).
 */
@ResponseStatus(HttpStatus.CONFLICT) // Retorna 409 Conflict
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}