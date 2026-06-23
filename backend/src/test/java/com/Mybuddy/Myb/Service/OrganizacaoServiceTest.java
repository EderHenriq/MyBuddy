package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.OrganizacaoRequestDTO;
import com.Mybuddy.Myb.DTO.OrganizacaoResponseDTO;
import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.Organizacao;
import com.Mybuddy.Myb.Repository.mongo.OrganizacaoRepository;
import com.Mybuddy.Myb.Repository.mongo.PetRepository;
import com.Mybuddy.Myb.Repository.mongo.EventoOngRepository;
import com.Mybuddy.Myb.Repository.jpa.CampanhaDoacaoRepository;
import com.Mybuddy.Myb.Repository.jpa.PaymentRepository;
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
class OrganizacaoServiceTest {

    @Mock
    private OrganizacaoRepository organizacaoRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private EventoOngRepository eventoOngRepository;

    @Mock
    private CampanhaDoacaoRepository campanhaDoacaoRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private OrganizacaoService organizacaoService;

    private Organizacao organizacao;
    private OrganizacaoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        organizacao = Organizacao.builder()
                .id(1L)
                .nomeFantasia("ONG Patinhas")
                .emailContato("patinhas@email.com")
                .cnpj("11.111.111/0001-11")
                .endereco("Rua A, 123")
                .telefoneContato("11999990001")
                .build();

