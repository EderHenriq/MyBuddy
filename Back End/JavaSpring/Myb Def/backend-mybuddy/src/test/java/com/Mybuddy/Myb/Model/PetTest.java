package com.Mybuddy.Myb.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PetTest {

    private Pet pet;
    private Organizacao organizacao;

    @BeforeEach
    void setUp() {
        organizacao = Organizacao.builder()
                .nomeFantasia("ONG Teste")
                .emailContato("ong@teste.com")
                .cnpj("12345678000100")
                .telefoneContato("44999999999")
                .endereco("Rua Teste, 123")
                .descricao("Descrição")
                .website("www.ong.com")
                .build();

        pet = new Pet("Rex", "Labrador", 3, Especie.CAO, Porte.MEDIO, "Amarelo",
                "CURTA", "MACHO", organizacao, true, true, false, "Maringá", "PR");
    }

    @Test
    void deveCriarPetComConstrutorCompleto() {
        assertThat(pet.getNome()).isEqualTo("Rex");
        assertThat(pet.getRaca()).isEqualTo("Labrador");
        assertThat(pet.getIdade()).isEqualTo(3);
        assertThat(pet.getEspecie()).isEqualTo(Especie.CAO);
        assertThat(pet.getPorte()).isEqualTo(Porte.MEDIO);
        assertThat(pet.getCor()).isEqualTo("Amarelo");
        assertThat(pet.getSexo()).isEqualTo("MACHO");
        assertThat(pet.isMicrochipado()).isTrue();
        assertThat(pet.isVacinado()).isTrue();
        assertThat(pet.isCastrado()).isFalse();
        assertThat(pet.getCidade()).isEqualTo("Maringá");
        assertThat(pet.getEstado()).isEqualTo("PR");
    }

    @Test
    void deveIniciarComStatusDisponivel() {
        assertThat(pet.getStatusAdocao()).isEqualTo(StatusAdocao.DISPONIVEL);
    }

    @Test
    void deveIniciarComStatusDisponivelNoConstrutoVazio() {
        Pet petVazio = new Pet();
        assertThat(petVazio.getStatusAdocao()).isEqualTo(StatusAdocao.DISPONIVEL);
    }

    @Test
    void deveAlterarStatusAdocao() {
        pet.setStatusAdocao(StatusAdocao.ADOTADO);
        assertThat(pet.getStatusAdocao()).isEqualTo(StatusAdocao.ADOTADO);
    }

    @Test
    void deveAdicionarFotoAoPet() {
        FotoPet foto = new FotoPet();
        foto.setUrl("http://foto.com/rex.jpg");

        pet.addFoto(foto);

        assertThat(pet.getFotos()).hasSize(1);
        assertThat(foto.getPet()).isEqualTo(pet);
    }

    @Test
    void deveRemoverFotoDoPet() {
        FotoPet foto = new FotoPet();
        foto.setUrl("http://foto.com/rex.jpg");
        pet.addFoto(foto);

        pet.removeFoto(foto);

        assertThat(pet.getFotos()).isEmpty();
        assertThat(foto.getPet()).isNull();
    }

    @Test
    void deveLimparTodasAsFotos() {
        FotoPet foto1 = new FotoPet();
        FotoPet foto2 = new FotoPet();
        pet.addFoto(foto1);
        pet.addFoto(foto2);

        pet.clearFotos();

        assertThat(pet.getFotos()).isEmpty();
    }

    @Test
    void deveAssociarOrganizacao() {
        assertThat(pet.getOrganizacao()).isEqualTo(organizacao);
    }
}