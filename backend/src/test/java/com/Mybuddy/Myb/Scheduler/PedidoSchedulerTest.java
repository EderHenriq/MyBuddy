package com.Mybuddy.Myb.Scheduler;

import com.Mybuddy.Myb.Model.Pedido;
import com.Mybuddy.Myb.Model.StatusPedido;
import com.Mybuddy.Myb.Repository.jpa.PedidoRepository;
import com.Mybuddy.Myb.Service.PedidoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoSchedulerTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoScheduler pedidoScheduler;

    @Test
    void cancelarPedidosPendentesExpirados_ComPedidosExpirados_DeveCancelarTodos() {
        Pedido pedido1 = new Pedido();
        pedido1.setId(101L);
        pedido1.setStatus(StatusPedido.PENDENTE);

        Pedido pedido2 = new Pedido();
        pedido2.setId(102L);
        pedido2.setStatus(StatusPedido.PENDENTE);

        when(pedidoRepository.findByStatusAndDataCriacaoBefore(eq(StatusPedido.PENDENTE), any(LocalDateTime.class)))
                .thenReturn(List.of(pedido1, pedido2));

        pedidoScheduler.cancelarPedidosPendentesExpirados();

        verify(pedidoService, times(1)).cancelarPedidoExpirado(pedido1);
        verify(pedidoService, times(1)).cancelarPedidoExpirado(pedido2);
    }

    @Test
    void cancelarPedidosPendentesExpirados_SemPedidosExpirados_NaoDeveChamarServico() {
        when(pedidoRepository.findByStatusAndDataCriacaoBefore(eq(StatusPedido.PENDENTE), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        pedidoScheduler.cancelarPedidosPendentesExpirados();

        verify(pedidoService, never()).cancelarPedidoExpirado(any());
    }
}
