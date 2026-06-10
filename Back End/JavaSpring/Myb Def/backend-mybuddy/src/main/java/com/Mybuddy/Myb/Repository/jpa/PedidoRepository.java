package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade Pedido (PostgreSQL).
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    java.util.List<Pedido> findByPetshopId(Long petshopId);
}
