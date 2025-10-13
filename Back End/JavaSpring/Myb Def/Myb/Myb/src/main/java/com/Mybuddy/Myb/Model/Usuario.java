package com.Mybuddy.Myb.Model; // Declara o pacote onde este modelo (entidade) está localizado.

import com.Mybuddy.Myb.Security.Role;
// Importa a classe Role, que representa os papéis/perfis de um usuário no sistema (ex: ADOTANTE, ONG).

import jakarta.persistence.*; // Importa todas as anotações JPA (Java Persistence API) para mapeamento de objetos para o banco de dados.

import java.util.HashSet; // Importa a classe HashSet para implementar um conjunto (Set) de forma eficiente.
import java.util.Set; // Importa a interface Set, uma coleção que não permite elementos duplicados.

@Entity // Anotação JPA que marca esta classe como uma entidade, ou seja, uma classe que será mapeada para uma tabela no banco de dados.
@Table (name = "USERS") // Anotação JPA que especifica o nome da tabela no banco de dados para esta entidade como "USERS".
public class Usuario { // Declara a classe que representa um usuário no sistema.

    @Id // Anotação JPA que marca este campo como a chave primária da entidade.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Anotação JPA que configura a estratégia de geração de valor para a chave primária. IDENTITY usa a auto-incremento do banco de dados.
    private long id; // Campo para armazenar o identificador único do usuário.

    @Column (nullable = false, length = 100) // Anotação JPA que mapeia o campo 'nome' para uma coluna no banco de dados.
    // nullable = false: Indica que esta coluna não pode ser nula.
    // length = 100: Define o tamanho máximo da string na coluna.
    private String nome; // Campo para armazenar o nome completo do usuário.

    @Column (unique = true) // Anotação JPA que mapeia o campo 'email' para uma coluna no banco de dados.
    // unique = true: Garante que os valores nesta coluna sejam únicos (não pode haver dois usuários com o mesmo e-mail).
    private String email; // Campo para armazenar o endereço de e-mail do usuário.

    @Column (name = "telefone") // Anotação JPA que mapeia o campo 'telefone' para uma coluna no banco de dados com o nome "telefone".
    private String telefone; // Campo para armazenar o número de telefone do usuário.

    @ManyToMany(fetch = FetchType.LAZY) // Anotação JPA que define um relacionamento Muitos-para-Muitos com a entidade Role.
    // fetch = FetchType.LAZY: Os papéis (roles) serão carregados do banco de dados apenas quando forem acessados.
    @JoinTable(name = "user_roles", // Anotação JPA que especifica a tabela de junção (intermediária) que armazena o relacionamento.
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), // Define a coluna da tabela de junção que se refere ao ID do usuário.
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")) // Define a coluna da tabela de junção que se refere ao ID do papel (role).
    private Set<Role> roles = new HashSet<>(); // Campo que armazena um conjunto (Set) de objetos Role associados a este usuário.
    // Inicializado com HashSet para garantir que não haja roles duplicadas e para uma busca eficiente.

    public Usuario() { // Construtor padrão (vazio), necessário para o JPA.
    }

    public Usuario(String nome, String email, String telefone) { // Construtor parametrizado para facilitar a criação de novos objetos Usuario.
        this.nome = nome; // Inicializa o nome.
        this.email = email; // Inicializa o e-mail.
        this.telefone = telefone; // Inicializa o telefone.
    }

    // ... (restante dos getters e setters)

    // Métodos Getters e Setters para cada atributo da classe, permitindo acesso e modificação dos valores dos campos.

    public long getId() { // Método getter para o ID do usuário.
        return id;
    }

    public void setId(long id) { // Método setter para o ID do usuário.
        this.id = id;
    }

    public String getNome() { // Método getter para o nome do usuário.
        return nome;
    }

    public void setNome(String nome) { // Método setter para o nome do usuário.
        this.nome = nome;
    }

    public String getEmail() { // Método getter para o e-mail do usuário.
        return email;
    }

    public void setEmail(String email) { // Método setter para o e-mail do usuário.
        this.email = email;
    }

    public String getTelefone() { // Método getter para o telefone do usuário.
        return telefone;
    }

    public void setTelefone(String telefone) { // Método setter para o telefone do usuário.
        this.telefone = telefone;
    }

    public Set<Role> getRoles() { // Método getter para o conjunto de roles do usuário.
        return roles;
    }

    public void setRoles(Set<Role> roles) { // Método setter para o conjunto de roles do usuário.
        this.roles = roles;
    }
}