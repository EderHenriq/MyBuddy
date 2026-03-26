package com.Mybuddy.Myb.Payload.Response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JwtResponse {

    private String token;
    private final String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private Long organizacaoId;

    public JwtResponse(String token, Long id, String username, String email,
                       List<String> roles, Long organizacaoId) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.organizacaoId = organizacaoId;
    }

    // Aliases mantidos para compatibilidade com código existente
    public String getAccessToken() {
        return token;
    }

    public String getTokenType() {
        return type;
    }
}