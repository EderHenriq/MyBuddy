package com.Mybuddy.Myb.Security;

import jakarta.persistence.*;
import java.util.Objects; // Para equals e hashCode

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Consistência: use Long (classe) para IDs

    // O nome da role (ex: "ROLE_ADOTANTE", "ROLE_ONG")
    // Deve ser único e não nulo. O EnumType.STRING já cuida de como é salvo.
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false, unique = true) // name deve ser único para uma Role
    private ERole name;

    // --- Construtores ---
    public Role() {} // Construtor padrão necessário para JPA

    public Role(ERole name) {
        this.name = name;
    } // Construtor para criar uma Role com um nome específico

    // --- Getters e Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }

    // --- Sobrescrita de equals() e hashCode() ---
    // Essencial para o bom funcionamento de coleções (Set) e para comparar entidades.
    // Usa o ID para comparação e hash, já que é o identificador único na persistência.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return id != null && Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 0;
    }

    // --- Sobrescrita de toString() ---
    // Útil para debugging, mostra uma representação significativa do objeto.
    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}