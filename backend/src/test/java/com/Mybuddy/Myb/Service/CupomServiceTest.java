package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.CupomRequestDTO;
import com.Mybuddy.Myb.DTO.CupomResponseDTO;
import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.Cupom;
import com.Mybuddy.Myb.Model.CupomUsuario;
import com.Mybuddy.Myb.Model.Petshop;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.jpa.CupomRepository;
import com.Mybuddy.Myb.Repository.jpa.CupomUsuarioRepository;
import com.Mybuddy.Myb.Repository.jpa.PetshopRepository;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authorization.AuthorizationDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CupomServiceTest {

    @Mock
    private CupomRepository cupomRepository;

    @Mock
    private CupomUsuarioRepository cupomUsuarioRepository;

    @Mock
    private PetshopRepository petshopRepository;

    @InjectMocks
    private CupomService cupomService;

    private Usuario adminUsuario;
    private Usuario petshopUsuario;
    private Usuario adotanteUsuario;
    private Petshop petshop;
    private CupomRequestDTO requestDTO;
    private Cupom cupom;

    @BeforeEach
    void setUp() {
        Role roleAdmin = new Role(ERole.ROLE_ADMIN);
        Role rolePetshop = new Role(ERole.ROLE_PETSHOP);
        Role roleAdotante = new Role(ERole.ROLE_ADOTANTE);

        adminUsuario = new Usuario();
        adminUsuario.setId(101L);
        adminUsuario.setRoles(Set.of(roleAdmin));

        petshopUsuario = new Usuario();
        petshopUsuario.setId(102L);
        petshopUsuario.setPetshopId(200L);
        petshopUsuario.setRoles(Set.of(rolePetshop));

        adotanteUsuario = new Usuario();
        adotanteUsuario.setId(103L);
        adotanteUsuario.setRoles(Set.of(roleAdotante));

        petshop = Petshop.builder()
                .id(200L)
                .nomeFantasia("Petshop Teste")
                .build();

        requestDTO = new CupomRequestDTO(
                "DESCONTO10", new BigDecimal("10.00"), 200L, true,
                null, null, null, null);

        cupom = Cupom.builder()
                .id(1L)
                .codigo("DESCONTO10")
                .percentualDesconto(new BigDecimal("10.00"))
                .petshop(petshop)
                .ativo(true)
                .build();
    }

    // ── Testes de Criação ────────────────────────────────────────────────────────

    @Test
    void criarCupom_ComoAdmin_ComSucesso() {
        when(cupomRepository.findByCodigo("DESCONTO10")).thenReturn(Optional.empty());
        when(petshopRepository.findById(200L)).thenReturn(Optional.of(petshop));
        when(cupomRepository.save(any(Cupom.class))).thenReturn(cupom);

        CupomResponseDTO response = cupomService.criar(requestDTO, adminUsuario);

        assertNotNull(response);
        assertEquals("DESCONTO10", response.getCodigo());
        assertEquals(200L, response.getPetshopId());
        verify(cupomRepository, times(1)).save(any(Cupom.class));
    }

    @Test
    void criarCupom_ComoPetshop_ComSucesso_ForcaProprioPetshop() {
        requestDTO.setPetshopId(999L); // Tenta colocar ID de outro petshop — deve ser ignorado
        when(cupomRepository.findByCodigo("DESCONTO10")).thenReturn(Optional.empty());
        when(petshopRepository.findById(200L)).thenReturn(Optional.of(petshop)); // Força ID 200 do petshopUsuario
        when(cupomRepository.save(any(Cupom.class))).thenReturn(cupom);

        CupomResponseDTO response = cupomService.criar(requestDTO, petshopUsuario);

        assertNotNull(response);
        assertEquals(200L, response.getPetshopId());
        verify(cupomRepository, times(1)).save(any(Cupom.class));
    }

    @Test
    void criarCupom_UsuarioSemPermissao_DeveLancarExcecao() {
        assertThrows(AuthorizationDeniedException.class, () -> cupomService.criar(requestDTO, adotanteUsuario));
        verify(cupomRepository, never()).save(any(Cupom.class));
    }

    @Test
    void criarCupom_CodigoExistente_DeveLancarExcecao() {
        when(cupomRepository.findByCodigo("DESCONTO10")).thenReturn(Optional.of(cupom));

        assertThrows(ConflictException.class, () -> cupomService.criar(requestDTO, adminUsuario));
        verify(cupomRepository, never()).save(any(Cupom.class));
    }

    @Test
    void criarCupom_DataExpiracaoAntesDaDataInicio_DeveLancarExcecao() {
        requestDTO.setDataInicio(LocalDate.now().plusDays(5));
        requestDTO.setDataExpiracao(LocalDate.now().plusDays(1));
        when(cupomRepository.findByCodigo("DESCONTO10")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> cupomService.criar(requestDTO, adminUsuario));
        verify(cupomRepository, never()).save(any(Cupom.class));
    }

    // ── Testes de Listagem ───────────────────────────────────────────────────────

    @Test
    void listarCupons_ComoAdmin_RetornaTodos() {
        when(cupomRepository.findAll()).thenReturn(List.of(cupom));

        List<CupomResponseDTO> response = cupomService.listar(adminUsuario);

        assertEquals(1, response.size());
        assertEquals("DESCONTO10", response.get(0).getCodigo());
    }

    @Test
    void listarCupons_ComoAdotante_RetornaApenasGlobaisAtivos() {
        Cupom globalCupom = Cupom.builder()
                .id(2L)
                .codigo("GLOBAL5")
                .percentualDesconto(new BigDecimal("5.00"))
                .petshop(null)
                .ativo(true)
                .build();

        when(cupomRepository.findByPetshopIsNullAndAtivoTrue()).thenReturn(List.of(globalCupom));

        List<CupomResponseDTO> response = cupomService.listar(adotanteUsuario);

        assertEquals(1, response.size());
        assertEquals("GLOBAL5", response.get(0).getCodigo());
        assertNull(response.get(0).getPetshopId());
    }

    // ── Testes de Alteração de Status ────────────────────────────────────────────

    @Test
    void alterarStatus_SemPermissao_DeveLancarExcecao() {
        when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupom));

        assertThrows(AuthorizationDeniedException.class, () -> cupomService.alterarStatus(1L, false, adotanteUsuario));
        verify(cupomRepository, never()).save(any(Cupom.class));
    }

    @Test
    void alterarStatus_ComoAdmin_ComSucesso() {
        Cupom cupomDesativado = Cupom.builder()
                .id(1L)
                .codigo("DESCONTO10")
                .percentualDesconto(new BigDecimal("10.00"))
                .petshop(petshop)
                .ativo(false)
                .build();
        when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupom));
        when(cupomRepository.save(any(Cupom.class))).thenReturn(cupomDesativado);

        CupomResponseDTO response = cupomService.alterarStatus(1L, false, adminUsuario);

        assertNotNull(response);
        assertFalse(response.isAtivo());
        verify(cupomRepository, times(1)).save(cupom);
    }

    // ── Testes de Validação Anti-Abuso ───────────────────────────────────────────

    @Test
    void buscarPorCodigoValido_Compativel_RetornaCupom() {
        when(cupomRepository.findByCodigoAndAtivoTrue("DESCONTO10")).thenReturn(Optional.of(cupom));
        when(cupomUsuarioRepository.existsByCupomIdAndUsuarioId(anyLong(), anyLong())).thenReturn(false);

        CupomResponseDTO response = cupomService.buscarPorCodigoValido("desconto10 ", 200L, 103L, new BigDecimal("100.00"));

        assertNotNull(response);
        assertEquals("DESCONTO10", response.getCodigo());
    }

    @Test
    void buscarPorCodigoValido_DiferentePetshop_DeveLancarExcecao() {
        when(cupomRepository.findByCodigoAndAtivoTrue("DESCONTO10")).thenReturn(Optional.of(cupom));
        when(cupomUsuarioRepository.existsByCupomIdAndUsuarioId(anyLong(), anyLong())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> cupomService.buscarPorCodigoValido("DESCONTO10", 999L, 103L, new BigDecimal("100.00")));
    }

    @Test
    void buscarPorCodigoValido_CupomExpirado_DeveLancarExcecao() {
        Cupom cupomExpirado = Cupom.builder()
                .id(2L)
                .codigo("EXPIRADO")
                .percentualDesconto(new BigDecimal("15.00"))
                .ativo(true)
                .dataExpiracao(LocalDate.now().minusDays(1)) // Expirado ontem
                .build();
        when(cupomRepository.findByCodigoAndAtivoTrue("EXPIRADO")).thenReturn(Optional.of(cupomExpirado));

        assertThrows(IllegalArgumentException.class,
                () -> cupomService.buscarPorCodigoValido("EXPIRADO", null, 103L, new BigDecimal("100.00")));
    }

    @Test
    void buscarPorCodigoValido_LimitEsgotado_DeveLancarExcecao() {
        Cupom cupomEsgotado = Cupom.builder()
                .id(3L)
                .codigo("ESGOTADO")
                .percentualDesconto(new BigDecimal("20.00"))
                .ativo(true)
                .limiteUsoGeral(10)
                .usoAtual(10) // Atingiu o limite
                .build();
        when(cupomRepository.findByCodigoAndAtivoTrue("ESGOTADO")).thenReturn(Optional.of(cupomEsgotado));

        assertThrows(IllegalArgumentException.class,
                () -> cupomService.buscarPorCodigoValido("ESGOTADO", null, 103L, new BigDecimal("100.00")));
    }

    @Test
    void buscarPorCodigoValido_UsuarioJaUsou_DeveLancarConflictException() {
        when(cupomRepository.findByCodigoAndAtivoTrue("DESCONTO10")).thenReturn(Optional.of(cupom));
        // Usuário 103 já usou o cupom 1
        when(cupomUsuarioRepository.existsByCupomIdAndUsuarioId(1L, 103L)).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> cupomService.buscarPorCodigoValido("DESCONTO10", 200L, 103L, new BigDecimal("100.00")));
    }

    @Test
    void buscarPorCodigoValido_ValorPedidoAbaixoDoMinimo_DeveLancarExcecao() {
        Cupom cupomComMinimo = Cupom.builder()
                .id(4L)
                .codigo("MINIMO100")
                .percentualDesconto(new BigDecimal("10.00"))
                .ativo(true)
                .valorMinimoPedido(new BigDecimal("100.00"))
                .petshop(null) // Global
                .build();
        when(cupomRepository.findByCodigoAndAtivoTrue("MINIMO100")).thenReturn(Optional.of(cupomComMinimo));
        when(cupomUsuarioRepository.existsByCupomIdAndUsuarioId(anyLong(), anyLong())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> cupomService.buscarPorCodigoValido("MINIMO100", null, 103L, new BigDecimal("50.00")));
    }

    @Test
    void registrarUso_ComSucesso_IncrementaContadorERegistraUsuario() {
        when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupom));
        when(cupomUsuarioRepository.existsByCupomIdAndUsuarioId(1L, 103L)).thenReturn(false);
        when(cupomRepository.save(any(Cupom.class))).thenReturn(cupom);
        when(cupomUsuarioRepository.save(any(CupomUsuario.class))).thenReturn(new CupomUsuario());

        cupomService.registrarUso(1L, 103L, 500L);

        verify(cupomRepository, times(1)).save(cupom);
        verify(cupomUsuarioRepository, times(1)).save(any(CupomUsuario.class));
        assertEquals(1, cupom.getUsoAtual());
    }

    @Test
    void registrarUso_CupomEsgotado_DeveLancarConflictException() {
        Cupom cupomEsgotado = Cupom.builder()
                .id(5L)
                .codigo("ESGOTADO")
                .ativo(true)
                .limiteUsoGeral(1)
                .usoAtual(1)
                .build();
        when(cupomRepository.findById(5L)).thenReturn(Optional.of(cupomEsgotado));

        assertThrows(ConflictException.class, () -> cupomService.registrarUso(5L, 103L, 501L));
        verify(cupomUsuarioRepository, never()).save(any(CupomUsuario.class));
    }
}
