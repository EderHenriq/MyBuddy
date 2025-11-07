package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Exception.ConflictException; // Adicione esta importação
import com.Mybuddy.Myb.Exception.ResourceNotFoundException; // Adicione esta importação
import com.Mybuddy.Myb.Model.Organizacao;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Payload.Request.LoginRequest;
import com.Mybuddy.Myb.Payload.Request.SignupRequest;
import com.Mybuddy.Myb.Payload.Response.JwtResponse; // Para caso a autenticação venha para cá
import com.Mybuddy.Myb.Repository.RoleRepository;
import com.Mybuddy.Myb.Repository.UsuarioRepository;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import com.Mybuddy.Myb.Security.jwt.JwtUtils;
import com.Mybuddy.Myb.Security.jwt.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder; // Para manter o contexto de segurança
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante para transações

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository; // Renomeado para usuarioRepository
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final OrganizacaoService organizacaoService; // Injeta o OrganizacaoService

    @Autowired // @Autowired no construtor é opcional a partir do Spring 4.3, mas é boa prática para clareza
    public AuthService(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository,
                       RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils,
                       OrganizacaoService organizacaoService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.organizacaoService = organizacaoService;
    }

    /**
     * Autentica um usuário e retorna o objeto Authentication.
     * Mantido aqui, mas também poderia retornar JwtResponse diretamente se preferir.
     */
    public Authentication authenticateUser(LoginRequest loginRequest) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
    }

    /**
     * Gera um token JWT para a autenticação fornecida.
     */
    public String generateJwtToken(Authentication authentication) {
        return jwtUtils.generateJwtToken(authentication);
    }

    /**
     * Lógica principal para registrar um novo usuário, incluindo a criação/associação de ONGs.
     * @param signUpRequest DTO com os dados de registro.
     * @throws ConflictException Se o e-mail ou CNPJ da ONG já estiverem em uso.
     * @throws RuntimeException Para erros de role não encontrada ou campos obrigatórios ausentes.
     */
    @Transactional // Garante que a criação da ONG e do usuário sejam uma transação atômica
    public void registerUser(SignupRequest signUpRequest) {
        // 1. Validação de e-mail de usuário existente
        if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new ConflictException("Erro: O e-mail já está em uso!");
        }

        // Opcional: Validação de telefone de usuário existente
        if (signUpRequest.getTelefone() != null && usuarioRepository.existsByTelefone(signUpRequest.getTelefone())) {
            throw new ConflictException("Erro: O telefone já está em uso!");
        }

        Set<String> strRoles = signUpRequest.getRoles(); // Agora é getRoles()
        Set<Role> roles = new HashSet<>();
        Organizacao organizacaoAssociada = null; // Inicializada como null

        // 2. Processamento das Roles e Lógica da ONG
        if (strRoles == null || strRoles.isEmpty()) {
            Role adotanteRole = roleRepository.findByName(ERole.ROLE_ADOTANTE)
                    .orElseThrow(() -> new ResourceNotFoundException("Erro: Role ADOTANTE não encontrada."));
            roles.add(adotanteRole);
        } else {
            for (String roleName : strRoles) {
                switch (roleName.toUpperCase()) { // Convertendo para maiúsculas para comparar com ERole
                    case "ADMIN":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new ResourceNotFoundException("Erro: Role ADMIN não encontrada."));
                        roles.add(adminRole);
                        break;

                    case "ONG":
                        Role ongRole = roleRepository.findByName(ERole.ROLE_ONG)
                                .orElseThrow(() -> new ResourceNotFoundException("Erro: Role ONG não encontrada."));
                        roles.add(ongRole);

                        // --- Lógica de Criação da Organização para a role ONG ---
                        // Validações para campos obrigatórios da ONG
                        if (signUpRequest.getOrganizacaoCnpj() == null || signUpRequest.getOrganizacaoCnpj().trim().isEmpty()) {
                            throw new RuntimeException("O CNPJ da organização é obrigatório para a role ONG.");
                        }
                        if (signUpRequest.getOrganizacaoNomeFantasia() == null || signUpRequest.getOrganizacaoNomeFantasia().trim().isEmpty()) {
                            throw new RuntimeException("O Nome Fantasia da organização é obrigatório para a role ONG.");
                        }
                        if (signUpRequest.getOrganizacaoEmailContato() == null || signUpRequest.getOrganizacaoEmailContato().trim().isEmpty()) {
                            throw new RuntimeException("O E-mail de Contato da organização é obrigatório para a role ONG.");
                        }
                        if (signUpRequest.getOrganizacaoEndereco() == null || signUpRequest.getOrganizacaoEndereco().trim().isEmpty()) {
                            throw new RuntimeException("O Endereço da organização é obrigatório para a role ONG.");
                        }

                        // Verifica se já existe uma ONG com o CNPJ ou Email de Contato (usando o serviço de ONG)
                        if (organizacaoService.existeOrganizacaoPorCnpj(signUpRequest.getOrganizacaoCnpj())) {
                            throw new ConflictException("Já existe uma organização com o CNPJ: " + signUpRequest.getOrganizacaoCnpj());
                        }
                        // O OrganizacaoService.criarOrganizacao já verifica duplicidade de emailContato
                        // então não precisamos duplicar a validação aqui para o email da ONG.

                        // Cria a nova entidade Organizacao
                        Organizacao novaOrganizacao = new Organizacao();
                        novaOrganizacao.setCnpj(signUpRequest.getOrganizacaoCnpj());
                        novaOrganizacao.setNomeFantasia(signUpRequest.getOrganizacaoNomeFantasia());
                        novaOrganizacao.setEmailContato(signUpRequest.getOrganizacaoEmailContato());
                        novaOrganizacao.setTelefoneContato(signUpRequest.getOrganizacaoTelefoneContato());
                        novaOrganizacao.setEndereco(signUpRequest.getOrganizacaoEndereco());
                        novaOrganizacao.setDescricao(signUpRequest.getOrganizacaoDescricao());
                        novaOrganizacao.setWebsite(signUpRequest.getOrganizacaoWebsite());

                        // Salva a ONG usando o OrganizacaoService
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

        // 3. Cria e Salva o Usuário
        Usuario user = new Usuario(
                signUpRequest.getNome(),
                signUpRequest.getEmail(),
                signUpRequest.getTelefone(),
                encoder.encode(signUpRequest.getPassword())
        );
        user.setOrganizacao(organizacaoAssociada); // Associa a ONG (será null para Adotante/Admin)
        user.setRoles(roles); // Atribui as roles

        usuarioRepository.save(user); // Salva o usuário no banco
    }
}