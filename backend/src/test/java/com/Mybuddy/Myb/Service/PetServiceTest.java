package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.PetRequestDTO;
import com.Mybuddy.Myb.DTO.PetResponse;
import com.Mybuddy.Myb.Model.Organizacao;
import com.Mybuddy.Myb.Model.Pet;
import com.Mybuddy.Myb.Model.StatusAdocao;
import com.Mybuddy.Myb.Repository.mongo.InteresseAdocaoRepository;
import com.Mybuddy.Myb.Repository.mongo.OrganizacaoRepository;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Repository.mongo.PetRepository;
import com.Mybuddy.Myb.Repository.jpa.AgendamentoRepository;
import com.Mybuddy.Myb.Repository.jpa.PaymentRepository;
import com.Mybuddy.Myb.Repository.jpa.CampanhaDoacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private InteresseAdocaoRepository interesseRepo;

    @Mock
    private OrganizacaoRepository organizacaoRepository;

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private PaymentRepository paymentRepository;
    
    @Mock
    private CampanhaDoacaoRepository campanhaDoacaoRepository;

    @InjectMocks
    private PetService petService;

    private Organizacao organizacao;
    private Pet pet;
    private PetRequestDTO petRequestDTO;

    @BeforeEach
    void setUp() {
        organizacao = new Organizacao();
        organizacao.setId(1L);
        organizacao.setNomeFantasia("ONG Teste");

        pet = new Pet();
        pet.setId(1L);
        pet.setNome("Rex");
        pet.setOrganizacao(organizacao);
        pet.setStatusAdocao(StatusAdocao.DISPONIVEL);

        petRequestDTO = new PetRequestDTO();
        petRequestDTO.setNome("Rex");
        petRequestDTO.setOrganizacaoId(1L);
        petRequestDTO.setStatusAdocao(StatusAdocao.DISPONIVEL);
    }

    // ===================== CRIAR PET =====================

    @Test
    void deveCriarPetComSucesso() {
        when(organizacaoRepository.findById(1L)).thenReturn(Optional.of(organizacao));
        when(petRepository.save(any(Pet.class))).thenReturn(pet);

        PetResponse response = petService.criarPet(petRequestDTO);

        assertThat(response).isNotNull();
        assertThat(response.nome()).isEqualTo("Rex");
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    void deveLancarExcecaoAoCriarPetSemOrganizacaoId() {
        petRequestDTO.setOrganizacaoId(null);

        assertThatThrownBy(() -> petService.criarPet(petRequestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID da organização é obrigatório");
    }

    @Test
    void deveLancarExcecaoAoCriarPetComOrganizacaoInexistente() {
        when(organizacaoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.criarPet(petRequestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Organização não encontrada");
    }

    @Test
    void deveCriarPetComStatusDisponivelPorPadraoQuandoStatusNulo() {
        petRequestDTO.setStatusAdocao(null);

        when(organizacaoRepository.findById(1L)).thenReturn(Optional.of(organizacao));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> {
            Pet p = invocation.getArgument(0);
            p.setId(1L);
            p.setOrganizacao(organizacao);
            return p;
        });

        PetResponse response = petService.criarPet(petRequestDTO);

        assertThat(response.statusAdocao()).isEqualTo(StatusAdocao.DISPONIVEL);
    }

    // ===================== BUSCAR PETS =====================

    @Test
    void deveBuscarTodosPets() {
        when(petRepository.findAll()).thenReturn(List.of(pet));

        List<Pet> pets = petService.buscarTodosPets();

        assertThat(pets).hasSize(1);
        assertThat(pets.get(0).getNome()).isEqualTo("Rex");
    }

    @Test
    void deveBuscarTodosPetsRetornarListaVaziaQuandoNaoHouverPets() {
        when(petRepository.findAll()).thenReturn(List.of());

        List<Pet> pets = petService.buscarTodosPets();

        assertThat(pets).isEmpty();
    }

    @Test
    void deveBuscarPetPorIdComSucesso() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        Optional<Pet> resultado = petService.buscarPetPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
    }

    @Test
    void deveBuscarPetPorIdRetornarVazioQuandoNaoEncontrado() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Pet> resultado = petService.buscarPetPorId(99L);

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveBuscarPetPorIdDTOComSucesso() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        Optional<PetResponse> resultado = petService.buscarPetPorIdDTO(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().nome()).isEqualTo("Rex");
    }

    @Test
    void deveBuscarPetsPorOrganizacaoId() {
        when(petRepository.findByOrganizacaoId(1L)).thenReturn(List.of(pet));

        List<PetResponse> pets = petService.buscarPetsPorOrganizacaoId(1L);

        assertThat(pets).hasSize(1);
        verify(petRepository, times(1)).findByOrganizacaoId(1L);
    }

    // ===================== ATUALIZAR PET =====================

    @Test
    void deveAtualizarPetComSucesso() {
        petRequestDTO.setNome("Max");

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenReturn(pet);

        PetResponse response = petService.atualizarPet(1L, petRequestDTO);

        assertThat(response).isNotNull();
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarPetInexistente() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.atualizarPet(99L, petRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pet com ID 99 não encontrado");
    }

    @Test
    void deveLancarExcecaoAoAtualizarPetSemOrganizacaoId() {
        petRequestDTO.setOrganizacaoId(null);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        assertThatThrownBy(() -> petService.atualizarPet(1L, petRequestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID da organização é obrigatório");
    }

    @Test
    void deveLancarExcecaoAoAtualizarPetComNovaOrganizacaoInexistente() {
        petRequestDTO.setOrganizacaoId(99L);

        Pet petSemOrg = new Pet();
        petSemOrg.setId(1L);
        petSemOrg.setOrganizacao(null);

        when(petRepository.findById(1L)).thenReturn(Optional.of(petSemOrg));
        when(organizacaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.atualizarPet(1L, petRequestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nova organização não encontrada");
    }

    // ===================== DELETAR PET =====================

    @Test
    void deveDeletarPetComSucesso() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(interesseRepo.countByPetId(1L)).thenReturn(0L);
        when(agendamentoRepository.existsByPetIdAndStatusNot(1L, com.Mybuddy.Myb.Model.StatusAgendamento.CANCELADO)).thenReturn(false);

        petService.deletarPet(1L);

        verify(paymentRepository, times(1)).nullifyPetId(1L);
        verify(campanhaDoacaoRepository, times(1)).nullifyPetId(1L);
        verify(petRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarPetInexistente() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.deletarPet(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pet com ID 99 não encontrado");
    }

    @Test
    void deveLancarExcecaoAoDeletarPetAdotado() {
        pet.setStatusAdocao(StatusAdocao.ADOTADO);
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        assertThatThrownBy(() -> petService.deletarPet(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Não é possível excluir pet já adotado");
    }

    @Test
    void deveLancarExcecaoAoDeletarPetComInteressesRegistrados() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(interesseRepo.countByPetId(1L)).thenReturn(3L);

        assertThatThrownBy(() -> petService.deletarPet(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("interesses registrados");
    }

    @Test
    void deveLancarExcecaoAoDeletarPetComAgendamentosAtivos() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(interesseRepo.countByPetId(1L)).thenReturn(0L);
        when(agendamentoRepository.existsByPetIdAndStatusNot(1L, com.Mybuddy.Myb.Model.StatusAgendamento.CANCELADO)).thenReturn(true);

        assertThatThrownBy(() -> petService.deletarPet(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Não é possível excluir o pet pois existem agendamentos ativos ou concluídos");
    }
}