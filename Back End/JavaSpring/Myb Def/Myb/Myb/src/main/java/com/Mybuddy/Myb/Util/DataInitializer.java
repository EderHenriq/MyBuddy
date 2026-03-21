package com.Mybuddy.Myb.Util;

import com.Mybuddy.Myb.Model.Organizacao;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.OrganizacaoRepository;
import com.Mybuddy.Myb.Repository.RoleRepository;
import com.Mybuddy.Myb.Repository.UsuarioRepository;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final OrganizacaoRepository organizacaoRepository;

    public DataInitializer(
            UsuarioRepository usuarioRepository,
            RoleRepository roleRepository,
            PasswordEncoder encoder,
            OrganizacaoRepository organizacaoRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.organizacaoRepository = organizacaoRepository;
    }

    @PostConstruct
    @Transactional
    public void initData() {
        log.info("Iniciando inicialização de dados...");

        // 1. Garante que as Roles existem no banco de dados
        // O Lombok @RequiredArgsConstructor na classe Role permite usar new Role(ERole)
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseGet(() -> {
                    log.info("Criando ROLE_ADMIN...");
                    return roleRepository.save(new Role(ERole.ROLE_ADMIN));
                });

        Role ongRole = roleRepository.findByName(ERole.ROLE_ONG)
                .orElseGet(() -> {
                    log.info("Criando ROLE_ONG...");
                    return roleRepository.save(new Role(ERole.ROLE_ONG));
                });

        Role adotanteRole = roleRepository.findByName(ERole.ROLE_ADOTANTE)
                .orElseGet(() -> {
                    log.info("Criando ROLE_ADOTANTE...");
                    return roleRepository.save(new Role(ERole.ROLE_ADOTANTE));
                });

        // 2. Cria uma Organização de teste usando o Padrão BUILDER do Lombok
        Organizacao myBuddyOrg = organizacaoRepository.findByCnpj("11.222.333/0001-44")
                .orElseGet(() -> {
                    log.info("Criando Organização MyBuddy via Builder...");
                    return organizacaoRepository.save(Organizacao.builder()
                            .nomeFantasia("MyBuddy ONG Principal")
                            .emailContato("contato@mybuddy.com")
                            .cnpj("11.222.333/0001-44")
                            .telefoneContato("(11) 98765-4321")
                            .endereco("Rua das Flores, 123 - Centro, SP")
                            .descricao("Organização dedicada ao resgate e adoção de animais.")
                            .website("http://www.mybuddy.com")
                            .build());
                });

        // 3. Cria Usuários de teste
        // Nota: Se a classe Usuario também usar @AllArgsConstructor do Lombok,
        // certifique-se de que a ordem dos parâmetros está correta ou use o Builder nela também.

        if (usuarioRepository.findByEmail("admin@mybuddy.com").isEmpty()) {
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);

            Usuario adminUser = new Usuario(
                    "Administrador MyBuddy",
                    "admin@mybuddy.com",
                    "(11) 99999-9999",
                    encoder.encode("admin123"),
                    null,
                    adminRoles
            );
            usuarioRepository.save(adminUser);
            log.info("Usuário Admin criado!");
        }

        if (usuarioRepository.findByEmail("ong@mybuddy.com").isEmpty()) {
            Set<Role> ongRoles = new HashSet<>();
            ongRoles.add(ongRole);

            Usuario ongUser = new Usuario(
                    "ONG Teste",
                    "ong@mybuddy.com",
                    "(11) 98888-8888",
                    encoder.encode("ong123"),
                    myBuddyOrg,
                    ongRoles
            );
            usuarioRepository.save(ongUser);
            log.info("Usuário ONG criado!");
        }

        if (usuarioRepository.findByEmail("adotante@mybuddy.com").isEmpty()) {
            Set<Role> adotanteRoles = new HashSet<>();
            adotanteRoles.add(adotanteRole);

            Usuario adotanteUser = new Usuario(
                    "Adotante Teste",
                    "adotante@mybuddy.com",
                    "(11) 97777-7777",
                    encoder.encode("adotante123"),
                    null,
                    adotanteRoles
            );
            usuarioRepository.save(adotanteUser);
            log.info("Usuário Adotante criado!");
        }

        log.info("Finalizada inicialização de dados.");
    }
}