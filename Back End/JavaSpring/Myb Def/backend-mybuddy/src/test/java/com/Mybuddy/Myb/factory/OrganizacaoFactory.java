package com.Mybuddy.Myb.factory;

import com.Mybuddy.Myb.Model.Organizacao;

public class OrganizacaoFactory {

    public static Organizacao criarOrganizacaoPadrao() {
        return new Organizacao(
                "ONG Amigos dos Pets",
                "contato@ong.com",
                "12345678000100",
                "44999999999",
                "Rua das Flores, 100",
                "ONG dedicada à adoção",
                "www.amigodospets.com"
        );
    }

    public static Organizacao criarOrganizacaoComCnpj(String cnpj) {
        return new Organizacao(
                "ONG Teste",
                "teste@ong.com",
                cnpj,
                "44988888888",
                "Rua Teste, 200",
                "Descrição teste",
                null
        );
    }
}