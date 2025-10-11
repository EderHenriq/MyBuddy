package com.Mybuddy.Myb.Security.jwt;

import com.Mybuddy.Myb.Repository.UserRepository;
import com.Mybuddy.Myb.Model.Usuario; // Certifique-se de que é o nome correto
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { // Renomeado parâmetro para 'email'
        Usuario user = userRepository.findByEmail(email) // Usa findByEmail
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));

        return UserDetailsImpl.build(user);
    }
}