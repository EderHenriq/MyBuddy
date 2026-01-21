package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.PetRequestDTO;
import com.Mybuddy.Myb.DTO.PetResponse;
import com.Mybuddy.Myb.Model.*;
import com.Mybuddy.Myb.Repository.FotoPetRepository;
import com.Mybuddy.Myb.Repository.InteresseAdoacaoRepository;
import com.Mybuddy.Myb.Repository.OrganizacaoRepository;
import com.Mybuddy.Myb.Repository.PetRepository;
import com.Mybuddy.Myb.Security.jwt.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - PetService")
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private OrganizacaoRepository organizacaoRepository;

    @Mock
    private InteresseAdoacaoRepository interesseRepo;

    @Mock
    private FotoPetRepository fotoPetRepository;

    @InjectMocks
    private PetService petService;

    private Organizacao organizacao;
    private PetRequestDTO petRequestDTO;

    @BeforeEach
    void setUp() {
        organizacao = new Organizacao();
        organizacao.setId(1L);
        organizacao.setNomeFantasia("ONG Amigos dos Animais");

        petRequestDTO = new PetRequestDTO();
        petRequestDTO.setNome("Rex");
        petRequestDTO.setEspecie(Especie.CAO);
        petRequestDTO.setRaca("Labrador");
        petRequestDTO.setIdade(3);
        petRequestDTO.setPorte(Porte.MEDIO);
        petRequestDTO.setCor("Dourado");
        petRequestDTO.setPelagem("Curto");
        petRequestDTO.setSexo("Macho");
        petRequestDTO.setMicrochipado(true);
        petRequestDTO.setVacinado(true);
        petRequestDTO.setCastrado(true);
        petRequestDTO.setCidade("São Paulo");
        petRequestDTO.setEstado("SP");
        petRequestDTO.setOrganizacaoId(1L);
    }

    @Test
    @DisplayName("Deve criar pet com dados válidos")
    void criarPet_ComDadosValidos_RetornaPetResponse() {
        // Arrange
        when(organizacaoRepository.findById(1L)).thenReturn(Optional.of(organizacao));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> {
            Pet pet = invocation.getArgument(0);
            pet.setId(1L);
            return pet;
        });

        // Act
        PetResponse response = petService.criarPet(petRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("Rex", response.nome());
        assertEquals("CAO", response.especie());
        assertEquals("Labrador", response.raca());
        verify(organizacaoRepository).findById(1L);
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando organização não existe")
    void criarPet_OrganizacaoInexistente_LancaExcecao() {
        // Arrange
        when(organizacaoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> petService.criarPet(petRequestDTO));

        assertTrue(exception.getMessage().contains("Organização não encontrada"));
        verify(organizacaoRepository).findById(1L);
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando organizacaoId é nulo")
    void criarPet_SemOrganizacaoId_LancaExcecao() {
        // Arrange
        petRequestDTO.setOrganizacaoId(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> petService.criarPet(petRequestDTO));

        assertTrue(exception.getMessage().contains("ID da organização é obrigatório"));
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    @DisplayName("Deve atualizar pet existente com sucesso")
    void atualizarPet_PetExistente_RetornaDadosAtualizados() {
        // Arrange
        Pet petExistente = new Pet();
        petExistente.setId(1L);
        petExistente.setNome("Rex Antigo");
        petExistente.setOrganizacao(organizacao);

        PetRequestDTO updateDTO = new PetRequestDTO();
        updateDTO.setNome("Rex Atualizado");
        updateDTO.setOrganizacaoId(1L);

        when(petRepository.findById(1L)).thenReturn(Optional.of(petExistente));
        when(petRepository.save(any(Pet.class))).thenReturn(petExistente);

        // Act
        PetResponse response = petService.atualizarPet(1L, updateDTO);

        // Assert
        assertNotNull(response);
        assertEquals("Rex Atualizado", response.nome());
        verify(petRepository).findById(1L);
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar pet inexistente")
    void atualizarPet_PetInexistente_LancaExcecao() {
        // Arrange
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> petService.atualizarPet(1L, petRequestDTO));

        assertTrue(exception.getMessage().contains("não encontrado"));
        verify(petRepository).findById(1L);
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    @DisplayName("Deve deletar pet disponível sem interesses")
    void deletarPet_PetDisponivelSemInteresses_DeletaComSucesso() {
        // Arrange
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setStatusAdocao(StatusAdocao.DISPONIVEL);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(interesseRepo.countByPetId(1L)).thenReturn(0L);

        // Act
        assertDoesNotThrow(() -> petService.deletarPet(1L));

        // Assert
        verify(petRepository).findById(1L);
        verify(interesseRepo).countByPetId(1L);
        verify(petRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar pet adotado")
    void deletarPet_PetAdotado_LancaExcecao() {
        // Arrange
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setStatusAdocao(StatusAdocao.ADOTADO);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> petService.deletarPet(1L));

        assertTrue(exception.getMessage().contains("já adotado"));
        verify(petRepository).findById(1L);
        verify(petRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar pet com interesses")
    void deletarPet_PetComInteresses_LancaExcecao() {
        // Arrange
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setStatusAdocao(StatusAdocao.DISPONIVEL);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(interesseRepo.countByPetId(1L)).thenReturn(3L);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> petService.deletarPet(1L));

        assertTrue(exception.getMessage().contains("interesses registrados"));
        verify(petRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve retornar true quando usuário é ADMIN")
    void isPetOwnedByCurrentUser_UsuarioAdmin_RetornaTrue() {
        // Arrange
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setOrganizacao(organizacao);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(new UserDetailsImpl(1L, "admin@test.com", "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
        when(authentication.getAuthorities()).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = petService.isPetOwnedByCurrentUser(1L, null);

        // Assert
        assertTrue(result);
        verify(petRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar true quando ONG é dona do pet")
    void isPetOwnedByCurrentUser_OngDonaPet_RetornaTrue() {
        // Arrange
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setOrganizacao(organizacao);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(new UserDetailsImpl(1L, "ong@test.com", "password", List.of(new SimpleGrantedAuthority("ROLE_ONG"))));
        when(authentication.getAuthorities()).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_ONG")));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = petService.isPetOwnedByCurrentUser(1L, 1L);

        // Assert
        assertTrue(result);
        verify(petRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar false quando ONG não é dona do pet")
    void isPetOwnedByCurrentUser_OngNaoDona_RetornaFalse() {
        // Arrange
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setOrganizacao(organizacao);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(new UserDetailsImpl(2L, "ong2@test.com", "password", List.of(new SimpleGrantedAuthority("ROLE_ONG"))));
        when(authentication.getAuthorities()).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_ONG")));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = petService.isPetOwnedByCurrentUser(1L, 2L);

        // Assert
        assertFalse(result);
        verify(petRepository).findById(1L);
    }
}
