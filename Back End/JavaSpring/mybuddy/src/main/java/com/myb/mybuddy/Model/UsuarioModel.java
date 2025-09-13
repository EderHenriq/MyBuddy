package com.myb.mybuddy.Model;

import jakarta.persistence.*;

@Entity
@Table (name = "Usuario_tbl")

public class UsuarioModel {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    private String nome;
    private String email;
    private


}
