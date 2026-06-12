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

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(p) > 0 FROM Pedido p JOIN p.itens i WHERE p.clienteId = :clienteId AND p.status = :status AND i.produto.id = :produtoId")
    boolean existeCompraConcluida(
            @org.springframework.data.repository.query.Param("clienteId") Long clienteId,
            @org.springframework.data.repository.query.Param("status") StatusPedido status,
            @org.springframework.data.repository.query.Param("produtoId") Long produtoId);
}
