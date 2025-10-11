package com.Mybuddy.Myb.Util;

import com.Mybuddy.Myb.Security.ERole;
// Assumindo que sua entidade Role está em 'Entity' ou 'Security' (confirme o caminho correto!)
import com.Mybuddy.Myb.Security.Role; // <<--- CONFIRME este import
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.RoleRepository;
import com.Mybuddy.Myb.Repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional; // <<--- NOVO IMPORT

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

    @PostConstruct
    @Transactional // <<--- ADICIONADO: Garante que tudo abaixo execute em uma única transação
    public void initData() {
        // 1. Garante que as Roles existam e as obtém do banco de dados
        Role adotanteRole = roleRepository.findByName(ERole.ROLE_ADOTANTE)
                .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_ADOTANTE)));
        Role ongRole = roleRepository.findByName(ERole.ROLE_ONG)
                .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_ONG)));

        // <<--- Se as roles foram criadas agora, imprima
        if (roleRepository.count() == 0) { // Verifica novamente se estavam vazias
            System.out.println("Roles ROLE_ADOTANTE e ROLE_ONG criadas!");
        }


        // 2. Cria Usuários de teste se não existirem
        if (userRepository.count() == 0) {
            // Cria usuário ONG
            Usuario ongUser = new Usuario("onguser", "ong@mybuddy.com", encoder.encode("123456"));
            Set<Role> ongRoles = new HashSet<>();
            ongRoles.add(ongRole); // Adiciona a role já persistida
            ongUser.setRoles(ongRoles);
            userRepository.save(ongUser); // Salva o Usuario com suas roles

            // Cria usuário ADOTANTE
            Usuario adotanteUser = new Usuario("adotanteuser", "adotante@mybuddy.com", encoder.encode("123456"));
            Set<Role> adotanteRoles = new HashSet<>();
            adotanteRoles.add(adotanteRole); // Adiciona a role já persistida
            adotanteUser.setRoles(adotanteRoles);
            userRepository.save(adotanteUser); // Salva o Usuario com suas roles

            System.out.println("Usuários de teste (onguser, adotanteuser) criados com sucesso!");
        }
    }
}