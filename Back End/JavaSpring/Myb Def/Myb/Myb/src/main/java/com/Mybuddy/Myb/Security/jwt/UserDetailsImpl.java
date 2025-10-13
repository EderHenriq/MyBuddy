package com.Mybuddy.Myb.Security.jwt; // Declara o pacote onde esta classe de detalhes do usuário JWT está localizada.

import com.Mybuddy.Myb.Model.Usuario; // Importa a entidade Usuario, o modelo de usuário do seu sistema.
import com.Mybuddy.Myb.Security.Role; // Importa a entidade Role, que representa os papéis/perfis de um usuário. (Comentário CORRIGIDO indica o caminho correto).
import com.fasterxml.jackson.annotation.JsonIgnore; // Importa a anotação @JsonIgnore, usada para ignorar um campo durante a serialização JSON.
import org.springframework.security.core.GrantedAuthority; // Importa a interface GrantedAuthority do Spring Security, que representa uma permissão concedida ao usuário.
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Importa a implementação padrão de GrantedAuthority.
import org.springframework.security.core.userdetails.UserDetails; // Importa a interface UserDetails do Spring Security, que fornece informações essenciais do usuário para o framework.

import java.util.Collection; // Importa a interface Collection para lidar com coleções genéricas.
import java.util.List; // Importa a interface List para coleções ordenadas.
import java.util.Objects; // Importa a classe Objects para utilitários de objetos, como comparação.
import java.util.stream.Collectors; // Importa Collectors para operar com Streams.

// Declara a classe UserDetailsImpl que implementa a interface UserDetails do Spring Security.
// Esta classe é uma implementação personalizada de UserDetails, que encapsula as informações do usuário
// necessárias para o processo de autenticação e autorização do Spring Security.
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L; // Um identificador de versão para serialização, necessário para classes serializáveis.

    private Long id; // Campo para armazenar o ID do usuário.
    private String nome; // Campo para armazenar o nome do usuário (mapeado para o campo 'nome' da entidade Usuario).
    private String email; // Campo para armazenar o e-mail do usuário.

    @JsonIgnore // Anotação que indica ao Jackson (biblioteca de JSON) para ignorar este campo
    // ao serializar o objeto para JSON. Isso é importante para não expor o "telefone" (que atua como senha)
    // em respostas da API, como no JwtResponse.
    private String telefone; // Campo para armazenar o telefone do usuário, que está sendo usado como a "senha" para autenticação.

    private Collection<? extends GrantedAuthority> authorities; // Campo para armazenar as permissões/papéis (roles) do usuário.

    // Construtor da classe UserDetailsImpl.
    // Usado para inicializar uma instância com as informações do usuário e suas autoridades.
    public UserDetailsImpl(Long id, String nome, String email, String telefone,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;             // Inicializa o ID.
        this.nome = nome;         // Inicializa o nome.
        this.email = email;       // Inicializa o e-mail.
        this.telefone = telefone; // Inicializa o telefone (a "senha").
        this.authorities = authorities; // Inicializa as autoridades (roles).
    }

    // Método estático de fábrica para construir um objeto UserDetailsImpl a partir de uma entidade Usuario.
    public static UserDetailsImpl build(Usuario user) {
        // Converte o conjunto de roles do usuário (user.getRoles()) em uma lista de GrantedAuthority.
        // Cada Role é mapeada para um SimpleGrantedAuthority com o nome da role (ex: "ROLE_ADOTANTE").
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name())) // Converte a enum RoleName para String e cria um SimpleGrantedAuthority.
                .collect(Collectors.toList()); // Coleta os resultados em uma lista.

        // Retorna uma nova instância de UserDetailsImpl com os dados extraídos do objeto Usuario.
        return new UserDetailsImpl(
                user.getId(),         // Passa o ID do usuário.
                user.getNome(),       // Passa o nome do usuário.
                user.getEmail(),      // Passa o e-mail do usuário.
                user.getTelefone(),   // Passa o telefone do usuário (a "senha").
                authorities);         // Passa a lista de autoridades (roles).
    }

    @Override // Implementa o método getAuthorities da interface UserDetails.
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities; // Retorna a coleção de permissões concedidas ao usuário.
    }

    public Long getId() { // Método getter para o ID do usuário.
        return id;
    }

    public String getEmail() { // Método getter para o e-mail do usuário.
        return email;
    }

    public String getNome() { // Método getter para o nome do usuário.
        return nome;
    }

    @Override // Implementa o método getPassword da interface UserDetails.
    public String getPassword() {
        return telefone; // Retorna o telefone, que está sendo usado como a senha para autenticação.
    }

    @Override // Implementa o método getUsername da interface UserDetails.
    public String getUsername() {
        return email; // Retorna o e-mail do usuário, que está sendo usado como o nome de usuário para login.
    }

    @Override // Implementa o método isAccountNonExpired da interface UserDetails.
    public boolean isAccountNonExpired() {
        return true; // Indica que a conta do usuário nunca expira (sem lógica de expiração implementada).
    }

    @Override // Implementa o método isAccountNonLocked da interface UserDetails.
    public boolean isAccountNonLocked() {
        return true; // Indica que a conta do usuário nunca é bloqueada (sem lógica de bloqueio implementada).
    }

    @Override // Implementa o método isCredentialsNonExpired da interface UserDetails.
    public boolean isCredentialsNonExpired() {
        return true; // Indica que as credenciais do usuário nunca expiram (sem lógica de expiração de credenciais implementada).
    }

    @Override // Implementa o método isEnabled da interface UserDetails.
    public boolean isEnabled() {
        return true; // Indica que a conta do usuário está sempre habilitada (sem lógica de habilitação/desabilitação implementada).
    }

    @Override // Sobrescreve o método equals para comparação de objetos UserDetailsImpl.
    public boolean equals(Object o) {
        if (this == o) // Se o objeto for a mesma instância, são iguais.
            return true;
        if (o == null || getClass() != o.getClass()) // Se o objeto for nulo ou de uma classe diferente, não são iguais.
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o; // Faz um cast do objeto para UserDetailsImpl.
        return Objects.equals(id, user.id); // Compara os objetos baseando-se no ID do usuário. Se os IDs forem iguais, os usuários são considerados os mesmos.
    }
}