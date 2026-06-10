package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Payload.Request.SignupRequest;
import com.Mybuddy.Myb.Repository.mongo.RoleRepository;
import com.Mybuddy.Myb.Repository.mongo.UsuarioRepository;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private OrganizacaoService organizacaoService;

    @InjectMocks
    private AuthService authService;

    private SignupRequest signupRequest;
    private Role roleAdotante;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest();
        signupRequest.setNome("Eder");
        signupRequest.setEmail("eder@mybuddy.com");
        signupRequest.setTelefone("44999999999");
        signupRequest.setPassword("senha123");
        signupRequest.setRoles(Set.of("ADOTANTE"));

        roleAdotante = new Role();
        roleAdotante.setName(ERole.ROLE_ADOTANTE);
    }

    @Test
    void deveRegistrarAdotanteComSucesso() {
        when(usuarioRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByTelefone(signupRequest.getTelefone())).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_ADOTANTE)).thenReturn(Optional.of(roleAdotante));
        when(encoder.encode(any())).thenReturn("senhaCriptografada");

        authService.registerUser(signupRequest);

        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExiste() {
        when(usuarioRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.registerUser(signupRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Erro: O e-mail já está em uso!");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoTelefoneJaExiste() {
        when(usuarioRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByTelefone(signupRequest.getTelefone())).thenReturn(true);

        assertThatThrownBy(() -> authService.registerUser(signupRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Erro: O telefone já está em uso!");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoRoleOngSemCnpj() {
        signupRequest.setRoles(Set.of("ONG"));
        signupRequest.setNome("ONG Teste");

        Role roleOng = new Role();
        roleOng.setName(ERole.ROLE_ONG);

        when(usuarioRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByTelefone(signupRequest.getTelefone())).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_ONG)).thenReturn(Optional.of(roleOng));

        assertThatThrownBy(() -> authService.registerUser(signupRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("O CNPJ da organização é obrigatório para a role ONG.");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveFazerCleanupDaOrganizacaoQuandoSalvarUsuarioFalha() {
        signupRequest.setRoles(Set.of("ONG"));
        signupRequest.setOrganizacaoCnpj("12.345.678/0001-90");
        signupRequest.setOrganizacaoNomeFantasia("ONG Teste");
        signupRequest.setOrganizacaoEmailContato("ong@teste.com");
        signupRequest.setOrganizacaoEndereco("Rua das ONGs");

        Role roleOng = new Role();
        roleOng.setName(ERole.ROLE_ONG);

        com.Mybuddy.Myb.Model.Organizacao orgCriada = new com.Mybuddy.Myb.Model.Organizacao();
        orgCriada.setId(123L);
        orgCriada.setCnpj("12.345.678/0001-90");

        when(usuarioRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByTelefone(signupRequest.getTelefone())).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_ONG)).thenReturn(Optional.of(roleOng));
        when(organizacaoService.existeOrganizacaoPorCnpj(any(String.class))).thenReturn(false);
        when(organizacaoService.criarOrganizacao(any(com.Mybuddy.Myb.Model.Organizacao.class))).thenReturn(orgCriada);
        when(encoder.encode(any())).thenReturn("senhaCriptografada");
        
        when(usuarioRepository.save(any())).thenThrow(new RuntimeException("Database error saving user"));

        assertThatThrownBy(() -> authService.registerUser(signupRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error saving user");

        verify(organizacaoService, times(1)).deletarOrganizacao(123L);
    }
}
