package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.Organizacao;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Payload.Request.SignupRequest;
import com.Mybuddy.Myb.Repository.RoleRepository;
import com.Mybuddy.Myb.Repository.UsuarioRepository;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final OrganizacaoService organizacaoService;

    public AuthService(UsuarioRepository usuarioRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder encoder,
                       OrganizacaoService organizacaoService) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.organizacaoService = organizacaoService;
    }

    // TODO MY-110: avaliar migração do cadastro para Keycloak Admin API
    @Transactional
    public void registerUser(SignupRequest signUpRequest) {
        if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new ConflictException("Erro: O e-mail já está em uso!");
        }

        if (signUpRequest.getTelefone() != null && usuarioRepository.existsByTelefone(signUpRequest.getTelefone())) {
            throw new ConflictException("Erro: O telefone já está em uso!");
        }

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        Organizacao organizacaoAssociada = null;

        if (strRoles == null || strRoles.isEmpty()) {
            Role adotanteRole = roleRepository.findByName(ERole.ROLE_ADOTANTE)
                    .orElseThrow(() -> new ResourceNotFoundException("Erro: Role ADOTANTE não encontrada."));
            roles.add(adotanteRole);
        } else {
            for (String roleName : strRoles) {
                switch (roleName.toUpperCase()) {
                    case "ADMIN":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new ResourceNotFoundException("Erro: Role ADMIN não encontrada."));
                        roles.add(adminRole);
                        break;

                    case "ONG":
                        Role ongRole = roleRepository.findByName(ERole.ROLE_ONG)
                                .orElseThrow(() -> new ResourceNotFoundException("Erro: Role ONG não encontrada."));
                        roles.add(ongRole);

                        if (signUpRequest.getOrganizacaoCnpj() == null || signUpRequest.getOrganizacaoCnpj().trim().isEmpty())
                            throw new RuntimeException("O CNPJ da organização é obrigatório para a role ONG.");
                        if (signUpRequest.getOrganizacaoNomeFantasia() == null || signUpRequest.getOrganizacaoNomeFantasia().trim().isEmpty())
                            throw new RuntimeException("O Nome Fantasia da organização é obrigatório para a role ONG.");
                        if (signUpRequest.getOrganizacaoEmailContato() == null || signUpRequest.getOrganizacaoEmailContato().trim().isEmpty())
                            throw new RuntimeException("O E-mail de Contato da organização é obrigatório para a role ONG.");
                        if (signUpRequest.getOrganizacaoEndereco() == null || signUpRequest.getOrganizacaoEndereco().trim().isEmpty())
                            throw new RuntimeException("O Endereço da organização é obrigatório para a role ONG.");

                        if (organizacaoService.existeOrganizacaoPorCnpj(signUpRequest.getOrganizacaoCnpj()))
                            throw new ConflictException("Já existe uma organização com o CNPJ: " + signUpRequest.getOrganizacaoCnpj());

                        Organizacao novaOrganizacao = new Organizacao();
                        novaOrganizacao.setCnpj(signUpRequest.getOrganizacaoCnpj());
                        novaOrganizacao.setNomeFantasia(signUpRequest.getOrganizacaoNomeFantasia());
                        novaOrganizacao.setEmailContato(signUpRequest.getOrganizacaoEmailContato());
                        novaOrganizacao.setTelefoneContato(signUpRequest.getOrganizacaoTelefoneContato());
                        novaOrganizacao.setEndereco(signUpRequest.getOrganizacaoEndereco());
                        novaOrganizacao.setDescricao(signUpRequest.getOrganizacaoDescricao());
                        novaOrganizacao.setWebsite(signUpRequest.getOrganizacaoWebsite());

                        organizacaoAssociada = organizacaoService.criarOrganizacao(novaOrganizacao);
                        break;

                    case "ADOTANTE":
                        Role adotanteRole = roleRepository.findByName(ERole.ROLE_ADOTANTE)
                                .orElseThrow(() -> new ResourceNotFoundException("Erro: Role ADOTANTE não encontrada."));
                        roles.add(adotanteRole);
                        break;

                    default:
                        throw new RuntimeException("Erro: Role inválida: " + roleName);
                }
            }
        }

        Usuario user = new Usuario(
                signUpRequest.getNome(),
                signUpRequest.getEmail(),
                signUpRequest.getTelefone(),
                encoder.encode(signUpRequest.getPassword())
        );
        user.setOrganizacao(organizacaoAssociada);
        user.setRoles(roles);
        usuarioRepository.save(user);
    }
}