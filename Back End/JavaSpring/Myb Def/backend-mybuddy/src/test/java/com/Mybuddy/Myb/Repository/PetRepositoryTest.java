package com.Mybuddy.Myb.Repository;

import com.Mybuddy.Myb.Model.*;
import com.Mybuddy.Myb.Repository.mongo.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de repositório para a entidade Pet adaptados para o MongoDB.
 * Todos os logs e comentários seguem os padrões em pt-br.
 */
@DataMongoTest
class PetRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PetRepository petRepository;

    private Organizacao organizacao1;
    private Organizacao organizacao2;
    private Pet pet1;
    private Pet pet2;
    private Pet pet3;

    @BeforeEach
    void setUp() {
        // Limpar coleções para garantir isolamento nos testes
        mongoTemplate.dropCollection(Pet.class);
        mongoTemplate.dropCollection(Organizacao.class);

        organizacao1 = Organizacao.builder()
                .id(1L)
                .nomeFantasia("ONG Patinhas")
                .emailContato("patinhas@email.com")
                .cnpj("11.111.111/0001-11")
                .endereco("Rua A, 123")
                .telefoneContato("11999990001")
                .build();

        organizacao2 = Organizacao.builder()
                .id(2L)
                .nomeFantasia("ONG Amigos dos Bichos")
                .emailContato("amigos@email.com")
                .cnpj("22.222.222/0002-22")
                .endereco("Rua B, 456")
                .telefoneContato("11999990002")
                .build();

        mongoTemplate.save(organizacao1);
        mongoTemplate.save(organizacao2);

        pet1 = new Pet("Rex", "Labrador", 2, Especie.CAO, Porte.GRANDE,
                "Amarelo", null, "M", organizacao1,
                false, true, true, "São Paulo", "SP");
        pet1.setId(1L);

        pet2 = new Pet("Mia", "Siamês", 3, Especie.GATO, Porte.PEQUENO,
                "Branco", null, "F", organizacao1,
                false, true, false, "Campinas", "SP");
        pet2.setId(2L);

        pet3 = new Pet("Bob", "Poodle", 1, Especie.CAO, Porte.PEQUENO,
                "Preto", null, "M", organizacao2,
                true, true, true, "Rio de Janeiro", "RJ");
        pet3.setId(3L);

        mongoTemplate.save(pet1);
        mongoTemplate.save(pet2);
        mongoTemplate.save(pet3);
    }

    // ===================== FIND BY ID =====================

    @Test
    void deveBuscarPetPorIdExistente() {
        Optional<Pet> resultado = petRepository.findById(pet1.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Rex");
    }

    @Test
    void deveRetornarVazioAoBuscarPetPorIdInexistente() {
        Optional<Pet> resultado = petRepository.findById(999L);

        assertThat(resultado).isEmpty();
    }

    // ===================== FIND ALL =====================

    @Test
    void deveBuscarTodosPets() {
        List<Pet> pets = petRepository.findAll();

        assertThat(pets).hasSize(3);
    }

    // ===================== FIND BY ORGANIZACAO ID =====================

    @Test
    void deveBuscarPetsPorOrganizacaoId() {
        List<Pet> pets = petRepository.findByOrganizacaoId(organizacao1.getId());

        assertThat(pets).hasSize(2);
        assertThat(pets).extracting(Pet::getNome)
                .containsExactlyInAnyOrder("Rex", "Mia");
    }

    @Test
    void deveBuscarPetsDaSegundaOrganizacao() {
        List<Pet> pets = petRepository.findByOrganizacaoId(organizacao2.getId());

        assertThat(pets).hasSize(1);
        assertThat(pets.get(0).getNome()).isEqualTo("Bob");
    }

    @Test
    void deveRetornarListaVaziaParaOrganizacaoSemPets() {
        Organizacao organizacaoVazia = Organizacao.builder()
                .id(3L)
                .nomeFantasia("ONG Vazia")
                .emailContato("vazia@email.com")
                .cnpj("33.333.333/0003-33")
                .endereco("Rua C, 789")
                .build();
        mongoTemplate.save(organizacaoVazia);

        List<Pet> pets = petRepository.findByOrganizacaoId(organizacaoVazia.getId());

        assertThat(pets).isEmpty();
    }

    @Test
    void deveRetornarListaVaziaParaOrganizacaoInexistente() {
        List<Pet> pets = petRepository.findByOrganizacaoId(999L);

        assertThat(pets).isEmpty();
    }

    // ===================== SAVE =====================

    @Test
    void deveSalvarNovoPet() {
        Pet novoPet = new Pet("Luna", "Vira-lata", 4, Especie.CAO, Porte.MEDIO,
                "Caramelo", null, "F", organizacao1,
                false, false, false, "Curitiba", "PR");
        novoPet.setId(4L);

        Pet salvo = petRepository.save(novoPet);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Luna");
        assertThat(salvo.getOrganizacao().getId()).isEqualTo(organizacao1.getId());
    }

    @Test
    void deveSalvarPetComStatusDisponivelPorPadrao() {
        Pet novoPet = new Pet("Toby", "Beagle", 2, Especie.CAO, Porte.MEDIO,
                "Tricolor", null, "M", organizacao1,
                false, true, true, "Belo Horizonte", "MG");
        novoPet.setId(5L);

        Pet salvo = petRepository.save(novoPet);

        assertThat(salvo.getStatusAdocao()).isEqualTo(StatusAdocao.DISPONIVEL);
    }

    // ===================== UPDATE =====================

    @Test
    void deveAtualizarPet() {
        pet1.setNome("Rex Atualizado");
        pet1.setStatusAdocao(StatusAdocao.ADOTADO);
        petRepository.save(pet1);

        Optional<Pet> atualizado = petRepository.findById(pet1.getId());

        assertThat(atualizado).isPresent();
        assertThat(atualizado.get().getNome()).isEqualTo("Rex Atualizado");
        assertThat(atualizado.get().getStatusAdocao()).isEqualTo(StatusAdocao.ADOTADO);
    }

    // ===================== DELETE =====================

    @Test
    void deveDeletarPet() {
        petRepository.deleteById(pet1.getId());

        Optional<Pet> deletado = petRepository.findById(pet1.getId());

        assertThat(deletado).isEmpty();
    }

    @Test
    void deveManterOutrosPetsAoDeletarUm() {
        petRepository.deleteById(pet1.getId());

        List<Pet> pets = petRepository.findAll();

        assertThat(pets).hasSize(2);
        assertThat(pets).extracting(Pet::getNome)
                .containsExactlyInAnyOrder("Mia", "Bob");
    }

    @Test
    void deveDeletarApenasPetsDaOrganizacaoCorreta() {
        petRepository.deleteById(pet3.getId());

        List<Pet> petsOrg1 = petRepository.findByOrganizacaoId(organizacao1.getId());
        List<Pet> petsOrg2 = petRepository.findByOrganizacaoId(organizacao2.getId());

        assertThat(petsOrg1).hasSize(2);
        assertThat(petsOrg2).isEmpty();
    }
}