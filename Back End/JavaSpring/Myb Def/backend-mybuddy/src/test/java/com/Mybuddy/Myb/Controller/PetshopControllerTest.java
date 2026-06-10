package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Config.SecurityConfig;
import com.Mybuddy.Myb.Model.*;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import com.Mybuddy.Myb.Security.JwtAuthConverter;
import com.Mybuddy.Myb.Repository.jpa.PedidoRepository;
import com.Mybuddy.Myb.Repository.jpa.ProdutoRepository;
import com.Mybuddy.Myb.Repository.mongo.ChatRepository;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PetshopController.class)
@Import({SecurityConfig.class, JwtAuthConverter.class})
class PetshopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProdutoRepository produtoRepository;

    @MockitoBean
    private PedidoRepository pedidoRepository;

    @MockitoBean
    private ChatRepository chatRepository;

    @MockitoBean
    private KeycloakUserSyncService keycloakUserSyncService;

    private Usuario petshopUser;
    private Usuario adminUser;
    private Usuario regularUser;
    private Produto produto;
    private Pedido pedido;
    private Chat chat;

    @BeforeEach
    void setUp() {
        Role petshopRole = new Role(ERole.ROLE_PETSHOP);
        petshopUser = new Usuario();
        petshopUser.setId(10L);
        petshopUser.setPetshopId(2L);
        petshopUser.setRoles(Set.of(petshopRole));

        Role adminRole = new Role(ERole.ROLE_ADMIN);
        adminUser = new Usuario();
        adminUser.setId(1L);
        adminUser.setRoles(Set.of(adminRole));

        Role adopterRole = new Role(ERole.ROLE_ADOTANTE);
        regularUser = new Usuario();
        regularUser.setId(30L);
        regularUser.setRoles(Set.of(adopterRole));

        produto = new Produto();
        produto.setId(100L);
        produto.setNome("Ração Premium");

        pedido = new Pedido();
        pedido.setId(200L);

        chat = new Chat();
        chat.setId(300L);
        chat.setCliente("Cliente Teste");
    }

    @Test
    void deveRetornar401QuandoBuscarProdutosSemToken() throws Exception {
        mockMvc.perform(get("/api/petshop/produtos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar200QuandoBuscarProdutosComUsuarioComum() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(regularUser);
        when(produtoRepository.findAll()).thenReturn(List.of(produto));

        mockMvc.perform(get("/api/petshop/produtos")
                        .with(jwt().authorities(() -> "ROLE_ADOTANTE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Ração Premium"));

        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    void deveRetornar200QuandoBuscarProdutosComPetshopFiltrado() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(petshopUser);
        when(produtoRepository.findByPetshopId(2L)).thenReturn(List.of(produto));

        mockMvc.perform(get("/api/petshop/produtos")
                        .with(jwt().authorities(() -> "ROLE_PETSHOP")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Ração Premium"));

        verify(produtoRepository, times(1)).findByPetshopId(2L);
        verify(produtoRepository, never()).findAll();
    }

    @Test
    void deveRetornar403QuandoUserTentaBuscarPedidos() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(regularUser);

        mockMvc.perform(get("/api/petshop/pedidos")
                        .with(jwt().authorities(() -> "ROLE_ADOTANTE")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar200QuandoPetshopBuscaPedidosFiltrado() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(petshopUser);
        when(pedidoRepository.findByPetshopId(2L)).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/petshop/pedidos")
                        .with(jwt().authorities(() -> "ROLE_PETSHOP")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(200));

        verify(pedidoRepository, times(1)).findByPetshopId(2L);
        verify(pedidoRepository, never()).findAll();
    }

    @Test
    void deveRetornar200QuandoAdminBuscaPedidosSemFiltro() throws Exception {
        when(keycloakUserSyncService.syncUsuario(any())).thenReturn(adminUser);
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/petshop/pedidos")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(200));

        verify(pedidoRepository, times(1)).findAll();
        verify(pedidoRepository, never()).findByPetshopId(any());
    }

    @Test
    void deveRetornar403QuandoUserTentaBuscarChats() throws Exception {
        mockMvc.perform(get("/api/petshop/chats")
                        .with(jwt().authorities(() -> "ROLE_ADOTANTE")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar403QuandoPetshopTentaBuscarChats() throws Exception {
        mockMvc.perform(get("/api/petshop/chats")
                        .with(jwt().authorities(() -> "ROLE_PETSHOP")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar200QuandoAdminBuscaChats() throws Exception {
        when(chatRepository.findAll()).thenReturn(List.of(chat));

        mockMvc.perform(get("/api/petshop/chats")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cliente").value("Cliente Teste"));

        verify(chatRepository, times(1)).findAll();
    }
}
