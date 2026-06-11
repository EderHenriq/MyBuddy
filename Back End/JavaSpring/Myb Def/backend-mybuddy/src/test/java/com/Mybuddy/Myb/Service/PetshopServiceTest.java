package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.PetshopRequestDTO;
import com.Mybuddy.Myb.DTO.PetshopResponseDTO;
import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.Petshop;
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
        requestDTO = new PetshopRequestDTO("Petshop do Ed", "ed@petshop.com", "12345678901234", "4499999999", "Av Principal", "Descrição legal", "www.edpet.com");
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
}
