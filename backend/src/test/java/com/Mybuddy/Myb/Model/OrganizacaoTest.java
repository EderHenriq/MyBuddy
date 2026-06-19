package com.Mybuddy.Myb.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizacaoTest {

    private Organizacao organizacao;

    @BeforeEach
    void setUp() {
        organizacao = Organizacao.builder()
                .id(1L)
                .nomeFantasia("ONG Amigos dos Pets")
                .emailContato("contato@ong.com")
                .cnpj("12345678000100")
                .telefoneContato("44999999999")
                .endereco("Rua das Flores, 100")
                .descricao("ONG dedicada à adoção")
                .website("www.amigodospets.com")
                .latitude(-23.42)
                .longitude(-51.93)
                .build();
    }

    @Test
    void deveCriarOrganizacaoComConstrutorCompleto() {
        assertThat(organizacao.getId()).isEqualTo(1L);
        assertThat(organizacao.getNomeFantasia()).isEqualTo("ONG Amigos dos Pets");
        assertThat(organizacao.getEmailContato()).isEqualTo("contato@ong.com");
        assertThat(organizacao.getCnpj()).isEqualTo("12345678000100");
        assertThat(organizacao.getTelefoneContato()).isEqualTo("44999999999");
        assertThat(organizacao.getEndereco()).isEqualTo("Rua das Flores, 100");
        assertThat(organizacao.getDescricao()).isEqualTo("ONG dedicada à adoção");
        assertThat(organizacao.getWebsite()).isEqualTo("www.amigodospets.com");
        assertThat(organizacao.getLatitude()).isEqualTo(-23.42);
        assertThat(organizacao.getLongitude()).isEqualTo(-51.93);
    }

    @Test
    void deveIniciarComStatusPendentePorPadrao() {
        Organizacao nova = new Organizacao();
        assertThat(nova.getStatusAprovacao()).isEqualTo(StatusAprovacao.PENDENTE_APROVACAO);
        assertThat(nova.isPendente()).isTrue();
        assertThat(nova.isAprovada()).isFalse();
    }

    @Test
    void deveAtualizarStatusParaAprovado() {
        organizacao.setStatusAprovacao(StatusAprovacao.APROVADO);
        assertThat(organizacao.getStatusAprovacao()).isEqualTo(StatusAprovacao.APROVADO);
        assertThat(organizacao.isAprovada()).isTrue();
        assertThat(organizacao.isPendente()).isFalse();
    }

    @Test
    void deveAssociarOrganizacaoAoPet() {
        Pet pet = new Pet();
        pet.setNome("Bolinha");
        pet.setOrganizacao(organizacao);

        assertThat(pet.getOrganizacao()).isEqualTo(organizacao);
    }

    @Test
    void deveAssociarOrganizacaoAoUsuario() {
        Usuario usuario = new Usuario("Eder", "eder@mybuddy.com", "44999999999", "senha123");
        usuario.setOrganizacao(organizacao);

        assertThat(usuario.getOrganizacao()).isEqualTo(organizacao);
    }
}