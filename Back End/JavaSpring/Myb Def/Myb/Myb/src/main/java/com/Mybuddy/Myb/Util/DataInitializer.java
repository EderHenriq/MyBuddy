package com.Mybuddy.Myb.Util; // Mantenha no pacote 'Util' ou mova para 'Config' se preferir

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

@Configuration // Ainda @Configuration, para ser escaneado pelo Spring
public class DataInitializer { // Renomeado para 'DatabaseInitializer' (ou InitialDataLoader)

    private final UsuarioRepository usuarioRepository; // Renomeado para 'usuarioRepository' para consistência
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

        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN) // ADICIONADO: Role ADMIN
                .orElseGet(() -> {
                    System.out.println("Criando ROLE_ADMIN...");
                    return roleRepository.save(new Role(ERole.ROLE_ADMIN));
                });


        // 2. Cria Usuários de teste se não existirem
        // Cria usuário ADMIN
        if (usuarioRepository.findByEmail("admin@mybuddy.com").isEmpty()) {
            Usuario adminUser = new Usuario("Administrador MyBuddy", "admin@mybuddy.com", encoder.encode("123456789")); // Telefone/Senha do Admin
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminUser.setRoles(adminRoles);
            usuarioRepository.save(adminUser);
            System.out.println("Usuário Admin (admin@mybuddy.com) criado com sucesso!");
        } else {
            System.out.println("Usuário Admin (admin@mybuddy.com) já existe.");
        }


        System.out.println("Finalizada inicialização de dados.");
    }
}