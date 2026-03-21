package com.Mybuddy.Myb.Payload.Response;

import java.util.List;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username; // Este agora será o NOME do usuário, como você tinha antes.
    private String email;
    private List<String> roles;
    private Long organizacaoId; // NOVO: ID da organização (pode ser null)

    // Construtor atualizado para incluir organizacaoId
    public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles, Long organizacaoId) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.organizacaoId = organizacaoId; // Inicializa o novo campo
    }

    // --- Getters e Setters ---

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    // Getter e Setter para o novo campo organizacaoId
    public Long getOrganizacaoId() {
        return organizacaoId;
    }

    public void setOrganizacaoId(Long organizacaoId) {
        this.organizacaoId = organizacaoId;
    }

    // Nota: O setter para 'roles' geralmente não é fornecido para evitar modificações externas
    // após a criação do objeto de resposta. Se precisar, pode adicioná-lo.
    // public void setRoles(List<String> roles) { this.roles = roles; }
}