package com.Mybuddy.Myb.Util; // Confirme se este é o pacote correto

import com.Mybuddy.Myb.Model.Organizacao; // Importar a entidade Organizacao
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.OrganizacaoRepository; // Importar o repositório da Organização
import com.Mybuddy.Myb.Repository.RoleRepository;
import com.Mybuddy.Myb.Repository.UsuarioRepository;
import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final OrganizacaoRepository organizacaoRepository; // Injetar OrganizacaoRepository

    public DataInitializer(
            UsuarioRepository usuarioRepository,
            RoleRepository roleRepository,
            PasswordEncoder encoder,
            OrganizacaoRepository organizacaoRepository // Adicionar ao construtor
    ) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.organizacaoRepository = organizacaoRepository;
    }

    @PostConstruct
    @Transactional
    public void initData() {
        System.out.println("Iniciando inicialização de dados...");

        // 1. Garante que as Roles existem no banco de dados
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseGet(() -> {
                    System.out.println("Criando ROLE_ADMIN...");
                    return roleRepository.save(new Role(ERole.ROLE_ADMIN));
                });

        Role ongRole = roleRepository.findByName(ERole.ROLE_ONG)
                .orElseGet(() -> {
                    System.out.println("Criando ROLE_ONG...");
                    return roleRepository.save(new Role(ERole.ROLE_ONG));
                });

        Role adotanteRole = roleRepository.findByName(ERole.ROLE_ADOTANTE)
                .orElseGet(() -> {
                    System.out.println("Criando ROLE_ADOTANTE...");
                    return roleRepository.save(new Role(ERole.ROLE_ADOTANTE));
                });

        // 2. Cria uma Organização de teste se não existir
        // A organização precisa existir antes de associarmos um usuário ONG a ela
        Organizacao myBuddyOrg = organizacaoRepository.findByCnpj("11.222.333/0001-44") // Use um CNPJ de teste
                .orElseGet(() -> {
                    System.out.println("Criando Organização MyBuddy...");
                    Organizacao org = new Organizacao(
                            "MyBuddy ONG Principal",
                            "contato@mybuddy.com",
                            "11.222.333/0001-44", // CNPJ único
                            "(11) 98765-4321",
                            "Rua das Flores, 123 - Centro, SP",
                            "Organização dedicada ao resgate e adoção de animais.",
                            "http://www.mybuddy.com"
                    );
                    return organizacaoRepository.save(org);
                });


        // 3. Cria Usuários de teste se não existirem
        // Cria usuário ADMIN
        if (usuarioRepository.findByEmail("admin@mybuddy.com").isEmpty()) {
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);

            Usuario adminUser = new Usuario(
                    "Administrador MyBuddy",
                    "admin@mybuddy.com",
                    "(11) 99999-9999", // Telefone
                    encoder.encode("admin123"), // Senha codificada
                    null, // Admin não associado a uma ONG específica
                    adminRoles
            );
            usuarioRepository.save(adminUser);
            System.out.println("Usuário Admin (admin@mybuddy.com) criado com sucesso!");
        } else {
            System.out.println("Usuário Admin (admin@mybuddy.com) já existe.");
        }

        // Cria usuário ONG de teste
        if (usuarioRepository.findByEmail("ong@mybuddy.com").isEmpty()) {
            Set<Role> ongRoles = new HashSet<>();
            ongRoles.add(ongRole);

            Usuario ongUser = new Usuario(
                    "ONG Teste",
                    "ong@mybuddy.com",
                    "(11) 98888-8888", // Telefone
                    encoder.encode("ong123"), // Senha codificada
                    myBuddyOrg, // Associa à organização criada
                    ongRoles
            );
            usuarioRepository.save(ongUser);
            System.out.println("Usuário ONG (ong@mybuddy.com) criado com sucesso!");
        } else {
            System.out.println("Usuário ONG (ong@mybuddy.com) já existe.");
        }

        // Cria usuário ADOTANTE de teste
        if (usuarioRepository.findByEmail("adotante@mybuddy.com").isEmpty()) {
            Set<Role> adotanteRoles = new HashSet<>();
            adotanteRoles.add(adotanteRole);

            Usuario adotanteUser = new Usuario(
                    "Adotante Teste",
                    "adotante@mybuddy.com",
                    "(11) 97777-7777", // Telefone
                    encoder.encode("adotante123"), // Senha codificada
                    null, // Adotante não associado a uma ONG
                    adotanteRoles
            );
            usuarioRepository.save(adotanteUser);
            System.out.println("Usuário Adotante (adotante@mybuddy.com) criado com sucesso!");
        } else {
            System.out.println("Usuário Adotante (adotante@mybuddy.com) já existe.");
        }

        System.out.println("Finalizada inicialização de dados.");
    }
}