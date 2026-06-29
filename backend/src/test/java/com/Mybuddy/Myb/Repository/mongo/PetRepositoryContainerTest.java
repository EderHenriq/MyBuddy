package com.Mybuddy.Myb.Repository.mongo;

import com.Mybuddy.Myb.AbstractContainerBaseTest;
import com.Mybuddy.Myb.Model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.Mybuddy.Myb.Repository.mongo.PetRepository;
import com.Mybuddy.Myb.Repository.mongo.OrganizacaoRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("Desabilitado temporariamente devido a limitações de named pipe do Docker Desktop no Windows")
class PetRepositoryContainerTest extends AbstractContainerBaseTest {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private OrganizacaoRepository organizacaoRepository;

    private Organizacao organizacao;

    @BeforeEach
    void setUp() {
        petRepository.deleteAll();
        organizacaoRepository.deleteAll();

        organizacao = new Organizacao();
        organizacao.setNomeFantasia("ONG Testcontainers");
        organizacao.setEmailContato("tc@email.com");
        organizacao.setCnpj("99.999.999/0001-99");
        organizacao.setEndereco("Rua TC, 1");
        organizacaoRepository.save(organizacao);
    }

    @Test
    void deveSalvarEBuscarPetNoMongoDB() {
        Pet pet = new Pet("Rex", "Labrador", 2, Especie.CAO, Porte.GRANDE,
                "Amarelo", null, "M", organizacao,
                false, true, true, "São Paulo", "SP");

        Pet salvo = petRepository.save(pet);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Rex");
    }

    @Test
    void deveBuscarPetPorNomeNoMongoDB() {
        Pet pet = new Pet("Mia", "Siamês", 3, Especie.GATO, Porte.PEQUENO,
                "Branco", null, "F", organizacao,
                false, true, false, "Campinas", "SP");
        petRepository.save(pet);

        Optional<Pet> resultado = petRepository.findByNome("Mia");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Mia");
    }

    @Test
    void deveBuscarPetsPorOrganizacaoIdNoMongoDB() {
        Pet pet1 = new Pet("Rex", "Labrador", 2, Especie.CAO, Porte.GRANDE,
                "Amarelo", null, "M", organizacao,
                false, true, true, "São Paulo", "SP");
        Pet pet2 = new Pet("Mia", "Siamês", 3, Especie.GATO, Porte.PEQUENO,
                "Branco", null, "F", organizacao,
                false, true, false, "Campinas", "SP");

        petRepository.save(pet1);
        petRepository.save(pet2);

        List<Pet> pets = petRepository.findByOrganizacaoId(organizacao.getId());

        assertThat(pets).hasSize(2);
        assertThat(pets).extracting(Pet::getNome)
                .containsExactlyInAnyOrder("Rex", "Mia");
    }

    @Test
    void deveDeletarPetNoMongoDB() {
        Pet pet = new Pet("Bob", "Poodle", 1, Especie.CAO, Porte.PEQUENO,
                "Preto", null, "M", organizacao,
                true, true, true, "RJ", "RJ");
        Pet salvo = petRepository.save(pet);

        petRepository.deleteById(salvo.getId());

        assertThat(petRepository.findById(salvo.getId())).isEmpty();
    }

    @Test
    void deveAtualizarPetNoMongoDB() {
        Pet pet = new Pet("Rex", "Labrador", 2, Especie.CAO, Porte.GRANDE,
                "Amarelo", null, "M", organizacao,
                false, true, true, "São Paulo", "SP");
        Pet salvo = petRepository.save(pet);

        salvo.setNome("Rex Atualizado");
        salvo.setStatusAdocao(StatusAdocao.ADOTADO);
        petRepository.save(salvo);

        Optional<Pet> atualizado = petRepository.findById(salvo.getId());

        assertThat(atualizado).isPresent();
        assertThat(atualizado.get().getNome()).isEqualTo("Rex Atualizado");
        assertThat(atualizado.get().getStatusAdocao()).isEqualTo(StatusAdocao.ADOTADO);
    }

    @Test
    void deveRetornarListaVaziaParaOrganizacaoSemPets() {
        List<Pet> pets = petRepository.findByOrganizacaoId(999L);

        assertThat(pets).isEmpty();
    }
}