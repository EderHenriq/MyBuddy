package com.Mybuddy.Myb.Util;

import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.RoleRepository;
import com.Mybuddy.Myb.Repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer { // O nome 'DataInitializer' está OK, mas 'DatabaseInitializer' ou 'InitialDataLoader' são comuns.

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public DataInitializer(UsuarioRepository usuarioRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @PostConstruct
    @Transactional
    public void initData() {
        System.out.println("Iniciando inicialização de dados...");

        // 1. Garante que as Roles existem no banco de dados
        // Mantenha o ADMIN como o primeiro a ser criado se for usado para criar outros usuários de teste.
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseGet(() -> {
                    System.out.println("Criando ROLE_ADMIN...");
                    return roleRepository.save(new Role(ERole.ROLE_ADMIN));
                });

        // ADICIONADO: Garante que a ROLE_ONG existe
        Role ongRole = roleRepository.findByName(ERole.ROLE_ONG) // <<< Linha adicionada
                .orElseGet(() -> {
                    System.out.println("Criando ROLE_ONG...");
                    return roleRepository.save(new Role(ERole.ROLE_ONG));
                });

        // ADICIONADO: Garante que a ROLE_ADOTANTE existe
        Role adotanteRole = roleRepository.findByName(ERole.ROLE_ADOTANTE) // <<< Linha adicionada
                .orElseGet(() -> {
                    System.out.println("Criando ROLE_ADOTANTE...");
                    return roleRepository.save(new Role(ERole.ROLE_ADOTANTE));
                });


        // 2. Cria Usuários de teste se não existirem
        // Cria usuário ADMIN
        if (usuarioRepository.findByEmail("admin@mybuddy.com").isEmpty()) {
            Usuario adminUser = new Usuario("Administrador MyBuddy", "admin@mybuddy.com", encoder.encode("123456789"));
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole); // Atribui a role ADMIN
            adminUser.setRoles(adminRoles);
            usuarioRepository.save(adminUser);
            System.out.println("Usuário Admin (admin@mybuddy.com) criado com sucesso!");
        } else {
            System.out.println("Usuário Admin (admin@mybuddy.com) já existe.");
        }

        // ADICIONADO: Cria usuário ONG de teste
        if (usuarioRepository.findByEmail("ong@mybuddy.com").isEmpty()) {
            Usuario ongUser = new Usuario("ONG Teste", "ong@mybuddy.com", encoder.encode("ong123"));
            Set<Role> ongRoles = new HashSet<>();
            ongRoles.add(ongRole); // Atribui a role ONG
            ongUser.setRoles(ongRoles);
            usuarioRepository.save(ongUser);
            System.out.println("Usuário ONG (ong@mybuddy.com) criado com sucesso!");
        } else {
            System.out.println("Usuário ONG (ong@mybuddy.com) já existe.");
        }

        // ADICIONADO: Cria usuário ADOTANTE de teste
        if (usuarioRepository.findByEmail("adotante@mybuddy.com").isEmpty()) {
            Usuario adotanteUser = new Usuario("Adotante Teste", "adotante@mybuddy.com", encoder.encode("adotante123"));
            Set<Role> adotanteRoles = new HashSet<>();
            adotanteRoles.add(adotanteRole); // Atribui a role ADOTANTE
            adotanteUser.setRoles(adotanteRoles);
            usuarioRepository.save(adotanteUser);
            System.out.println("Usuário Adotante (adotante@mybuddy.com) criado com sucesso!");
        } else {
            System.out.println("Usuário Adotante (adotante@mybuddy.com) já existe.");
        }


        System.out.println("Finalizada inicialização de dados.");
    }
}