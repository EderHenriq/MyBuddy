package com.Mybuddy.Myb.Service;

// --- Imports Existentes ---
import com.Mybuddy.Myb.Model.Pet;
import com.Mybuddy.Myb.Model.StatusAdocao;
import com.Mybuddy.Myb.Repository.PetRepository;
import com.Mybuddy.Myb.Security.jwt.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.Mybuddy.Myb.Repository.InteresseAdoacaoRepository;
import org.springframework.transaction.annotation.Transactional;

// --- Imports Necessários para DTO e Organização ---
import com.Mybuddy.Myb.DTO.PetRequestDTO;
import com.Mybuddy.Myb.Model.Organizacao;
import com.Mybuddy.Myb.Repository.OrganizacaoRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Importação do PetResponse que serve para transação de dados Seguros
import com.Mybuddy.Myb.DTO.PetResponse;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final InteresseAdoacaoRepository interesseRepo;
    private final OrganizacaoRepository organizacaoRepository;

    public PetService(PetRepository petRepository, InteresseAdoacaoRepository interesseRepo, OrganizacaoRepository organizacaoRepository) {
        this.petRepository = petRepository;
        this.interesseRepo = interesseRepo;
        this.organizacaoRepository = organizacaoRepository;
    }

    /**
     * Cria um novo pet a partir de um PetRequestDTO.
     * Busca a organização associada pelo ID e a atribui ao pet antes de salvar.
     * @param petRequestDTO DTO contendo os dados do pet, incluindo o ID da organização.
     * @return O Pet criado e persistido.
     * @throws IllegalArgumentException se o ID da organização for nulo ou a organização não for encontrada.
     */
    public Pet criarPet(PetRequestDTO petRequestDTO) {
        Pet pet = new Pet();

        pet.setNome(petRequestDTO.getNome());
        pet.setEspecie(petRequestDTO.getEspecie());
        pet.setRaca(petRequestDTO.getRaca());
        pet.setIdade(petRequestDTO.getIdade());
        pet.setCor(petRequestDTO.getCor());
        pet.setPorte(petRequestDTO.getPorte());
        pet.setSexo(petRequestDTO.getSexo());
        pet.setImageUrl(petRequestDTO.getImageUrl());

        pet.setMicrochipado(petRequestDTO.isMicrochipado());
        pet.setVacinado(petRequestDTO.isVacinado());
        pet.setCastrado(petRequestDTO.isCastrado());
        pet.setCidade(petRequestDTO.getCidade());
        pet.setEstado(petRequestDTO.getEstado());

        pet.setStatusAdocao(petRequestDTO.getStatusAdocao() != null ? petRequestDTO.getStatusAdocao() : StatusAdocao.EM_ADOCAO);

        if (petRequestDTO.getOrganizacaoId() != null) {
            Organizacao organizacao = organizacaoRepository.findById(petRequestDTO.getOrganizacaoId())
                    .orElseThrow(() -> new IllegalArgumentException("Organização não encontrada com ID: " + petRequestDTO.getOrganizacaoId()));
            pet.setOrganizacao(organizacao);
        } else {
            throw new IllegalArgumentException("O ID da organização é obrigatório para cadastrar um pet.");
        }

        return petRepository.save(pet);
    }

    public List<Pet> buscarTodosPets() {
        return petRepository.findAll();
    }

    public Optional<Pet> buscarPetPorId(Long id) {
        return petRepository.findById(id);
    }

    /**
     * Atualiza os dados de um pet existente a partir de um PetRequestDTO.
     * @param id ID do pet a ser atualizado.
     * @param petRequestDTO DTO contendo os dados atualizados do pet.
     * @return O Pet atualizado e persistido.
     * @throws IllegalStateException se o pet com o ID não for encontrado.
     * @throws IllegalArgumentException se o ID da organização fornecido no DTO for inválido.
     */
    public Pet atualizarPet(Long id, PetRequestDTO petRequestDTO) {
        Pet petExistente = petRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Pet com ID " + id + " não encontrado."));

        if (petRequestDTO.getNome() != null) petExistente.setNome(petRequestDTO.getNome());
        if (petRequestDTO.getCor() != null) petExistente.setCor(petRequestDTO.getCor());
        if (petRequestDTO.getPorte() != null) petExistente.setPorte(petRequestDTO.getPorte());
        if (petRequestDTO.getIdade() != null) petExistente.setIdade(petRequestDTO.getIdade());
        if (petRequestDTO.getSexo() != null) petExistente.setSexo(petRequestDTO.getSexo());
        if (petRequestDTO.getEspecie() != null) petExistente.setEspecie(petRequestDTO.getEspecie());
        if (petRequestDTO.getRaca() != null) petExistente.setRaca(petRequestDTO.getRaca());

        petExistente.setMicrochipado(petRequestDTO.isMicrochipado());
        petExistente.setVacinado(petRequestDTO.isVacinado());
        petExistente.setCastrado(petRequestDTO.isCastrado());
        if (petRequestDTO.getCidade() != null) petExistente.setCidade(petRequestDTO.getCidade());
        if (petRequestDTO.getEstado() != null) petExistente.setEstado(petRequestDTO.getEstado());


        if (petRequestDTO.getImageUrl() != null && !petRequestDTO.getImageUrl().isBlank()) {
            petExistente.setImageUrl(petRequestDTO.getImageUrl());
        } else if (petRequestDTO.getImageUrl() == null) {
            petExistente.setImageUrl(null);
        }

        if (petRequestDTO.getStatusAdocao() != null) {
            petExistente.setStatusAdocao(petRequestDTO.getStatusAdocao());
        }

        if (petRequestDTO.getOrganizacaoId() != null && !petRequestDTO.getOrganizacaoId().equals(petExistente.getOrganizacao().getId())) {
            Organizacao novaOrganizacao = organizacaoRepository.findById(petRequestDTO.getOrganizacaoId())
                    .orElseThrow(() -> new IllegalArgumentException("Nova organização não encontrada com ID: " + petRequestDTO.getOrganizacaoId()));
            petExistente.setOrganizacao(novaOrganizacao);
        }

        return petRepository.save(petExistente);
    }

    /**
     * Método para verificar se o pet pertence à organização do usuário atual.
     * Usado no @PreAuthorize do Controller.
     * @param petId O ID do pet a ser verificado.
     * @param userOrganizationId O ID da organização do usuário logado, obtido do UserDetailsImpl.
     * @return true se o pet pertence à organização do usuário (se for ONG) ou se o usuário é ADMIN, false caso contrário.
     */
    public boolean isPetOwnedByCurrentUser(Long petId, Long userOrganizationId) { // <-- ASSINATURA CORRETA
        // Busca o pet
        Optional<Pet> petOptional = petRepository.findById(petId);
        if (petOptional.isEmpty()) {
            return false; // Pet não existe
        }
        Pet pet = petOptional.get();

        // Obtém a autenticação do contexto de segurança para verificar as roles
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) { // Adicionei verificação de null para authentication
            return false;
        }

        // Verifica se o usuário é ADMIN
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true; // Administradores podem manipular qualquer pet
        }
        // Verifica se o usuário é ONG e se o pet pertence à sua organização
        else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ONG"))) {
            // Se o pet não tem organização ou a organização do pet é diferente da organização do usuário logado
            if (pet.getOrganizacao() == null || !pet.getOrganizacao().getId().equals(userOrganizationId)) { // <-- CORREÇÃO AQUI
                return false;
            }
            return true; // Pet pertence à organização do usuário
        }
        return false; // Outros roles não são proprietários ou não há correspondência
    }


    @Transactional
    public void deletarPet(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Pet com ID " + id + " não encontrado."));

        if (pet.getStatusAdocao().equals(StatusAdocao.ADOTADO)) {
            throw new IllegalStateException("Não é possível excluir pet já adotado. Altere o status para INDISPONIVEL.");
        }

        long countInteresses = interesseRepo.countByPetId(id);
        if (countInteresses > 0) {
            throw new IllegalStateException("Não é possível excluir pet com interesses registrados. Total: " + countInteresses);
        }

        petRepository.deleteById(id);
    }


    public Page<Pet> buscarComFiltros(PetFiltro filtro, Pageable pageable) {
        return petRepository.findAll(PetSpecification.comFiltros(filtro), pageable);
    }

    public Page<PetResponse> buscarComFiltrosDTO(PetFiltro filtro, Pageable pageable) {
        return petRepository.findAll(PetSpecification.comFiltros(filtro), pageable)
                .map(p -> new PetResponse(
                        p.getId(),
                        p.getNome(),
                        p.getEspecie(),
                        p.getRaca(),
                        p.getIdade(),
                        p.getPorte(),
                        p.getCor(),
                        p.getSexo(),
                        p.getImageUrl(),
                        p.getStatusAdocao(),
                        p.getOrganizacao() != null ? p.getOrganizacao().getNomeFantasia() : null
                ));
    }

    public Optional<PetResponse> buscarPetPorIdDTO(Long id) {
        return petRepository.findById(id)
                .map(p -> new PetResponse(
                        p.getId(),
                        p.getNome(),
                        p.getEspecie(),
                        p.getRaca(),
                        p.getIdade(),
                        p.getPorte(),
                        p.getCor(),
                        p.getSexo(),
                        p.getImageUrl(),
                        p.getStatusAdocao(),
                        p.getOrganizacao() != null ? p.getOrganizacao().getNomeFantasia() : null
                ));
    }
}