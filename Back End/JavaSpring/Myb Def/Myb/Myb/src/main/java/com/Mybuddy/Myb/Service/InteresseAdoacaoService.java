package com.Mybuddy.Myb.Service; // Declara o pacote onde esta classe de serviço está localizada.

import com.Mybuddy.Myb.Dto.InteresseAdoacaoMapper; // Importa o mapper para converter entidades em DTOs de resposta.
import com.Mybuddy.Myb.Dto.InteresseResponse; // Importa o DTO de resposta para interesses de adoção.
import com.Mybuddy.Myb.Model.*; // Importa todas as classes de modelo (entidades), incluindo InteresseAdoacao, Usuario, Pet, StatusInteresse.
import com.Mybuddy.Myb.Repository.InteresseAdoacaoRepository; // Importa o repositório para a entidade InteresseAdoacao.
import com.Mybuddy.Myb.Repository.UsuarioRepository; // Importa o repositório para a entidade Usuario.
import com.Mybuddy.Myb.Repository.PetRepository; // Importa o repositório para a entidade Pet.
import org.springframework.stereotype.Service; // Importa a anotação @Service do Spring.
import org.springframework.transaction.annotation.Transactional; // Importa a anotação @Transactional do Spring.

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
    @Transactional
    public InteresseResponse registrarInteresse(Long usuarioId, Long petId, String mensagem) {
        // Busca o usuário pelo ID. Se não encontrado, lança uma exceção.
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));
        // Busca o pet pelo ID. Se não encontrado, lança uma exceção.
        Pet pet = petRepo.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet não encontrado: " + petId));

        InteresseAdoacao interesse = new InteresseAdoacao(); // Cria uma nova instância de InteresseAdoacao.
        interesse.setUsuario(usuario); // Associa o usuário encontrado ao interesse.
        interesse.setPet(pet); // Associa o pet encontrado ao interesse.
        interesse.setMensagem(mensagem); // Define a mensagem do interesse.
        interesse.setStatus(StatusInteresse.PENDENTE); // Define o status inicial do interesse como PENDENTE.

        var salvo = interesseRepo.save(interesse); // Salva o novo interesse de adoção no banco de dados.
        return InteresseAdoacaoMapper.toResponse(salvo); // Converte a entidade salva para um DTO de resposta e o retorna.
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

        // VALIDAÇÃO 1: Não permitir voltar de APROVADO/REJEITADO para PENDENTE (irreversível)
        // Uma vez que um interesse foi processado (aprovado ou rejeitado), não pode voltar a pendente.
        // Isso garante rastreabilidade e evita manipulação de decisões já tomadas.
        if (interesse.getStatus() != StatusInteresse.PENDENTE && novoStatus == StatusInteresse.PENDENTE) {
            throw new IllegalStateException("Não é possível reverter status de interesse já processado para PENDENTE");
        }

        // VALIDAÇÃO 2: Se estiver aprovando o interesse, verificar se o pet ainda está disponível
        // Previne aprovação de interesse quando o pet já foi adotado por outro usuário ou ficou indisponível.
        if (novoStatus == StatusInteresse.APROVADO) {
            Pet pet = interesse.getPet(); // Obtém o pet associado ao interesse
            // Verifica se o pet está no status EM_ADOCAO (disponível para adoção)
            if (!pet.getStatusAdocao().equals(StatusAdocao.EM_ADOCAO)) {
                throw new IllegalStateException("Pet não está mais disponível para adoção. Status atual: " + pet.getStatusAdocao());
            }
        }

        // VALIDAÇÃO 3 (OPCIONAL): Rejeitar outros interesses automaticamente ao aprovar um
        // Quando um interesse é aprovado, pode-se considerar rejeitar automaticamente os demais interesses no mesmo pet.
        // Esta lógica está comentada, mas pode ser ativada conforme regra de negócio:
    /*
    if (novoStatus == StatusInteresse.APROVADO) {
        List<InteresseAdoacao> outrosInteresses = interesseRepo.findByPetIdAndStatusNot(
            interesse.getPet().getId(),
            StatusInteresse.APROVADO
        );
        outrosInteresses.forEach(outro -> {
            if (!outro.getId().equals(interesseId)) {
                outro.setStatus(StatusInteresse.REJEITADO);
                interesseRepo.save(outro);
            }
        });
    }
    */

        interesse.setStatus(novoStatus); // Atualiza o status do interesse com o novo status fornecido

        // Salva as alterações no interesse de adoção no banco de dados através do repositório
        var salvo = interesseRepo.save(interesse);
        // Converte a entidade atualizada para um DTO de resposta e o retorna ao controlador
        return InteresseAdoacaoMapper.toResponse(salvo);
    }

    // Anotação @Transactional(readOnly = true) indica que este método é somente de leitura.
    // Otimiza o desempenho, pois a transação não precisa gerenciar alterações e não bloqueia escritas.
    @Transactional(readOnly = true)
    public List<InteresseResponse> listarPorUsuario(Long usuarioId) {
        // Busca o usuário pelo ID. Se não encontrado, lança uma exceção.
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));
        // Busca todos os interesses de adoção associados ao usuário encontrado.
        return interesseRepo.findByUsuario(usuario)
                .stream() // Converte a lista de entidades para um Stream.
                .map(InteresseAdoacaoMapper::toResponse) // Mapeia cada entidade InteresseAdoacao para um InteresseResponse DTO.
                .toList(); // Coleta os DTOs em uma nova lista e a retorna.
    }
}