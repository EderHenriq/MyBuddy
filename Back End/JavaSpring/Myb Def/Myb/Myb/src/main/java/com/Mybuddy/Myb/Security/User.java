package com.Mybuddy.Myb.Security; // Declara o pacote onde esta classe de segurança está localizada.

import jakarta.persistence.*; // Importa todas as anotações JPA (Java Persistence API) para mapeamento de objetos para o banco de dados.
import java.util.HashSet; // Importa a classe HashSet para implementar um conjunto (Set) de forma eficiente.
import java.util.Set; // Importa a interface Set, uma coleção que não permite elementos duplicados.

@Entity // Anotação JPA que marca esta classe como uma entidade, ou seja, uma classe que será mapeada para uma tabela no banco de dados.
@Table(name = "users", // Anotação JPA que especifica o nome da tabela no banco de dados para esta entidade como "users".
        uniqueConstraints = { // Define restrições de unicidade para colunas específicas na tabela.
                @UniqueConstraint(columnNames = "username"), // Garante que a coluna 'username' tenha valores únicos.
                @UniqueConstraint(columnNames = "email") // Garante que a coluna 'email' tenha valores únicos.
        })
public class User { // Declara a classe que representa um usuário para fins de segurança/autenticação.
    // Note que existe uma classe 'Usuario' em 'com.Mybuddy.Myb.Model'.
    // Esta 'User' pode ser uma entidade separada usada especificamente pelo Spring Security,
    // ou uma duplicidade que talvez precise de refatoração para ser unificada com 'Usuario'.

    @Id // Anotação JPA que marca este campo como a chave primária da entidade.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Anotação JPA que configura a estratégia de geração de valor para a chave primária. IDENTITY usa a auto-incremento do banco de dados.
    private Long id; // Campo para armazenar o identificador único do usuário.

    @Column(length = 20) // Anotação JPA que mapeia o campo 'username' para uma coluna no banco de dados com tamanho máximo de 20 caracteres.
    private String username; // Campo para armazenar o nome de usuário (identificador de login).

    @Column(length = 50) // Anotação JPA que mapeia o campo 'email' para uma coluna no banco de dados com tamanho máximo de 50 caracteres.
    private String email; // Campo para armazenar o endereço de e-mail do usuário.

    @Column(length = 120) // Anotação JPA que mapeia o campo 'password' para uma coluna no banco de dados com tamanho máximo de 120 caracteres.
    // length = 120: É um tamanho apropriado para armazenar senhas criptografadas (hashing).
    private String password; // Campo para armazenar a senha do usuário (criptografada).

    @ManyToMany(fetch = FetchType.EAGER) // Anotação JPA que define um relacionamento Muitos-para-Muitos com a entidade Role.
    // fetch = FetchType.EAGER: Os papéis (roles) serão carregados do banco de dados imediatamente junto com o usuário.
    @JoinTable(name = "user_roles", // Anotação JPA que especifica a tabela de junção (intermediária) que armazena o relacionamento entre 'users' e 'roles'.
            joinColumns = @JoinColumn(name = "user_id"), // Define a coluna da tabela de junção que se refere ao ID do usuário.
            inverseJoinColumns = @JoinColumn(name = "role_id")) // Define a coluna da tabela de junção que se refere ao ID do papel (role).
    private Set<Role> roles = new HashSet<>(); // Campo que armazena um conjunto (Set) de objetos Role associados a este usuário.
    // Inicializado com HashSet para garantir que não haja roles duplicadas e para uma busca eficiente.

    public User() {} // Construtor padrão (vazio), necessário para o JPA.

    public User(String username, String email, String password) { // Construtor parametrizado para facilitar a criação de novos objetos User.
        this.username = username; // Inicializa o nome de usuário.
        this.email = email; // Inicializa o e-mail.
        this.password = password; // Inicializa a senha.
    }

    // Métodos Getters e Setters para cada atributo da classe, permitindo acesso e modificação dos valores dos campos.

    public Long getId() { // Método getter para o ID do usuário.
        return id;
    }

    public void setId(Long id) { // Método setter para o ID do usuário.
        this.id = id;
    }

    public String getUsername() { // Método getter para o nome de usuário.
        return username;
    }

    public void setUsername(String username) { // Método setter para o nome de usuário.
        this.username = username;
    }

    public String getEmail() { // Método getter para o e-mail do usuário.
        return email;
    }

    public void setEmail(String email) { // Método setter para o e-mail do usuário.
        this.email = email;
    }

    public String getPassword() { // Método getter para a senha do usuário.
        return password;
    }

    public void setPassword(String password) { // Método setter para a senha do usuário.
        this.password = password;
    }

    public Set<Role> getRoles() { // Método getter para o conjunto de roles do usuário.
        return roles;
    }

    public void setRoles(Set<Role> roles) { // Método setter para o conjunto de roles do usuário.
        this.roles = roles;
    }
}