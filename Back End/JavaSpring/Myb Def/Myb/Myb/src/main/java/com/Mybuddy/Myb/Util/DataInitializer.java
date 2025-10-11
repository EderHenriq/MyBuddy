package com.Mybuddy.Myb.Util; // ou o pacote que você escolheu

import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Security.Role;
import com.Mybuddy.Myb.Security.User;
import com.Mybuddy.Myb.Repository.RoleRepository;
import com.Mybuddy.Myb.Repository.UserRepository;
import jakarta.annotation.PostConstruct; // Ou use CommandLineRunner
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @PostConstruct // Esta anotação faz o método run() ser executado após a inicialização do Spring
    public void initData() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(ERole.ROLE_ADOTANTE));
            roleRepository.save(new Role(ERole.ROLE_ONG));
        }

        if (userRepository.count() == 0) {
            // Cria usuário ONG
            User ongUser = new User("onguser", "ong@mybuddy.com", encoder.encode("123456"));
            Set<Role> ongRoles = new HashSet<>();
            roleRepository.findByName(ERole.ROLE_ONG).ifPresent(ongRoles::add);
            ongUser.setRoles(ongRoles);
            userRepository.save(ongUser);

            // Cria usuário ADOTANTE
            User adotanteUser = new User("adotanteuser", "adotante@mybuddy.com", encoder.encode("123456"));
            Set<Role> adotanteRoles = new HashSet<>();
            roleRepository.findByName(ERole.ROLE_ADOTANTE).ifPresent(adotanteRoles::add);
            adotanteUser.setRoles(adotanteRoles);
            userRepository.save(adotanteUser);

            System.out.println("Usuários de teste (onguser, adotanteuser) criados com sucesso!");
        }
    }
}