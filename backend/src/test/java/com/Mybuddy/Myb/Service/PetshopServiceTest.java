package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.PetshopRequestDTO;
import com.Mybuddy.Myb.DTO.PetshopResponseDTO;
import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.Petshop;
import com.Mybuddy.Myb.Model.StatusAprovacao;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.jpa.PetshopRepository;
import com.Mybuddy.Myb.Repository.mongo.UsuarioRepository;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authorization.AuthorizationDeniedException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PetshopServiceTest {

    @Mock
    private PetshopRepository petshopRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PetshopService petshopService;

    private PetshopRequestDTO requestDTO;
    private Usuario usuario;
    private Petshop petshop;

    @BeforeEach
    void setUp() {
        requestDTO = new PetshopRequestDTO("Petshop do Ed", "ed@petshop.com", "12345678901234", "4499999999", "Av Principal", "Descrição legal", "www.edpet.com", null);
        usuario = new Usuario("Eder", "eder@gmail.com", "4499999999", "password");
        usuario.setId(1L);

        petshop = Petshop.builder()
                .id(10L)
                .nomeFantasia(requestDTO.getNomeFantasia())
                .emailContato(requestDTO.getEmailContato())
                .cnpj(requestDTO.getCnpj())
                .telefoneContato(requestDTO.getTelefoneContato())
                .endereco(requestDTO.getEndereco())
                .descricao(requestDTO.getDescricao())
                .website(requestDTO.getWebsite())
                .build();
    }

    @Test
    void criarPetshop_ComSucesso() {
        when(petshopRepository.existsByCnpj(requestDTO.getCnpj())).thenReturn(false);
        when(petshopRepository.save(any(Petshop.class))).thenReturn(petshop);

        PetshopResponseDTO response = petshopService.criar(requestDTO, usuario);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(10L, usuario.getPetshopId());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void criarPetshop_CnpjDuplicado_DeveLancarExcecao() {
        when(petshopRepository.existsByCnpj(requestDTO.getCnpj())).thenReturn(true);

        assertThrows(ConflictException.class, () -> petshopService.criar(requestDTO, usuario));
        verify(petshopRepository, never()).save(any(Petshop.class));
    }

    @Test
    void buscarPorId_ComSucesso() {
        when(petshopRepository.findById(10L)).thenReturn(Optional.of(petshop));

        PetshopResponseDTO response = petshopService.buscarPorId(10L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
    }

    @Test
    void buscarPorId_Inexistente_DeveLancarExcecao() {
        when(petshopRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> petshopService.buscarPorId(99L));
    }

    @Test
    void atualizarPetshop_ComSucesso() {
        Role petshopRole = new Role();
        petshopRole.setName(ERole.ROLE_PETSHOP);
        usuario.setRoles(Set.of(petshopRole));
        usuario.setPetshopId(10L);

        when(petshopRepository.findById(10L)).thenReturn(Optional.of(petshop));
        when(petshopRepository.save(any(Petshop.class))).thenReturn(petshop);

        PetshopResponseDTO response = petshopService.atualizar(10L, requestDTO, usuario);

        assertNotNull(response);
        verify(petshopRepository, times(1)).save(any(Petshop.class));
    }

    @Test
    void atualizarPetshop_SemPermissao_DeveLancarExcecao() {
        Role petshopRole = new Role();
        petshopRole.setName(ERole.ROLE_PETSHOP);
        usuario.setRoles(Set.of(petshopRole));
        usuario.setPetshopId(99L); // outro petshop

        when(petshopRepository.findById(10L)).thenReturn(Optional.of(petshop));

        assertThrows(AuthorizationDeniedException.class, () -> petshopService.atualizar(10L, requestDTO, usuario));
    }

    @Test
    void deletarPetshop_ComSucesso() {
        when(petshopRepository.findById(10L)).thenReturn(Optional.of(petshop));
        when(usuarioRepository.findByPetshopId(10L)).thenReturn(Collections.singletonList(usuario));

        petshopService.deletar(10L);

        assertNull(usuario.getPetshopId());
        verify(usuarioRepository, times(1)).save(usuario);
        verify(petshopRepository, times(1)).delete(petshop);
    }

    // ── Testes de Fila de Aprovação ──────────────────────────────────────────────

    @Test
    void criarPetshop_DeveIniciarComStatusPendente() {
        Petshop petshopPendente = Petshop.builder()
                .id(10L)
                .nomeFantasia(requestDTO.getNomeFantasia())
                .statusAprovacao(StatusAprovacao.PENDENTE_APROVACAO)
                .build();
        when(petshopRepository.existsByCnpj(requestDTO.getCnpj())).thenReturn(false);
        when(petshopRepository.save(any(Petshop.class))).thenReturn(petshopPendente);

        PetshopResponseDTO response = petshopService.criar(requestDTO, usuario);

        assertNotNull(response);
        assertEquals(StatusAprovacao.PENDENTE_APROVACAO, response.getStatusAprovacao());
    }

    @Test
    void listarAprovados_RetornaApenasAprovados() {
        Petshop aprovado = Petshop.builder().id(10L).nomeFantasia("Aprovado").statusAprovacao(StatusAprovacao.APROVADO).build();
        when(petshopRepository.findByStatusAprovacao(StatusAprovacao.APROVADO)).thenReturn(List.of(aprovado));

        List<PetshopResponseDTO> lista = petshopService.listarAprovados();

        assertEquals(1, lista.size());
        assertEquals(StatusAprovacao.APROVADO, lista.get(0).getStatusAprovacao());
    }

    @Test
    void listarPendentes_ComoAdmin_RetornaPendentes() {
        Role adminRole = new Role();
        adminRole.setName(ERole.ROLE_ADMIN);
        usuario.setRoles(Set.of(adminRole));

        Petshop pendente = Petshop.builder().id(20L).nomeFantasia("Pendente").statusAprovacao(StatusAprovacao.PENDENTE_APROVACAO).build();
        when(petshopRepository.findByStatusAprovacao(StatusAprovacao.PENDENTE_APROVACAO)).thenReturn(List.of(pendente));

        List<PetshopResponseDTO> lista = petshopService.listarPendentes(usuario);

        assertEquals(1, lista.size());
        assertEquals(StatusAprovacao.PENDENTE_APROVACAO, lista.get(0).getStatusAprovacao());
    }

    @Test
    void listarPendentes_SemPermissaoAdmin_DeveLancarExcecao() {
        Role petshopRole = new Role();
        petshopRole.setName(ERole.ROLE_PETSHOP);
        usuario.setRoles(Set.of(petshopRole));

        assertThrows(AuthorizationDeniedException.class, () -> petshopService.listarPendentes(usuario));
        verify(petshopRepository, never()).findByStatusAprovacao(any());
    }

    @Test
    void alterarStatusAprovacao_ComoAdmin_Aprova_ComSucesso() {
        Role adminRole = new Role();
        adminRole.setName(ERole.ROLE_ADMIN);
        usuario.setRoles(Set.of(adminRole));

        Petshop petshopAprovado = Petshop.builder().id(10L).nomeFantasia("Petshop do Ed")
                .statusAprovacao(StatusAprovacao.APROVADO).build();
        when(petshopRepository.findById(10L)).thenReturn(Optional.of(petshop));
        when(petshopRepository.save(any(Petshop.class))).thenReturn(petshopAprovado);

        PetshopResponseDTO response = petshopService.alterarStatusAprovacao(10L, StatusAprovacao.APROVADO, usuario);

        assertEquals(StatusAprovacao.APROVADO, response.getStatusAprovacao());
        verify(petshopRepository, times(1)).save(petshop);
    }

    @Test
    void alterarStatusAprovacao_SemPermissaoAdmin_DeveLancarExcecao() {
        Role petshopRole = new Role();
        petshopRole.setName(ERole.ROLE_PETSHOP);
        usuario.setRoles(Set.of(petshopRole));

        assertThrows(AuthorizationDeniedException.class,
                () -> petshopService.alterarStatusAprovacao(10L, StatusAprovacao.APROVADO, usuario));
        verify(petshopRepository, never()).save(any());
    }

    @Test
    void alterarStatusAprovacao_TentarVoltarParaPendente_DeveLancarExcecao() {
        Role adminRole = new Role();
        adminRole.setName(ERole.ROLE_ADMIN);
        usuario.setRoles(Set.of(adminRole));

        assertThrows(IllegalArgumentException.class,
                () -> petshopService.alterarStatusAprovacao(10L, StatusAprovacao.PENDENTE_APROVACAO, usuario));
        verify(petshopRepository, never()).save(any());
    }
}
