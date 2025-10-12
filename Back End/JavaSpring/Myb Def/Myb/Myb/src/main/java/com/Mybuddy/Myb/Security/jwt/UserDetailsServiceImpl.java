package com.Mybuddy.Myb.Security.jwt;
// Pacote onde a classe de serviço de usuários para autenticação JWT está localizada

import com.Mybuddy.Myb.Repository.UserRepository;
// Importa o repositório de usuários para buscar usuários no banco de dados
import com.Mybuddy.Myb.Model.Usuario;
// Importa a entidade Usuario, que representa os dados do usuário
import org.springframework.security.core.userdetails.UserDetails;
// Interface do Spring Security que representa os detalhes do usuário
import org.springframework.security.core.userdetails.UserDetailsService;
// Interface do Spring Security que deve ser implementada para carregar usuários
import org.springframework.security.core.userdetails.UsernameNotFoundException;
// Exceção lançada quando um usuário não é encontrado
import org.springframework.stereotype.Service;
// Marca a classe como um serviço gerenciado pelo Spring
import org.springframework.transaction.annotation.Transactional;
// Garante que a operação seja executada dentro de uma transação do banco

@Service
// Indica que esta classe é um serviço Spring, responsável por lógica de negócios relacionada a usuários
public class UserDetailsServiceImpl implements UserDetailsService {
    // Implementa UserDetailsService, necessário para autenticação com Spring Security

    private final UserRepository userRepository;
    // Repositório para buscar usuários no banco de dados

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    // Construtor que injeta o UserRepository (injeção de dependência)

    @Override
    @Transactional
    // Garante que a busca do usuário seja feita dentro de uma transação
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Método obrigatório da interface UserDetailsService
        // Renomeado para "email" pois o login será feito pelo email

        Usuario user = userRepository.findByEmail(email) // Busca o usuário pelo email
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));
        // Se não encontrar, lança exceção UsernameNotFoundException

        return UserDetailsImpl.build(user);
        // Constrói e retorna um UserDetailsImpl (classe que implementa UserDetails)
        // Usado pelo Spring Security para autenticar e gerenciar permissões do usuário
    }
}
