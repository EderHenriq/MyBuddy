package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.CupomUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para controle de uso de cupons por usuário.
 * Usado para garantir a regra de uso único por usuário (anti-abuso).
 */
@Repository
public interface CupomUsuarioRepository extends JpaRepository<CupomUsuario, Long> {

    /**
     * Verifica se um determinado usuário já utilizou este cupom anteriormente.
     */
    boolean existsByCupomIdAndUsuarioId(Long cupomId, Long usuarioId);

    /**
     * Busca o registro de uso de um cupom por um usuário específico.
     */
    Optional<CupomUsuario> findByCupomIdAndUsuarioId(Long cupomId, Long usuarioId);
}
