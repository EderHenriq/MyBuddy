// Em com.Mybuddy.Myb.Service.PetService.java
package com.Mybuddy.Myb.Service; // Declara o pacote onde esta classe de serviço está localizada.

import com.Mybuddy.Myb.Model.Pet; // Importa a entidade Pet.
import com.Mybuddy.Myb.Model.StatusAdocao; // Importa a enumeração StatusAdocao.
import com.Mybuddy.Myb.Repository.PetRepository; // Importa o repositório para a entidade Pet.
import org.springframework.stereotype.Service; // Importa a anotação @Service do Spring.

import java.util.List; // Importa a interface List para lidar com coleções de objetos.
import java.util.Optional; // Importa a classe Optional para lidar com resultados que podem estar ausentes.

import org.springframework.data.domain.Page; // Importa a interface Page para resultados paginados.
import org.springframework.data.domain.Pageable; // Importa a interface Pageable para informações de paginação.

// Anotação @Service do Spring, que marca esta classe como um componente de serviço.
// Classes de serviço contêm a lógica de negócio principal da aplicação e atuam como intermediárias
// entre os controladores e as camadas de persistência (repositórios).
@Service
public class PetService { // Declara a classe de serviço para Pets.

    private final PetRepository petRepository; // Declara uma instância do repositório de pets, marcada como 'final'.

    // Construtor da classe. O Spring injeta automaticamente uma instância de PetRepository aqui (Injeção por construtor).
    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository; // Inicializa o repositório de pets.
    }

    // Método para criar um novo pet.
    // Aceita um objeto Pet completo, que pode incluir a URL da imagem.
    public Pet criarPet(Pet pet) {
        // Verifica se o status de adoção não foi definido.
        if (pet.getStatusAdocao() == null) {
            // Se não foi, define o status padrão como EM_ADOCAO.
            pet.setStatusAdocao(StatusAdocao.EM_ADOCAO);
        }
        // Salva o objeto Pet no banco de dados através do repositório e retorna a entidade salva.
        return petRepository.save(pet);
    }

    // Método para listar todos os pets.
    public List<Pet> buscarTodosPets() {
        // Retorna todos os pets encontrados no repositório.
        return petRepository.findAll();
    }

    // Método para buscar um pet por seu ID.
    public Optional<Pet> buscarPetPorId(Long id) {
        // Retorna um Optional contendo o pet se encontrado, ou um Optional vazio caso contrário.
        return petRepository.findById(id);
    }

    // Método para atualizar os dados de um pet existente.
    public Pet atualizarPet(Long id, Pet dadosPet) {
        // Busca o pet existente pelo ID. Se não encontrado, lança uma exceção IllegalStateException.
        Pet petExistente = petRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Pet com ID " + id + " não encontrado."));

        // Atualiza os campos do pet existente com os dados fornecidos no objeto 'dadosPet'.
        petExistente.setNome(dadosPet.getNome());
        petExistente.setCor(dadosPet.getCor());
        petExistente.setPorte(dadosPet.getPorte());
        petExistente.setIdade(dadosPet.getIdade());
        petExistente.setSexo(dadosPet.getSexo());
        petExistente.setEspecie(dadosPet.getEspecie());
        petExistente.setRaca(dadosPet.getRaca());

        // NOVO: Lógica para atualizar a URL da imagem.
        // Se uma nova URL de imagem for fornecida e não for vazia, atualiza o campo imageUrl do pet existente.
        if (dadosPet.getImageUrl() != null && !dadosPet.getImageUrl().isBlank()) {
            petExistente.setImageUrl(dadosPet.getImageUrl());
        }
        // NOVO: Lógica para atualizar o status de adoção.
        // Se um novo status de adoção for fornecido, atualiza o campo statusAdocao do pet existente.
        if (dadosPet.getStatusAdocao() != null) {
            petExistente.setStatusAdocao(dadosPet.getStatusAdocao());
        }

        // Salva o pet existente (com os dados atualizados) no banco de dados e o retorna.
        return petRepository.save(petExistente);
    }

    // Método para deletar um pet por seu ID.
    public void deletarPet(Long id) {
        // Verifica se o pet com o ID fornecido existe.
        if (!petRepository.existsById(id)) {
            // Se não existir, lança uma exceção.
            throw new IllegalStateException("Pet com ID " + id + " não encontrado.");
        }
        // Se existir, deleta o pet do banco de dados.
        petRepository.deleteById(id);
    }

    // Método para buscar pets com filtros e paginação.
    // Recebe um objeto PetFiltro (contendo os critérios de busca) e um objeto Pageable (para informações de paginação).
    public Page<Pet> buscarComFiltros(PetFiltro filtro, Pageable pageable) {
        // Utiliza o repositório para buscar pets.
        // JpaSpecificationExecutor é usado com PetSpecification.comFiltros(filtro)
        // para construir dinamicamente a consulta baseada nos filtros e aplicar a paginação.
        return petRepository.findAll(PetSpecification.comFiltros(filtro), pageable);
    }
}