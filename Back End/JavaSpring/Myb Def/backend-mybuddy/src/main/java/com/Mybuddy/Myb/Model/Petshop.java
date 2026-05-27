package com.Mybuddy.Myb.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidade Petshop adaptada para o MongoDB.
 * Representa os petshops parceiros da plataforma.
 */
@Document(collection = "petshops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Petshop {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    private String nomeFantasia;

    private String emailContato;

    private String cnpj;

    private String telefoneContato;

    private String endereco;

    private String descricao;

    private String website;

    @DocumentReference(lazy = true)
    @JsonManagedReference
    @ToString.Exclude
    @Builder.Default
    private Set<Usuario> usuarios = new HashSet<>();

    public void addUsuario(Usuario usuario) {
        this.usuarios.add(usuario);
        if (usuario != null) {
            usuario.setPetshop(this);
        }
    }

    public void removeUsuario(Usuario usuario) {
        this.usuarios.remove(usuario);
        if (usuario != null) {
            usuario.setPetshop(null);
        }
    }
}
