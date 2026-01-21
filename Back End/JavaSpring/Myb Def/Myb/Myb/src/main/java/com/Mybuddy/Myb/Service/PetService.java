package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.PetRequestDTO;
import com.Mybuddy.Myb.DTO.PetResponse;
import com.Mybuddy.Myb.Model.FotoPet;
import com.Mybuddy.Myb.Model.Organizacao;
import com.Mybuddy.Myb.Model.Pet;
import com.Mybuddy.Myb.Model.StatusAdocao;
import com.Mybuddy.Myb.Repository.FotoPetRepository;
import com.Mybuddy.Myb.Repository.InteresseAdoacaoRepository;
import com.Mybuddy.Myb.Repository.OrganizacaoRepository;
import com.Mybuddy.Myb.Repository.PetRepository;
import com.Mybuddy.Myb.Security.jwt.UserDetailsImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PetService {

    private static final Logger log = LoggerFactory.getLogger(PetService.class);

    private final PetRepository petRepository;
    private final InteresseAdoacaoRepository interesseRepo;
    private final OrganizacaoRepository organizacaoRepository;
    private final FotoPetRepository fotoPetRepository;

    public PetService(PetRepository petRepository, InteresseAdoacaoRepository interesseRepo,
                      OrganizacaoRepository organizacaoRepository, FotoPetRepository fotoPetRepository) {
        this.petRepository = petRepository;
        this.interesseRepo = interesseRepo;
        this.organizacaoRepository = organizacaoRepository;
        this.fotoPetRepository = fotoPetRepository;
    }

    @Transactional
    public PetResponse criarPet(PetRequestDTO petRequestDTO) {
        log.debug("Iniciando criação de pet para: {}", petRequestDTO.getNome());
        Pet pet = new Pet();

        pet.setNome(petRequestDTO.getNome());
        pet.setEspecie(petRequestDTO.getEspecie());
        pet.setRaca(petRequestDTO.getRaca());
        pet.setIdade(petRequestDTO.getIdade());
        pet.setCor(petRequestDTO.getCor());
        pet.setPorte(petRequestDTO.getPorte());
        pet.setSexo(petRequestDTO.getSexo());
        pet.setPelagem(petRequestDTO.getPelagem());
        pet.setMicrochipado(petRequestDTO.isMicrochipado());
        pet.setVacinado(petRequestDTO.isVacinado());
        pet.setCastrado(petRequestDTO.isCastrado());
        pet.setCidade(petRequestDTO.getCidade());
        pet.setEstado(petRequestDTO.getEstado());

        pet.setStatusAdocao(petRequestDTO.getStatusAdocao() != null ? petRequestDTO.getStatusAdocao() : StatusAdocao.DISPONIVEL);

        if (petRequestDTO.getOrganizacaoId() != null) {
            Organizacao organizacao = organizacaoRepository.findById(petRequestDTO.getOrganizacaoId())
                    .orElseThrow(() -> new IllegalArgumentException("Organização não encontrada com ID: " + petRequestDTO.getOrganizacaoId()));
            pet.setOrganizacao(organizacao);
        } else {
            throw new IllegalArgumentException("O ID da organização é obrigatório para cadastrar um pet.");
        }

        Pet savedPet = petRepository.save(pet);
        log.debug("Pet salvo no banco de dados com ID: {}", savedPet.getId());

        if (petRequestDTO.getFotosUrls() != null && !petRequestDTO.getFotosUrls().isEmpty()) {
            boolean primeiraFoto = true;
            for (String url : petRequestDTO.getFotosUrls()) {
                FotoPet foto = new FotoPet(url, primeiraFoto, savedPet);
                savedPet.addFoto(foto);
                if (primeiraFoto) primeiraFoto = false;
                log.debug("Adicionada foto ao pet {}: {}", savedPet.getId(), url);
            }
        }

        return toPetResponse(savedPet);
    }

    public List<Pet> buscarTodosPets() {
        return petRepository.findAll();
    }

    public Optional<Pet> buscarPetPorId(Long id) {
        return petRepository.findById(id);
    }

    @Transactional
    public PetResponse atualizarPet(Long id, PetRequestDTO petRequestDTO) {
        log.debug("Iniciando atualização de pet ID: {}", id);
        Pet petExistente = petRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Pet com ID " + id + " não encontrado."));

        Optional.ofNullable(petRequestDTO.getNome()).ifPresent(petExistente::setNome);
        Optional.ofNullable(petRequestDTO.getCor()).ifPresent(petExistente::setCor);
        Optional.ofNullable(petRequestDTO.getPorte()).ifPresent(petExistente::setPorte);
        Optional.ofNullable(petRequestDTO.getIdade()).ifPresent(petExistente::setIdade);
        Optional.ofNullable(petRequestDTO.getSexo()).ifPresent(petExistente::setSexo);
        Optional.ofNullable(petRequestDTO.getEspecie()).ifPresent(petExistente::setEspecie);
        Optional.ofNullable(petRequestDTO.getRaca()).ifPresent(petExistente::setRaca);
        Optional.ofNullable(petRequestDTO.getPelagem()).ifPresent(petExistente::setPelagem);
        Optional.ofNullable(petRequestDTO.getCidade()).ifPresent(petExistente::setCidade);
        Optional.ofNullable(petRequestDTO.getEstado()).ifPresent(petExistente::setEstado);

        petExistente.setMicrochipado(petRequestDTO.isMicrochipado());
        petExistente.setVacinado(petRequestDTO.isVacinado());
        petExistente.setCastrado(petRequestDTO.isCastrado());

        if (petRequestDTO.getFotosUrls() != null) {
            log.debug("Atualizando fotos para o pet ID {}. Novas URLs: {}", id, petRequestDTO.getFotosUrls());
            petExistente.clearFotos();

            if (!petRequestDTO.getFotosUrls().isEmpty()) {
                boolean primeiraFoto = true;
                for (String url : petRequestDTO.getFotosUrls()) {
                    FotoPet novaFoto = new FotoPet(url, primeiraFoto, petExistente);
                    petExistente.addFoto(novaFoto);
                    primeiraFoto = false;
                }
            }
        }

        if (petRequestDTO.getStatusAdocao() != null) {
            petExistente.setStatusAdocao(petRequestDTO.getStatusAdocao());
        }

        if (petRequestDTO.getOrganizacaoId() != null) {
            if (petExistente.getOrganizacao() == null || !petRequestDTO.getOrganizacaoId().equals(petExistente.getOrganizacao().getId())) {
                Organizacao novaOrganizacao = organizacaoRepository.findById(petRequestDTO.getOrganizacaoId())
                        .orElseThrow(() -> new IllegalArgumentException("Nova organização não encontrada com ID: " + petRequestDTO.getOrganizacaoId()));
                petExistente.setOrganizacao(novaOrganizacao);
            }
        } else {
            throw new IllegalArgumentException("O ID da organização é obrigatório para atualizar um pet.");
        }

        Pet updatedPet = petRepository.save(petExistente);
        log.debug("Pet ID {} atualizado com sucesso.", id);
        return toPetResponse(updatedPet);
    }

    /**
     * Verifica se o pet pertence à organização do usuário logado (ONG) ou se o usuário é ADMIN.
     * Método auxiliar para @PreAuthorize.
     *
     * @param petId O ID do pet.
     * @param userOrganizationId O ID da organização do usuário logado (se for ONG). Pode ser null para ADMIN/ADOTANTE.
     * @return true se o usuário tem permissão para modificar o pet, false caso contrário.
     */
    public boolean isPetOwnedByCurrentUser(Long petId, Long userOrganizationId) {
        log.debug("Verificando posse do pet {}. Organização do usuário: {}", petId, userOrganizationId);
        Optional<Pet> petOptional = petRepository.findById(petId);
        if (petOptional.isEmpty()) {
            log.warn("Pet com ID {} não encontrado para verificação de posse.", petId);
            return false;
        }
        Pet pet = petOptional.get();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            log.warn("Tentativa de acesso não autenticada para pet {}.", petId);
            return false;
        }

        // Verifica se é ADMIN
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            log.debug("Usuário é ADMIN. Acesso concedido para pet {}.", petId);
            return true;
        }
        // Verifica se é ONG e se o pet pertence à sua organização
        else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ONG"))) {
            if (pet.getOrganizacao() == null) {
                log.warn("Pet ID {} não tem organização associada.", petId);
                return false;
            }
            if (userOrganizationId == null) {
                log.warn("ID da organização do usuário nulo para ONG ao verificar pet {}.", petId);
                return false;
            }
            boolean isOwner = pet.getOrganizacao().getId().equals(userOrganizationId);
            log.debug("Usuário é ONG. Pet ID {} pertence à organização do usuário ({}): {}", petId, userOrganizationId, isOwner);
            return isOwner;
        }
        log.warn("Usuário sem permissão adequada para pet {}.", petId);
        return false;
    }


    @Transactional
    public void deletarPet(Long id) {
        log.debug("Iniciando exclusão de pet ID: {}", id);
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
        log.debug("Pet ID {} excluído com sucesso.", id);
    }

    // --- NOVO MÉTODO PARA BUSCAR PETS POR ID DA ORGANIZAÇÃO ---
    public List<PetResponse> buscarPetsPorOrganizacaoId(Long organizacaoId) {
        log.debug("Buscando pets pela organização ID: {}", organizacaoId);
        return petRepository.findByOrganizacaoId(organizacaoId) // Chamada ao método do PetRepository
                .stream()
                .map(this::toPetResponse)
                .collect(Collectors.toList());
    }


    private PetResponse toPetResponse(Pet p) {
        List<String> fotosUrls = p.getFotos().stream()
                .sorted(Comparator.comparing(FotoPet::isPrincipal).reversed())
                .map(FotoPet::getUrl)
                .collect(Collectors.toList());

        String statusDescricao = mapStatusAdocaoToFriendlyText(p.getStatusAdocao());

        return new PetResponse(
                p.getId(),
                p.getNome(),
                p.getEspecie() != null ? p.getEspecie().name() : null,
                p.getRaca(),
                p.getIdade(),
                p.getPorte() != null ? p.getPorte().name() : null,
                p.getCor(),
                p.getPelagem(),
                p.getSexo(),
                fotosUrls,
                statusDescricao,
                p.getStatusAdocao(),
                p.getOrganizacao() != null ? p.getOrganizacao().getNomeFantasia() : null,
                p.getOrganizacao() != null ? p.getOrganizacao().getId() : null,
                p.isMicrochipado(),
                p.isVacinado(),
                p.isCastrado(),
                p.getCidade(),
                p.getEstado()
        );
    }

    /**
     * Mapeia o enum StatusAdocao para um texto amigável em português.
     * @param status O StatusAdocao a ser mapeado.
     * @return A descrição amigável do status.
     */
    private String mapStatusAdocaoToFriendlyText(StatusAdocao status) {
        switch (status) {
            case DISPONIVEL: return "Disponível para Adoção";
            case RESERVADO: return "Reservado para Adoção";
            case ADOTADO: return "Adotado";
            case INDISPONIVEL: return "Indisponível para Adoção";
            default: return "Não Informado";
        }
    }


    public Page<PetResponse> buscarComFiltrosDTO(PetFiltro filtro, Pageable pageable) {
        return petRepository.findAll(PetSpecification.comFiltros(filtro), pageable)
                .map(this::toPetResponse);
    }

    public Optional<PetResponse> buscarPetPorIdDTO(Long id) {
        return petRepository.findById(id)
                .map(this::toPetResponse);
    }
}