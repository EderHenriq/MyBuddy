package com.Mybuddy.Myb.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizacaoTest {

    private Organizacao organizacao;

    @BeforeEach
    void setUp() {
        organizacao = new Organizacao("ONG Amigos dos Pets", "contato@ong.com",
                "12345678000100", "44999999999", "Rua das Flores, 100",
                "ONG dedicada à adoção", "www.amigodospets.com");
    }
//ss
    @Test
    void deveCriarOrganizacaoComConstrutorCompleto() {
        assertThat(organizacao.getNomeFantasia()).isEqualTo("ONG Amigos dos Pets");
        assertThat(organizacao.getEmailContato()).isEqualTo("contato@ong.com");
        assertThat(organizacao.getCnpj()).isEqualTo("12345678000100");
        assertThat(organizacao.getTelefoneContato()).isEqualTo("44999999999");
        assertThat(organizacao.getEndereco()).isEqualTo("Rua das Flores, 100");
        assertThat(organizacao.getDescricao()).isEqualTo("ONG dedicada à adoção");
        assertThat(organizacao.getWebsite()).isEqualTo("www.amigodospets.com");
    }

    @Test
    void deveIniciarComSetsVazios() {
        Organizacao nova = new Organizacao();
        assertThat(nova.getPets()).isEmpty();
        assertThat(nova.getUsuarios()).isEmpty();
    }

    @Test
    void deveAdicionarPetNaOrganizacao() {
        Pet pet = new Pet();
        pet.setNome("Bolinha");

        organizacao.addPet(pet);

        assertThat(organizacao.getPets()).hasSize(1);
        assertThat(pet.getOrganizacao()).isEqualTo(organizacao);
    }

    @Test
    void deveRemoverPetDaOrganizacao() {
        Pet pet = new Pet();
        organizacao.addPet(pet);

        organizacao.removePet(pet);

        assertThat(organizacao.getPets()).isEmpty();
        assertThat(pet.getOrganizacao()).isNull();
    }

    @Test
    void deveAdicionarUsuarioNaOrganizacao() {
        Usuario usuario = new Usuario("Eder", "eder@mybuddy.com", "44999999999", "senha123");

        organizacao.addUsuario(usuario);

        assertThat(organizacao.getUsuarios()).hasSize(1);
        assertThat(usuario.getOrganizacao()).isEqualTo(organizacao);
    }

    @Test
    void deveRemoverUsuarioDaOrganizacao() {
        Usuario usuario = new Usuario("Eder", "eder@mybuddy.com", "44999999999", "senha123");
        organizacao.addUsuario(usuario);

        organizacao.removeUsuario(usuario);

        assertThat(organizacao.getUsuarios()).isEmpty();
        assertThat(usuario.getOrganizacao()).isNull();
    }
}