package com.Mybuddy.Myb.Security; // Pacote onde a entidade Role está localizada

import com.Mybuddy.Myb.Security.ERole;
// Importa o enum ERole, que define os tipos de roles (ex: ADMIN, USER)
import com.Mybuddy.Myb.Model.Usuario;
// Importa a entidade Usuario, caso você queira relacionar roles com usuários
import jakarta.persistence.*;
// Importa anotações JPA para mapear a entidade no banco de dados

import java.util.HashSet;
import java.util.Set;
// Importa coleções para relacionamentos ManyToMany (opcional)

@Entity
// Marca a classe como uma entidade JPA, que será mapeada para uma tabela no banco
@Table(name = "roles")
// Define o nome da tabela no banco de dados como "roles"
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Define que o campo 'id' é a chave primária e será gerado automaticamente
    private Integer id;

    @Enumerated(EnumType.STRING)
    // Indica que o enum ERole será armazenado como String no banco
    @Column(length = 20)
    // Define o tamanho máximo da coluna no banco
    private ERole name;

    // Relacionamento ManyToMany inverso (opcional) caso queira acessar usuários pela Role
    // @ManyToMany(mappedBy = "roles")
    // private Set<Usuario> usuarios = new HashSet<>();

    public Role() {}
    // Construtor padrão necessário para JPA

    public Role(ERole name) {
        this.name = name;
    }
    // Construtor para criar uma Role com um nome específico

    public Integer getId() {
        return id;
    }
    // Getter do id

    public void setId(Integer id) {
        this.id = id;
    }
    // Setter do id

    public ERole getName() {
        return name;
    }
    // Getter do nome da Role

    public void setName(ERole name) {
        this.name = name;
    }

}
