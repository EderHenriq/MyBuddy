package com.Mybuddy.Myb.Service; // Declara o pacote onde esta classe de serviço está localizada.

import com.Mybuddy.Myb.DTO.InteresseAdoacaoMapper; // Importa o mapper para converter entidades em DTOs de resposta.
import com.Mybuddy.Myb.DTO.InteresseResponse; // Importa o DTO de resposta para interesses de adoção.
import com.Mybuddy.Myb.Model.*; // Importa todas as classes de modelo (entidades), incluindo InteresseAdoacao, Usuario, Pet, StatusInteresse, StatusAdocao.
import com.Mybuddy.Myb.Repository.InteresseAdoacaoRepository; // Importa o repositório para a entidade InteresseAdoacao.
import com.Mybuddy.Myb.Repository.PetRepository; // Importa o repositório para a entidade Pet.
import com.Mybuddy.Myb.Repository.UsuarioRepository; // Importa o repositório para a entidade Usuario.
import org.springframework.stereotype.Service; // Importa a anotação @Service do Spring.
import org.springframework.transaction.annotation.Transactional; // Importa a anotação @Transactional do Spring.

import java.time.LocalDateTime; // Importa a classe LocalDateTime para registrar timestamps de criação e atualização.
import java.util.List; // Importa a interface List para lidar com coleções de objetos.

// Anotação @Service do Spring, que marca esta classe como um componente de serviço.
// Classes de serviço contêm a lógica de negócio principal da aplicação e atuam como intermediárias
// entre os controladores e as camadas de persistência (repositórios).
@Service
public class InteresseAdoacaoService { // Declara a classe de serviço para Interesses de Adoção.

    // Declara instâncias dos repositórios necessários. São marcados como 'final' para garantir que sejam inicializados uma vez.
    private final InteresseAdoacaoRepository interesseRepo; // Repositório para operações com InteresseAdoacao.
    private final UsuarioRepository usuarioRepo; // Repositório para operações com Usuario.
    private final PetRepository petRepo; // Repositório para operações com Pet.

    // Construtor da classe. O Spring injeta automaticamente as dependências dos repositórios (Injeção por construtor).
    public InteresseAdoacaoService(InteresseAdoacaoRepository interesseRepo,
                                   UsuarioRepository usuarioRepo,
                                   PetRepository petRepo) {
        this.interesseRepo = interesseRepo; // Inicializa o repositório de interesses.
        this.usuarioRepo = usuarioRepo;     // Inicializa o repositório de usuários.
        this.petRepo = petRepo;             // Inicializa o repositório de pets.
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

        // Cria uma nova instância de InteresseAdoacao após passar por todas as validações.
        InteresseAdoacao interesse = new InteresseAdoacao();
        interesse.setUsuario(usuario); // Associa o usuário encontrado ao interesse.
        interesse.setPet(pet); // Associa o pet encontrado ao interesse.
        interesse.setMensagem(mensagem); // Define a mensagem opcional do interesse.
        interesse.setStatus(StatusInteresse.PENDENTE); // Define o status inicial do interesse como PENDENTE.
        interesse.setCriadoEm(LocalDateTime.now()); // Registra o timestamp de criação do interesse.

        // Salva o novo interesse de adoção no banco de dados através do repositório.
        var salvo = interesseRepo.save(interesse);
        // Converte a entidade salva para um DTO de resposta e o retorna ao controlador.
        return InteresseAdoacaoMapper.toResponse(salvo);
    }

    // Anotação @Transactional indica que este método deve ser executado dentro de uma transação.
// Método da BUDDY-95: Permite atualizar o status de um interesse de adoção (PENDENTE → APROVADO/REJEITADO).
// Inclui validações de negócio para garantir integridade do fluxo de adoção.
// Normalmente usado por usuários com permissões de ONG ou ADMIN.
    @Transactional
    public InteresseResponse atualizarStatus(Long interesseId, StatusInteresse novoStatus) {
        // Busca o interesse de adoção pelo ID. Se não encontrado, lança uma exceção IllegalArgumentException.
        InteresseAdoacao interesse = interesseRepo.findById(interesseId)
                .orElseThrow(() -> new IllegalArgumentException("Interesse não encontrado: " + interesseId));

        interesse.setStatus(novoStatus); // Atualiza o status do interesse com o novo status fornecido.
        interesse.setAtualizadoEm(LocalDateTime.now()); // Registra o timestamp de atualização do interesse.

        // Salva as alterações no interesse de adoção no banco de dados através do repositório.
        var salvo = interesseRepo.save(interesse);
        // Converte a entidade atualizada para um DTO de resposta e o retorna ao controlador.
        return InteresseAdoacaoMapper.toResponse(salvo);
    }

    // Anotação @Transactional(readOnly = true) indica que este método é somente de leitura.
    // Otimiza o desempenho, pois a transação não precisa gerenciar alterações e não bloqueia escritas.
    // Método da BUDDY-91: Lista todos os interesses de adoção registrados por um usuário específico.
    @Transactional(readOnly = true)
    public List<InteresseResponse> listarPorUsuario(Long usuarioId) {
        // Busca todos os interesses de adoção associados ao ID do usuário fornecido.
        // Utiliza o método derivado do repositório que busca por usuarioId diretamente.
        return interesseRepo.findByUsuarioId(usuarioId)
                .stream() // Converte a lista de entidades para um Stream para processamento funcional.
                .map(InteresseAdoacaoMapper::toResponse) // Mapeia cada entidade InteresseAdoacao para um InteresseResponse DTO.
                .toList(); // Coleta os DTOs em uma nova lista imutável e a retorna ao controlador.
    }

    @Transactional(readOnly = true)
    public List<InteresseResponse> listarTodos() {
        return interesseRepo.findAll().stream()
                .map(InteresseAdoacaoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InteresseResponse> listarInteressesPorOrganizacao(Long organizacaoId) {
        //  Encontrar todos os pets desta organização
        List<Pet> petsDaOng = petRepo.findByOrganizacaoId(organizacaoId);

        //  Para cada pet, encontrar seus interesses
        return petsDaOng.stream()
                .flatMap(pet -> interesseRepo.findByPet(pet).stream()) // Combina todos os interesses de todos os pets
                .map(InteresseAdoacaoMapper::toResponse)
                .toList();
    }

}
