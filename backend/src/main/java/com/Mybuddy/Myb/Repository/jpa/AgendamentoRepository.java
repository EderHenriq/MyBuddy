package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.Agendamento;
import com.Mybuddy.Myb.Model.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByClienteIdOrderByDataHoraInicioDesc(Long clienteId);

    List<Agendamento> findByServicoPetshopIdOrderByDataHoraInicioDesc(Long petshopId);

    @Query("""
            select count(a) > 0
            from Agendamento a
            where a.petId = :petId
              and a.status <> :statusCancelado
              and a.dataHoraInicio < :fim
              and a.dataHoraFim > :inicio
            """)
    boolean existsConflitoPet(
            @Param("petId") Long petId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("statusCancelado") StatusAgendamento statusCancelado);

    @Query("""
            select count(a) > 0
            from Agendamento a
            where a.servico.petshop.id = :petshopId
              and lower(a.profissionalNome) = lower(:profissionalNome)
              and a.status <> :statusCancelado
              and a.dataHoraInicio < :fim
              and a.dataHoraFim > :inicio
            """)
    boolean existsConflitoProfissional(
            @Param("petshopId") Long petshopId,
            @Param("profissionalNome") String profissionalNome,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("statusCancelado") StatusAgendamento statusCancelado);
}
