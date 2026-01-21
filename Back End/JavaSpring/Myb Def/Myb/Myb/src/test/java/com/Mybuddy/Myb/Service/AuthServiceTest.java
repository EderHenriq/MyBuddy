package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Model.Organizacao;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Payload.Request.LoginRequest;
import com.Mybuddy.Myb.Payload.Request.SignupRequest;
import com.Mybuddy.Myb.Repository.RoleRepository;
import com.Mybuddy.Myb.Repository.UsuarioRepository;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import com.Mybuddy.Myb.Security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - AuthService")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private OrganizacaoService organizacaoService;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private SignupRequest signupRequest;
    private Role adotanteRole;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("usuario@test.com");
        loginRequest.setPassword("senha123");

        signupRequest = new SignupRequest();
        signupRequest.setNome("João Silva");
        signupRequest.setEmail("joao@test.com");
        signupRequest.setTelefone("11999999999");
        signupRequest.setPassword("senha123");

        adotanteRole = new Role();
        adotanteRole.setId(1L);
        adotanteRole.setName(ERole.ROLE_ADOTANTE);
    }

    @Test
    @DisplayName("Deve autenticar usuário com credenciais válidas")
    void authenticateUser_CredenciaisValidas_RetornaAuthentication() {
        // Arrange
        Authentication expectedAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(expectedAuth);

        // Act
        Authentication result = authService.authenticateUser(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals(expectedAuth, result);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Deve gerar token JWT para autenticação")
    void generateJwtToken_ComAuthentication_RetornaToken() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        String expectedToken = "jwt.token.here";
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(expectedToken);

        // Act
        String result = authService.generateJwtToken(authentication);

        // Assert
        assertNotNull(result);
        assertEquals(expectedToken, result);
        verify(jwtUtils).generateJwtToken(authentication);
    }

    @Test
    @DisplayName("Deve registrar usuário com dados válidos")
    void registerUser_DadosValidos_CriaUsuario() {
        // Arrange
        when(usuarioRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_ADOTANTE)).thenReturn(Optional.of(adotanteRole));
        when(encoder.encode(signupRequest.getPassword())).thenReturn("senhaEncoded");

        // Act
        assertDoesNotThrow(() -> authService.registerUser(signupRequest));

        // Assert
        verify(usuarioRepository).existsByEmail(signupRequest.getEmail());
        verify(roleRepository).findByName(ERole.ROLE_ADOTANTE);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void registerUser_EmailJaExiste_LancaExcecao() {
        // Arrange
        when(usuarioRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class,
                () -> authService.registerUser(signupRequest));

        assertTrue(exception.getMessage().contains("e-mail já está em uso"));
        verify(usuarioRepository).existsByEmail(signupRequest.getEmail());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve criar ONG ao registrar usuário com role ONG")
    void registerUser_ComRoleOng_CriaOrganizacao() {
        // Arrange
        Set<String> roles = new HashSet<>();
        roles.add("ONG");
        signupRequest.setRoles(roles);
        signupRequest.setOrganizacaoCnpj("12345678000100");
        signupRequest.setOrganizacaoNomeFantasia("ONG Teste");
        signupRequest.setOrganizacaoEmailContato("ong@test.com");
        signupRequest.setOrganizacaoEndereco("Rua Teste, 123");

        Role ongRole = new Role();
        ongRole.setId(2L);
        ongRole.setName(ERole.ROLE_ONG);

        Organizacao org = new Organizacao();
        org.setId(1L);

        when(usuarioRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_ONG)).thenReturn(Optional.of(ongRole));
        when(organizacaoService.existeOrganizacaoPorCnpj(signupRequest.getOrganizacaoCnpj())).thenReturn(false);
        when(organizacaoService.criarOrganizacao(any(Organizacao.class))).thenReturn(org);
        when(encoder.encode(signupRequest.getPassword())).thenReturn("senhaEncoded");

        // Act
        assertDoesNotThrow(() -> authService.registerUser(signupRequest));

        // Assert
        verify(organizacaoService).existeOrganizacaoPorCnpj(signupRequest.getOrganizacaoCnpj());
        verify(organizacaoService).criarOrganizacao(any(Organizacao.class));
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando CNPJ da ONG já existe")
    void registerUser_CnpjDuplicado_LancaExcecao() {
        // Arrange
        Set<String> roles = new HashSet<>();
        roles.add("ONG");
        signupRequest.setRoles(roles);
        signupRequest.setOrganizacaoCnpj("12345678000100");
        signupRequest.setOrganizacaoNomeFantasia("ONG Teste");
        signupRequest.setOrganizacaoEmailContato("ong@test.com");
        signupRequest.setOrganizacaoEndereco("Rua Teste, 123");

        Role ongRole = new Role();
        ongRole.setId(2L);
        ongRole.setName(ERole.ROLE_ONG);

        when(usuarioRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_ONG)).thenReturn(Optional.of(ongRole));
        when(organizacaoService.existeOrganizacaoPorCnpj(signupRequest.getOrganizacaoCnpj())).thenReturn(true);

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class,
                () -> authService.registerUser(signupRequest));

        assertTrue(exception.getMessage().contains("CNPJ"));
        verify(organizacaoService).existeOrganizacaoPorCnpj(signupRequest.getOrganizacaoCnpj());
        verify(organizacaoService, never()).criarOrganizacao(any(Organizacao.class));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
