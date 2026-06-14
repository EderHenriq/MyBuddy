package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.mongo.RoleRepository;
import com.Mybuddy.Myb.Repository.mongo.UsuarioRepository;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class KeycloakUserSyncService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public Usuario syncUsuario(Jwt jwt) {
        String keycloakId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        Usuario usuario = usuarioRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> usuarioRepository.findByEmail(email)
                        .map(existente -> {
                            existente.setKeycloakId(keycloakId);
                            return existente;
                        })
                        .orElseGet(() -> criarUsuarioDoKeycloak(jwt))
                );

        syncRoles(usuario, jwt);
        return usuarioRepository.save(usuario);
    }

    private Usuario criarUsuarioDoKeycloak(Jwt jwt) {
        Usuario usuario = new Usuario();
        usuario.setKeycloakId(jwt.getSubject());
        usuario.setEmail(jwt.getClaimAsString("email"));
        usuario.setNome(jwt.getClaimAsString("name") != null
                ? jwt.getClaimAsString("name")
                : jwt.getClaimAsString("preferred_username"));
        usuario.setPassword("KEYCLOAK_MANAGED");
        return usuario;
    }

    private void syncRoles(Usuario usuario, Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null) return;

        List<?> rolesList = (List<?>) realmAccess.get("roles");
        if (rolesList == null) return;

        Set<Role> roles = new HashSet<>();
        for (Object r : rolesList) {
            String roleName = r.toString().toUpperCase();
            ERole eRole = null;

            if ("ADMIN".equals(roleName) || "ROLE_ADMIN".equals(roleName)) {
                eRole = ERole.ROLE_ADMIN;
            } else if ("ONG".equals(roleName) || "ROLE_ONG".equals(roleName)) {
                eRole = ERole.ROLE_ONG;
            } else if ("ADOTANTE".equals(roleName) || "ROLE_ADOTANTE".equals(roleName)) {
                eRole = ERole.ROLE_ADOTANTE;
            } else if ("PETSHOP".equals(roleName) || "ROLE_PETSHOP".equals(roleName)) {
                eRole = ERole.ROLE_PETSHOP;
            }

            if (eRole != null) {
                roleRepository.findByName(eRole).ifPresent(roles::add);
            }
        }
        usuario.setRoles(roles);
    }
}
