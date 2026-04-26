package com.Mybuddy.Myb.factory;

import com.Mybuddy.Myb.Model.Organizacao;

public class OrganizacaoFactory {

    public static Organizacao criarOrganizacaoPadrao() {
        return Organizacao.builder()
                .nomeFantasia("ONG Amigos dos Pets")
                .emailContato("contato@ong.com")
                .cnpj("12345678000100")
                .telefoneContato("44999999999")
                .endereco("Rua das Flores, 100")
                .descricao("ONG dedicada à adoção")
                .website("www.amigodospets.com")
                .build();
    }

    public static Organizacao criarOrganizacaoComCnpj(String cnpj) {
        return Organizacao.builder()
                .nomeFantasia("ONG Teste")
                .emailContato("teste@ong.com")
                .cnpj(cnpj)
                .telefoneContato("44988888888")
                .endereco("Rua Teste, 200")
                .descricao("Descrição teste")
                .build();
    }
}
