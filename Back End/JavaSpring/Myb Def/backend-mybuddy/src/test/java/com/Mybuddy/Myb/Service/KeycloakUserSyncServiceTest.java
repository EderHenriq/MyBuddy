package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Model.Usuario;
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
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakUserSyncServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private KeycloakUserSyncService keycloakUserSyncService;

    private Jwt jwt;

    @BeforeEach
    void setUp() {
        jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .claims(c -> c.putAll(Map.of(
                        "sub", "keycloak-id-123",
                        "email", "pedro@mybuddy.com",
                        "name", "Pedro Lira",
                        "preferred_username", "pedro"
                )))
                .build();
    }

    @Test
    void deveRetornarUsuarioExistenteQuandoKeycloakIdJaCadastrado() {
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setKeycloakId("keycloak-id-123");
        usuarioExistente.setEmail("pedro@mybuddy.com");

        when(usuarioRepository.findByKeycloakId("keycloak-id-123"))
                .thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuarioExistente);

        Usuario resultado = keycloakUserSyncService.syncUsuario(jwt);

        assertThat(resultado.getKeycloakId()).isEqualTo("keycloak-id-123");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void deveCriarUsuarioQuandoKeycloakIdNaoExiste() {
        Usuario usuarioNovo = new Usuario();
        usuarioNovo.setKeycloakId("keycloak-id-123");
        usuarioNovo.setEmail("pedro@mybuddy.com");
        usuarioNovo.setNome("Pedro Lira");
        usuarioNovo.setPassword("KEYCLOAK_MANAGED");

        when(usuarioRepository.findByKeycloakId("keycloak-id-123"))
                .thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuarioNovo);

        Usuario resultado = keycloakUserSyncService.syncUsuario(jwt);

        assertThat(resultado.getKeycloakId()).isEqualTo("keycloak-id-123");
        assertThat(resultado.getEmail()).isEqualTo("pedro@mybuddy.com");
        assertThat(resultado.getNome()).isEqualTo("Pedro Lira");
        assertThat(resultado.getPassword()).isEqualTo("KEYCLOAK_MANAGED");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deveUsarPreferredUsernameQuandoNameForNulo() {
        Jwt jwtSemName = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .claims(c -> c.putAll(Map.of(
                        "sub", "keycloak-id-123",
                        "email", "pedro@mybuddy.com",
                        "preferred_username", "Pedro"
                )))
                .build();

        Usuario usuarioNovo = new Usuario();
        usuarioNovo.setNome("Pedro");

        when(usuarioRepository.findByKeycloakId("keycloak-id-123"))
                .thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuarioNovo);

        Usuario resultado = keycloakUserSyncService.syncUsuario(jwtSemName);

        assertThat(resultado.getNome()).isEqualTo("Pedro");
    }

    @Test
    void deveSincronizarRolesDoKeycloak() {
        Jwt jwtComRoles = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .claims(c -> c.putAll(Map.of(
                        "sub", "keycloak-id-123",
                        "email", "pedro@mybuddy.com",
                        "realm_access", Map.of("roles", List.of("ADMIN", "ONG"))
                )))
                .build();

        Usuario usuarioNovo = new Usuario();
        usuarioNovo.setKeycloakId("keycloak-id-123");

        Role roleAdmin = new Role();
        roleAdmin.setId(1L);
        roleAdmin.setName(ERole.ROLE_ADMIN);

        Role roleOng = new Role();
        roleOng.setId(2L);
        roleOng.setName(ERole.ROLE_ONG);

        when(usuarioRepository.findByKeycloakId("keycloak-id-123"))
                .thenReturn(Optional.empty());
        when(roleRepository.findByName(ERole.ROLE_ADMIN))
                .thenReturn(Optional.of(roleAdmin));
        when(roleRepository.findByName(ERole.ROLE_ONG))
                .thenReturn(Optional.of(roleOng));
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = keycloakUserSyncService.syncUsuario(jwtComRoles);

        assertThat(resultado.getRoles()).hasSize(2);
        assertThat(resultado.getRoles()).contains(roleAdmin, roleOng);
    }
}