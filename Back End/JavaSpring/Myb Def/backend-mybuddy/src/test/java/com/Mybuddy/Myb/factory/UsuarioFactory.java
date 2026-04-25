package com.Mybuddy.Myb.factory;

import com.Mybuddy.Myb.Model.Organizacao;
import com.Mybuddy.Myb.Model.Usuario;

public class UsuarioFactory {

    public static Usuario criarUsuarioPadrao() {
        return new Usuario("Eder Henrique", "eder@mybuddy.com", "44999999999", "senha123");
    }

    public static Usuario criarUsuarioComEmail(String email) {
        return new Usuario("Eder Henrique", email, "44999999999", "senha123");
    }

    public static Usuario criarUsuarioComOrganizacao(Organizacao organizacao) {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setOrganizacao(organizacao);
        return usuario;
    }
}