        requestDTO = new OrganizacaoRequestDTO();
        requestDTO.setNomeFantasia("ONG Patinhas");
        requestDTO.setEmailContato("patinhas@email.com");
        requestDTO.setCnpj("11.111.111/0001-11");
        requestDTO.setEndereco("Rua A, 123");
        requestDTO.setTelefoneContato("11999990001");
    }

    // ===================== CRIAR (via entidade) =====================

    @Test
    void deveCriarOrganizacaoViaEntidadeComSucesso() {
        when(organizacaoRepository.existsByCnpj(organizacao.getCnpj())).thenReturn(false);
        when(organizacaoRepository.existsByEmailContato(organizacao.getEmailContato())).thenReturn(false);
        when(organizacaoRepository.save(any(Organizacao.class))).thenReturn(organizacao);

        Organizacao result = organizacaoService.criarOrganizacao(organizacao);

        assertThat(result).isNotNull();
        assertThat(result.getNomeFantasia()).isEqualTo("ONG Patinhas");
        verify(organizacaoRepository, times(1)).save(organizacao);
    }

    @Test
    void deveLancarExcecaoAoCriarOrganizacaoViaEntidadeComCnpjDuplicado() {
        when(organizacaoRepository.existsByCnpj(organizacao.getCnpj())).thenReturn(true);

        assertThatThrownBy(() -> organizacaoService.criarOrganizacao(organizacao))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("CNPJ já cadastrado");
    }

    @Test
    void deveLancarExcecaoAoCriarOrganizacaoViaEntidadeComEmailDuplicado() {
        when(organizacaoRepository.existsByCnpj(organizacao.getCnpj())).thenReturn(false);
        when(organizacaoRepository.existsByEmailContato(organizacao.getEmailContato())).thenReturn(true);

        assertThatThrownBy(() -> organizacaoService.criarOrganizacao(organizacao))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("E-mail de contato");
    }

    // ===================== CRIAR (via DTO) =====================

    @Test
    void deveCriarOrganizacaoViaDTOComSucesso() {
        when(organizacaoRepository.existsByCnpj(requestDTO.getCnpj())).thenReturn(false);
        when(organizacaoRepository.existsByEmailContato(requestDTO.getEmailContato())).thenReturn(false);
        when(organizacaoRepository.save(any(Organizacao.class))).thenReturn(organizacao);

        OrganizacaoResponseDTO result = organizacaoService.criarOrganizacao(requestDTO);

        assertThat(result).isNotNull();
        verify(organizacaoRepository, times(1)).save(any(Organizacao.class));
    }

    @Test
    void deveLancarExcecaoAoCriarOrganizacaoViaDTOComCnpjDuplicado() {
        when(organizacaoRepository.existsByCnpj(requestDTO.getCnpj())).thenReturn(true);

        assertThatThrownBy(() -> organizacaoService.criarOrganizacao(requestDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("CNPJ já cadastrado");
    }

    @Test
    void deveLancarExcecaoAoCriarOrganizacaoViaDTOComEmailDuplicado() {
        when(organizacaoRepository.existsByCnpj(requestDTO.getCnpj())).thenReturn(false);
        when(organizacaoRepository.existsByEmailContato(requestDTO.getEmailContato())).thenReturn(true);

        assertThatThrownBy(() -> organizacaoService.criarOrganizacao(requestDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("E-mail de contato");
    }

    // ===================== BUSCAR =====================

    @Test
    void deveBuscarOrganizacaoPorIdComSucesso() {
        when(organizacaoRepository.findById(1L)).thenReturn(Optional.of(organizacao));

        OrganizacaoResponseDTO result = organizacaoService.buscarOrganizacaoPorId(1L);

        assertThat(result).isNotNull();
    }

    @Test
    void deveLancarExcecaoAoBuscarOrganizacaoPorIdInexistente() {
        when(organizacaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> organizacaoService.buscarOrganizacaoPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Organização não encontrada com ID: 99");
    }

    @Test
    void deveBuscarOrganizacaoPorCnpj() {
        when(organizacaoRepository.findByCnpj("11.111.111/0001-11")).thenReturn(Optional.of(organizacao));

        Optional<Organizacao> result = organizacaoService.buscarOrganizacaoPorCnpj("11.111.111/0001-11");

        assertThat(result).isPresent();
    }

    @Test
    void deveRetornarVazioAoBuscarOrganizacaoPorCnpjInexistente() {
        when(organizacaoRepository.findByCnpj("00.000.000/0000-00")).thenReturn(Optional.empty());

        Optional<Organizacao> result = organizacaoService.buscarOrganizacaoPorCnpj("00.000.000/0000-00");

        assertThat(result).isEmpty();
    }

    @Test
    void deveVerificarExistenciaOrganizacaoPorCnpj() {
        when(organizacaoRepository.existsByCnpj("11.111.111/0001-11")).thenReturn(true);

        boolean existe = organizacaoService.existeOrganizacaoPorCnpj("11.111.111/0001-11");

        assertThat(existe).isTrue();
    }

    @Test
    void deveListarTodasOrganizacoes() {
        when(organizacaoRepository.findAll()).thenReturn(List.of(organizacao));

        List<OrganizacaoResponseDTO> result = organizacaoService.listarTodasOrganizacoes();

        assertThat(result).hasSize(1);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverOrganizacoes() {
        when(organizacaoRepository.findAll()).thenReturn(List.of());

        List<OrganizacaoResponseDTO> result = organizacaoService.listarTodasOrganizacoes();

        assertThat(result).isEmpty();
    }

    // ===================== ATUALIZAR =====================

    @Test
    void deveAtualizarOrganizacaoComSucesso() {
        when(organizacaoRepository.findById(1L)).thenReturn(Optional.of(organizacao));
        when(organizacaoRepository.findByCnpj(requestDTO.getCnpj())).thenReturn(Optional.of(organizacao));
        when(organizacaoRepository.findByEmailContato(requestDTO.getEmailContato())).thenReturn(Optional.of(organizacao));
        when(organizacaoRepository.save(any(Organizacao.class))).thenReturn(organizacao);

        OrganizacaoResponseDTO result = organizacaoService.atualizarOrganizacao(1L, requestDTO);

        assertThat(result).isNotNull();
        verify(organizacaoRepository, times(1)).save(any(Organizacao.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarOrganizacaoInexistente() {
        when(organizacaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> organizacaoService.atualizarOrganizacao(99L, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Organização não encontrada para atualização com ID: 99");
    }

    @Test
    void deveLancarExcecaoAoAtualizarComCnpjDeOutraOrganizacao() {
        Organizacao outraOrganizacao = Organizacao.builder().id(2L).build();

        when(organizacaoRepository.findById(1L)).thenReturn(Optional.of(organizacao));
        when(organizacaoRepository.findByCnpj(requestDTO.getCnpj())).thenReturn(Optional.of(outraOrganizacao));

        assertThatThrownBy(() -> organizacaoService.atualizarOrganizacao(1L, requestDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("CNPJ já cadastrado para outra organização");
    }

    @Test
    void deveLancarExcecaoAoAtualizarComEmailDeOutraOrganizacao() {
        Organizacao outraOrganizacao = Organizacao.builder().id(2L).build();

        when(organizacaoRepository.findById(1L)).thenReturn(Optional.of(organizacao));
        when(organizacaoRepository.findByCnpj(requestDTO.getCnpj())).thenReturn(Optional.empty());
        when(organizacaoRepository.findByEmailContato(requestDTO.getEmailContato())).thenReturn(Optional.of(outraOrganizacao));

        assertThatThrownBy(() -> organizacaoService.atualizarOrganizacao(1L, requestDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("E-mail de contato da organização já cadastrado para outra organização");
    }

    // ===================== DELETAR =====================

    @Test
    void deveDeletarOrganizacaoComSucesso() {
        when(organizacaoRepository.existsById(1L)).thenReturn(true);
        when(petRepository.findByOrganizacaoId(1L)).thenReturn(List.of());
        when(eventoOngRepository.findByOrganizacaoId(1L)).thenReturn(List.of());
        when(campanhaDoacaoRepository.existsByOrganizacaoId(1L)).thenReturn(false);

        organizacaoService.deletarOrganizacao(1L);

        verify(paymentRepository, times(1)).nullifyOrganizacaoId(1L);
        verify(organizacaoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarOrganizacaoInexistente() {
        when(organizacaoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> organizacaoService.deletarOrganizacao(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Organização não encontrada para deleção com ID: 99");
    }

    @Test
    void deveLancarExcecaoAoDeletarOrganizacaoComPetsVinculados() {
        when(organizacaoRepository.existsById(1L)).thenReturn(true);
        when(petRepository.findByOrganizacaoId(1L)).thenReturn(List.of(new com.Mybuddy.Myb.Model.Pet()));

        assertThatThrownBy(() -> organizacaoService.deletarOrganizacao(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Não é possível deletar a organização pois existem pets vinculados");
    }

    @Test
    void deveLancarExcecaoAoDeletarOrganizacaoComEventosVinculados() {
        when(organizacaoRepository.existsById(1L)).thenReturn(true);
        when(petRepository.findByOrganizacaoId(1L)).thenReturn(List.of());
        when(eventoOngRepository.findByOrganizacaoId(1L)).thenReturn(List.of(new com.Mybuddy.Myb.Model.EventoOng()));

        assertThatThrownBy(() -> organizacaoService.deletarOrganizacao(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Não é possível deletar a organização pois existem eventos vinculados");
    }

    @Test
    void deveLancarExcecaoAoDeletarOrganizacaoComCampanhasVinculadas() {
        when(organizacaoRepository.existsById(1L)).thenReturn(true);
        when(petRepository.findByOrganizacaoId(1L)).thenReturn(List.of());
        when(eventoOngRepository.findByOrganizacaoId(1L)).thenReturn(List.of());
        when(campanhaDoacaoRepository.existsByOrganizacaoId(1L)).thenReturn(true);

        assertThatThrownBy(() -> organizacaoService.deletarOrganizacao(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Não é possível deletar a organização pois existem campanhas de doação vinculadas");
    }
}