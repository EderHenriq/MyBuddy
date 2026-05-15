package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.UsuarioRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KeycloakUserSyncService {

    private final UsuarioRepository usuarioRepository;

    public KeycloakUserSyncService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Usuario syncUsuario(Jwt jwt) {
        String keycloakId = jwt.getSubject();

        return usuarioRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> criarUsuarioDoKeycloak(jwt));
    }

    private Usuario criarUsuarioDoKeycloak(Jwt jwt) {
        Usuario usuario = new Usuario();
        usuario.setKeycloakId(jwt.getSubject());
        usuario.setEmail(jwt.getClaimAsString("email"));
        usuario.setNome(jwt.getClaimAsString("name") != null
                ? jwt.getClaimAsString("name")
                : jwt.getClaimAsString("preferred_username"));
        usuario.setPassword("KEYCLOAK_MANAGED");
        return usuarioRepository.save(usuario);
    }
}
