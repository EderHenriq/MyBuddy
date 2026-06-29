package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Config.SecurityConfig;
import com.Mybuddy.Myb.Model.DonationSubscription;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.jpa.DonationSubscriptionRepository;
import com.Mybuddy.Myb.Security.JwtAuthConverter;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import com.Mybuddy.Myb.Service.PaymentService;
import com.Mybuddy.Myb.Util.MercadoPagoWebhookValidator;
import com.mercadopago.client.preapproval.PreapprovalClient;
import com.mercadopago.client.preapproval.PreapprovalUpdateRequest;
import com.mercadopago.resources.preapproval.Preapproval;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@Import({SecurityConfig.class, JwtAuthConverter.class})
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private KeycloakUserSyncService keycloakUserSyncService;

    @MockitoBean
    private MercadoPagoWebhookValidator webhookValidator;

    @MockitoBean
    private DonationSubscriptionRepository donationSubscriptionRepository;

    private Usuario usuario;
    private DonationSubscription subscription;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(10L);
        usuario.setEmail("user@test.com");

        subscription = DonationSubscription.builder()
                .id(1L)
                .mpPreapprovalId("sub-123")
                .usuarioId(10L)
                .amount(new BigDecimal("50.00"))
                .frequency("monthly")
                .status("pending")
                .build();
    }

    @Test
    void deveRetornar401AoCancelarAssinaturaSemToken() throws Exception {
        mockMvc.perform(post("/api/payments/subscribe/1/cancelar"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveCancelarAssinaturaComSucesso() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(usuario);
        when(donationSubscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));

        try (MockedConstruction<PreapprovalClient> mocked = Mockito.mockConstruction(PreapprovalClient.class,
                (mock, context) -> {
                    when(mock.update(eq("sub-123"), any(PreapprovalUpdateRequest.class)))
                            .thenReturn(new Preapproval());
                })) {

            mockMvc.perform(post("/api/payments/subscribe/1/cancelar")
                            .with(jwt().authorities(() -> "ROLE_ADOTANTE")))
                    .andExpect(status().isOk());

            assertEquals("cancelled", subscription.getStatus());
            verify(donationSubscriptionRepository, times(1)).save(subscription);
        }
    }

    @Test
    void deveRetornar403AoTentarCancelarAssinaturaDeOutroUsuario() throws Exception {
        subscription.setUsuarioId(99L); // de outro usuário
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(usuario);
        when(donationSubscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));

        mockMvc.perform(post("/api/payments/subscribe/1/cancelar")
                        .with(jwt().authorities(() -> "ROLE_ADOTANTE")))
                .andExpect(status().isForbidden());

        verify(donationSubscriptionRepository, never()).save(any());
    }
}
