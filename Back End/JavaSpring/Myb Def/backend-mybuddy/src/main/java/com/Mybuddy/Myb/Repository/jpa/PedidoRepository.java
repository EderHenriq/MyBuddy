package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.Pedido;
import com.Mybuddy.Myb.Model.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório JPA para a entidade Pedido (PostgreSQL).
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByPetshopId(Long petshopId);
    List<Pedido> findByClienteId(Long clienteId);
    List<Pedido> findByStatusAndDataCriacaoBefore(StatusPedido status, LocalDateTime limite);
}
