package com.Mybuddy.Myb.Security.jwt;

import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Security.Role; // Corrigido para Role, assumindo que é a entidade
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
    private String email;

    @JsonIgnore // Garante que a senha não seja serializada para JSON
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    // --- NOVO CAMPO: ID DA ORGANIZAÇÃO DO USUÁRIO ---
    private Long organizacaoId; // Pode ser null se o usuário não for de uma ONG (ex: ADMIN)

    // Construtor atualizado para receber a senha (codificada) E o ID da organização
    public UserDetailsImpl(Long id, String nome, String email, String password,
                           Collection<? extends GrantedAuthority> authorities,
                           Long organizacaoId) { // <-- NOVO PARAMETRO
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.organizacaoId = organizacaoId; // <-- Inicializa o novo campo
    }

    /**
     * Constrói um UserDetailsImpl a partir de um objeto Usuario.
     * @param user O objeto Usuario do banco de dados.
     * @return Uma instância de UserDetailsImpl.
     */
    public static UserDetailsImpl build(Usuario user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        // Extrai o ID da organização se o usuário tiver uma ONG associada.
        // Assumimos que a entidade Usuario tem um campo 'organizacao' que é uma entidade Organizacao.
        // Se a lógica for diferente (ex: o Usuario pode ter um organizacaoId direto), ajuste aqui.
        Long userOrgId = (user.getOrganizacao() != null) ? user.getOrganizacao().getId() : null;

        return new UserDetailsImpl(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                userOrgId); // <-- PASSA O ID DA ORGANIZAÇÃO
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    // --- NOVO GETTER PARA O ID DA ORGANIZAÇÃO ---
    public Long getOrganizacaoId() {
        return organizacaoId;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}