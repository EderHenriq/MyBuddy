package com.Mybuddy.Myb.Payload.Response; // Declara o pacote onde esta classe de resposta está localizada.
// O termo "Payload.Response" geralmente indica que a classe é um DTO (Data Transfer Object)
// usado para enviar dados como resposta a uma requisição HTTP.

import java.util.List; // Importa a interface List para lidar com coleções ordenadas de elementos.

// Declara a classe JwtResponse.
// Esta classe é um DTO que representa a estrutura de dados retornada ao cliente da API
// após uma autenticação bem-sucedida, contendo o token JWT e informações básicas do usuário.
public class JwtResponse {
    private String token; // Campo para armazenar o token de acesso JWT (JSON Web Token).
    private String type = "Bearer"; // Campo para indicar o tipo de token, com valor padrão "Bearer".
    private Long id; // Campo para armazenar o ID do usuário autenticado.
    private String username; // Campo para armazenar o nome de usuário (ou identificador) do usuário autenticado.
    private String email; // Campo para armazenar o endereço de e-mail do usuário autenticado.
    private List<String> roles; // Campo para armazenar uma lista de strings, representando os papéis/perfis do usuário (ex: "ADOTANTE", "ONG").

    // Construtor da classe JwtResponse.
    // Ele é usado para inicializar um novo objeto JwtResponse com os dados fornecidos.
    public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles) {
        this.token = accessToken; // Atribui o token de acesso fornecido ao campo 'token'.
        this.id = id;             // Atribui o ID do usuário fornecido ao campo 'id'.
        this.username = username; // Atribui o nome de usuário fornecido ao campo 'username'.
        this.email = email;       // Atribui o e-mail fornecido ao campo 'email'.
        this.roles = roles;       // Atribui a lista de papéis fornecida ao campo 'roles'.
    }

    // Métodos Getters e Setters para cada atributo da classe, permitindo acesso e modificação (onde aplicável) dos valores.

    public String getAccessToken() { // Método getter para obter o token de acesso.
        return token;
    }

    public void setAccessToken(String accessToken) { // Método setter para definir o token de acesso.
        this.token = accessToken;
    }

    public String getTokenType() { // Método getter para obter o tipo de token.
        return type;
    }

    public void setTokenType(String tokenType) { // Método setter para definir o tipo de token.
        this.type = tokenType;
    }

    public Long getId() { // Método getter para obter o ID do usuário.
        return id;
    }

    public void setId(Long id) { // Método setter para definir o ID do usuário.
        this.id = id;
    }

    public String getEmail() { // Método getter para obter o e-mail do usuário.
        return email;
    }

    public void setEmail(String email) { // Método setter para definir o e-mail do usuário.
        this.email = email;
    }

    public String getUsername() { // Método getter para obter o nome de usuário.
        return username;
    }

    public void setUsername(String username) { // Método setter para definir o nome de usuário.
        this.username = username;
    }

    public List<String> getRoles() { // Método getter para obter a lista de papéis do usuário. (Não há setter, indicando que a lista de roles é definida no construtor ou por outros meios controlados).
        return roles;
    }
}