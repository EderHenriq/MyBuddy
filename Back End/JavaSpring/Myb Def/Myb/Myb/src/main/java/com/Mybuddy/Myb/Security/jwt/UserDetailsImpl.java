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
    private String password; // <-- MUDANÇA AQUI: Campo para a senha codificada

    private Collection<? extends GrantedAuthority> authorities;

    // Construtor atualizado para receber a senha (codificada)
    public UserDetailsImpl(Long id, String nome, String email, String password, // <-- MUDANÇA AQUI
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.password = password; // <-- MUDANÇA AQUI
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(Usuario user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getPassword(),   // <-- MUDANÇA CRUCIAL AQUI: Passa a senha real do usuário
                authorities);
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

    @Override
    public String getPassword() {
        return password; // <-- MUDANÇA CRUCIAL AQUI: Retorna a senha codificada
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