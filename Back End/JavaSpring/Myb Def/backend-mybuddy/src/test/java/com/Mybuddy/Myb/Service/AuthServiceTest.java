package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Payload.Request.LoginRequest;
import com.Mybuddy.Myb.Payload.Request.SignupRequest;
import com.Mybuddy.Myb.Repository.RoleRepository;
import com.Mybuddy.Myb.Repository.UsuarioRepository;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import com.Mybuddy.Myb.Security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private Role roleAdotante;

    @BeforeEach
    void setUp() {
        // Signup base — adotante
        signupRequest = new SignupRequest();
        signupRequest.setNome("Eder");
        signupRequest.setEmail("eder@mybuddy.com");
        signupRequest.setTelefone("44999999999");
        signupRequest.setPassword("senha123");
        signupRequest.setRoles(Set.of("ADOTANTE"));

        // Login base
        loginRequest = new LoginRequest();
        loginRequest.setEmail("eder@mybuddy.com");
        loginRequest.setPassword("senha123");

        // Role adotante
        roleAdotante = new Role();
        roleAdotante.setName(ERole.ROLE_ADOTANTE);
    }

    // ===================== authenticateUser =====================

    @Test
    void deveAutenticarUsuarioComSucesso() {
        // Arrange
        Authentication authMock = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authMock);

        // Act
        Authentication resultado = authService.authenticateUser(loginRequest);

        // Assert
        assertThat(resultado).isNotNull();
        verify(authenticationManager, times(1)).authenticate(any());
    }

    // ===================== registerUser =====================

    @Test
    void deveRegistrarAdotanteComSucesso() {
        // Arrange
        when(usuarioRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByTelefone(signupRequest.getTelefone())).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_ADOTANTE)).thenReturn(Optional.of(roleAdotante));
        when(encoder.encode(any())).thenReturn("senhaCriptografada");

        // Act
        authService.registerUser(signupRequest);

        // Assert
        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExiste() {
        // Arrange
        when(usuarioRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.registerUser(signupRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Erro: O e-mail já está em uso!");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoTelefoneJaExiste() {
        // Arrange
        when(usuarioRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByTelefone(signupRequest.getTelefone())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.registerUser(signupRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Erro: O telefone já está em uso!");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoRoleOngSemCnpj() {
        // Arrange
        signupRequest.setRoles(Set.of("ONG"));
        signupRequest.setNome("ONG Teste");

        Role roleOng = new Role();
        roleOng.setName(ERole.ROLE_ONG);

        when(usuarioRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByTelefone(signupRequest.getTelefone())).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_ONG)).thenReturn(Optional.of(roleOng));

        // Act & Assert
        assertThatThrownBy(() -> authService.registerUser(signupRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("O CNPJ da organização é obrigatório para a role ONG.");

        verify(usuarioRepository, never()).save(any());
    }
}