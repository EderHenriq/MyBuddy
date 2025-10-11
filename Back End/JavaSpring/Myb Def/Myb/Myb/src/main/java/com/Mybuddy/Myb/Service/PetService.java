// Em com.Mybuddy.Myb.Service.PetService.java

package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Model.Pet;
import com.Mybuddy.Myb.Model.StatusAdocao;
import com.Mybuddy.Myb.Repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    // Criar Pet (já aceita imageUrl se vier no Pet completo)
    public Pet criarPet(Pet pet) {
        if (pet.getStatusAdocao() == null) {
            pet.setStatusAdocao(StatusAdocao.EM_ADOCAO);
        }
        return petRepository.save(pet);
    }

    // Listar Todos os Pets
    public List<Pet> buscarTodosPets() {
        return petRepository.findAll();
    }

    // Buscar o Pet por ID
    public Optional<Pet> buscarPetPorId(Long id) {
        return petRepository.findById(id);
    }

    // Atualizar o Pet (AGORA INCLUI imageUrl)
    public Pet atualizarPet(Long id, Pet dadosPet) {
        Pet petExistente = petRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Pet com ID " + id + " não encontrado."));

        petExistente.setNome(dadosPet.getNome());
        petExistente.setCor(dadosPet.getCor());
        petExistente.setPorte(dadosPet.getPorte());
        petExistente.setIdade(dadosPet.getIdade());
        petExistente.setSexo(dadosPet.getSexo());
        petExistente.setEspecie(dadosPet.getEspecie());
        petExistente.setRaca(dadosPet.getRaca());

        // NOVO: Atualiza a URL da imagem se uma nova for fornecida
        if (dadosPet.getImageUrl() != null && !dadosPet.getImageUrl().isBlank()) {
            petExistente.setImageUrl(dadosPet.getImageUrl());
        }
        // NOVO: Atualiza o status de adoção se fornecido
        if (dadosPet.getStatusAdocao() != null) {
            petExistente.setStatusAdocao(dadosPet.getStatusAdocao());
        }

        return petRepository.save(petExistente);
    }

    // Deletar o Pet
    public void deletarPet(Long id) {
        if (!petRepository.existsById(id)) {
            throw new IllegalStateException("Pet com ID " + id + " não encontrado.");
        }
        petRepository.deleteById(id);
    }

    public Page<Pet> buscarComFiltros(PetFiltro filtro, Pageable pageable) {
        return petRepository.findAll(PetSpecification.comFiltros(filtro), pageable);
    }
}