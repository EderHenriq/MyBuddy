package com.Mybuddy.Myb.Service; // Declara o pacote onde esta classe de serviço está localizada.

import com.Mybuddy.Myb.DTO.InteresseAdocaoMapper; // Importa o mapper para converter entidades em DTOs de resposta.
import com.Mybuddy.Myb.DTO.InteresseResponse; // Importa o DTO de resposta para interesses de adoção.
import com.Mybuddy.Myb.Model.*; // Importa todas as classes de modelo (entidades), incluindo InteresseAdocao, Usuario, Pet, StatusInteresse, StatusAdocao.
import com.Mybuddy.Myb.Repository.InteresseAdocaoRepository; // Importa o repositório para a entidade InteresseAdocao.
import com.Mybuddy.Myb.Repository.PetRepository; // Importa o repositório para a entidade Pet.
import com.Mybuddy.Myb.Repository.UsuarioRepository; // Importa o repositório para a entidade Usuario.
import org.springframework.stereotype.Service; // Importa a anotação @Service do Spring.
import org.springframework.transaction.annotation.Transactional; // Importa a anotação @Transactional do Spring.

import java.time.LocalDateTime; // Importa a classe LocalDateTime para registrar timestamps de criação e atualização.
import java.util.List; // Importa a interface List para lidar com coleções de objetos.

@Service
public class InteresseAdocaoService {


    private final InteresseAdocaoRepository interesseRepo;
    private final UsuarioRepository usuarioRepo;
    private final PetRepository petRepo;

    public InteresseAdocaoService(InteresseAdocaoRepository interesseRepo,
                                  UsuarioRepository usuarioRepo,
                                  PetRepository petRepo) {
        this.interesseRepo = interesseRepo;
        this.usuarioRepo = usuarioRepo;
        this.petRepo = petRepo;
    }

    // Anotação @Transactional do Spring. Indica que este método deve ser executado dentro de uma transação.
    // Isso garante que todas as operações de banco de dados dentro do método sejam tratadas como uma única unidade de trabalho (all-or-nothing).
    // Método principal da BUDDY-77: Permite que um usuário manifeste interesse em adotar um pet disponível.
    // Inclui validações de negócio para garantir integridade dos dados.
    @Transactional
    public InteresseResponse manifestarInteresse(Long usuarioId, Long petId, String mensagem) {
        // Busca o usuário pelo ID. Se não encontrado, lança uma exceção IllegalArgumentException.
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));

        // Busca o pet pelo ID. Se não encontrado, lança uma exceção IllegalArgumentException.
        Pet pet = petRepo.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet não encontrado: " + petId));

        // VALIDAÇÃO 1: Verifica se o pet está disponível para adoção (status = EM_ADOCAO).
        // Se o pet já foi adotado ou está indisponível, lança uma exceção IllegalStateException.
        if (!pet.getStatusAdocao().equals(StatusAdocao.DISPONIVEL)) {
            throw new IllegalStateException("Pet não está disponível para adoção no momento");
        }

        // VALIDAÇÃO 2: Verifica se o usuário já manifestou interesse neste mesmo pet anteriormente.
        // Previne registros duplicados de interesse (mesma combinação usuário + pet).
        // Se já existe um interesse, lança uma exceção IllegalStateException.
        if (interesseRepo.existsByUsuarioAndPet(usuario, pet)) {
            throw new IllegalStateException("Você já manifestou interesse neste pet");
        }

        InteresseAdocao interesse = new InteresseAdocao();
        interesse.setUsuario(usuario); // Associa o usuário encontrado ao interesse.
        interesse.setPet(pet); // Associa o pet encontrado ao interesse.
        interesse.setMensagem(mensagem); // Define a mensagem opcional do interesse.
        interesse.setStatus(StatusInteresse.PENDENTE); // Define o status inicial do interesse como PENDENTE.
        interesse.setCriadoEm(LocalDateTime.now()); // Registra o timestamp de criação do interesse.

        // Salva o novo interesse de adoção no banco de dados através do repositório.
        var salvo = interesseRepo.save(interesse);
        // Converte a entidade salva para um DTO de resposta e o retorna ao controlador.
        return InteresseAdocaoMapper.toResponse(salvo);
    }

    @Transactional
    public InteresseResponse atualizarStatus(Long interesseId, StatusInteresse novoStatus) {
        InteresseAdocao interesse = interesseRepo.findById(interesseId)
                .orElseThrow(() -> new IllegalArgumentException("Interesse não encontrado: " + interesseId));

        interesse.setStatus(novoStatus); // Atualiza o status do interesse com o novo status fornecido.
        interesse.setAtualizadoEm(LocalDateTime.now()); // Registra o timestamp de atualização do interesse.

        // Salva as alterações no interesse de adoção no banco de dados através do repositório.
        var salvo = interesseRepo.save(interesse);
        // Converte a entidade atualizada para um DTO de resposta e o retorna ao controlador.
        return InteresseAdocaoMapper.toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<InteresseResponse> listarPorUsuario(Long usuarioId) {
        // Query com JOIN FETCH para evitar N+1 ao acessar Usuario e Pet no Mapper
        return interesseRepo.findByUsuarioIdWithFetch(usuarioId)
                .stream()
                .map(InteresseAdocaoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InteresseResponse> listarTodos() {
        // Query com JOIN FETCH para evitar N+1 ao acessar Usuario e Pet no Mapper
        return interesseRepo.findAllWithFetch().stream()
                .map(InteresseAdocaoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InteresseResponse> listarInteressesPorOrganizacao(Long organizacaoId) {
        // Query única com JOIN FETCH - evita N+1
        return interesseRepo.findByPetOrganizacaoIdWithFetch(organizacaoId)
                .stream()
                .map(InteresseAdocaoMapper::toResponse)
                .toList();
    }

}
