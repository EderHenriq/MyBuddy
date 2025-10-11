package com.Mybuddy.Myb.Security; // <<--- CONFIRME este pacote

import com.Mybuddy.Myb.Security.ERole;
import com.Mybuddy.Myb.Model.Usuario; // <<-- IMPORTANTE: Importar Usuario aqui também!
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;

    // Se você tiver o ManyToMany inverso na Role, ele ficaria assim:
    // @ManyToMany(mappedBy = "roles")
    // private Set<Usuario> usuarios = new HashSet<>();

    public Role() {}

    public Role(ERole name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }

    // public Set<Usuario> getUsuarios() { return usuarios; }
    // public void setUsuarios(Set<Usuario> usuarios) { this.usuarios = usuarios; }
}