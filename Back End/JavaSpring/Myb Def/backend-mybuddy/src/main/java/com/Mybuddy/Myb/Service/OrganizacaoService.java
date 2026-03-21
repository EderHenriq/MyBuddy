package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.OrganizacaoRequestDTO;
import com.Mybuddy.Myb.DTO.OrganizacaoResponseDTO;
import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.Organizacao;
import com.Mybuddy.Myb.Repository.OrganizacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrganizacaoService {

    private static final Logger logger = LoggerFactory.getLogger(OrganizacaoService.class);

    private final OrganizacaoRepository organizacaoRepository;

    public OrganizacaoService(OrganizacaoRepository organizacaoRepository) {
        this.organizacaoRepository = organizacaoRepository;
    }

    /**
     * NOVO MÉTODO: Cria uma nova organização a partir de uma entidade Organizacao.
     * Este método é ideal para ser usado internamente por outros serviços (ex: AuthService)
     * que já mapearam os dados para a entidade.
     * @param organizacao Entidade Organizacao a ser criada.
     * @return A entidade Organizacao criada e salva.
     * @throws ConflictException Se o CNPJ ou e-mail de contato já existirem.
     */
    @Transactional
    public Organizacao criarOrganizacao(Organizacao organizacao) {
        logger.info("Tentando criar nova organização (via entidade): {}", organizacao.getNomeFantasia());

        if (organizacaoRepository.existsByCnpj(organizacao.getCnpj())) {
            logger.warn("Criação de organização falhou: CNPJ {} já existe.", organizacao.getCnpj());
            throw new ConflictException("CNPJ já cadastrado.");
        }

        if (organizacaoRepository.existsByEmailContato(organizacao.getEmailContato())) {
            logger.warn("Criação de organização falhou: E-mail de contato {} já existe.", organizacao.getEmailContato());
            throw new ConflictException("E-mail de contato da organização já cadastrado.");
        }

        Organizacao savedOrganizacao = organizacaoRepository.save(organizacao);
        logger.info("Organização criada com sucesso com ID: {}", savedOrganizacao.getId());
        return savedOrganizacao;
    }


    /**
     * Cria uma nova organização. Realiza validações de unicidade de CNPJ e e-mail.
     * @param requestDTO Dados da organização a serem criados.
     * @return DTO da organização criada.
     * @throws ConflictException Se o CNPJ ou e-mail de contato já existirem.
     */
    @Transactional // Garante que a operação seja atômica
    public OrganizacaoResponseDTO criarOrganizacao(OrganizacaoRequestDTO requestDTO) {
        logger.info("Tentando criar nova organização (via DTO): {}", requestDTO.getNomeFantasia());

        // Validação de unicidade do CNPJ usando o método existsByCnpj do repositório
        if (organizacaoRepository.existsByCnpj(requestDTO.getCnpj())) {
            logger.warn("Criação de organização falhou: CNPJ {} já existe.", requestDTO.getCnpj());
            throw new ConflictException("CNPJ já cadastrado.");
        }

        // Validação de unicidade do E-mail de Contato usando o método existsByEmailContato do repositório
        if (organizacaoRepository.existsByEmailContato(requestDTO.getEmailContato())) {
            logger.warn("Criação de organização falhou: E-mail de contato {} já existe.", requestDTO.getEmailContato());
            throw new ConflictException("E-mail de contato da organização já cadastrado.");
        }

        // Mapeia DTO de requisição para Entidade Organizacao
        Organizacao organizacao = new Organizacao(
                requestDTO.getNomeFantasia(),
                requestDTO.getEmailContato(),
                requestDTO.getCnpj(),
                requestDTO.getTelefoneContato(),
                requestDTO.getEndereco(),
                requestDTO.getDescricao(),
                requestDTO.getWebsite()
        );

        Organizacao savedOrganizacao = organizacaoRepository.save(organizacao);
        logger.info("Organização criada com sucesso com ID: {}", savedOrganizacao.getId());
        return new OrganizacaoResponseDTO(savedOrganizacao); // Mapeia a entidade salva para um DTO de resposta
    }

    /**
     * Busca uma organização pelo seu ID.
     * @param id O ID da organização.
     * @return DTO da organização encontrada.
     * @throws ResourceNotFoundException Se a organização não for encontrada.
     */
    @Transactional(readOnly = true) // Otimização para operações de leitura
    public OrganizacaoResponseDTO buscarOrganizacaoPorId(Long id) {
        logger.debug("Buscando organização com ID: {}", id);
        // Usa findById e orElseThrow para lançar ResourceNotFoundException se não encontrar
        Organizacao organizacao = organizacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organização não encontrada com ID: " + id));
        return new OrganizacaoResponseDTO(organizacao); // Mapeia a entidade encontrada para um DTO de resposta
    }

    /**
     * NOVO MÉTODO: Busca uma organização pelo seu CNPJ.
     * @param cnpj O CNPJ da organização.
     * @return Optional de Organizacao.
     */
    @Transactional(readOnly = true)
    public Optional<Organizacao> buscarOrganizacaoPorCnpj(String cnpj) {
        logger.debug("Buscando organização com CNPJ: {}", cnpj);
        return organizacaoRepository.findByCnpj(cnpj);
    }

    /**
     * NOVO MÉTODO: Verifica se uma organização existe pelo CNPJ.
     * @param cnpj O CNPJ da organização.
     * @return true se existir, false caso contrário.
     */
    @Transactional(readOnly = true)
    public boolean existeOrganizacaoPorCnpj(String cnpj) {
        logger.debug("Verificando existência de organização com CNPJ: {}", cnpj);
        return organizacaoRepository.existsByCnpj(cnpj);
    }

    /**
     * Lista todas as organizações cadastradas.
     * @return Uma lista de DTOs de organizações.
     */
    @Transactional(readOnly = true)
    public List<OrganizacaoResponseDTO> listarTodasOrganizacoes() {
        logger.debug("Listando todas as organizações.");
        // Busca todas as entidades e mapeia cada uma para um DTO de resposta
        return organizacaoRepository.findAll().stream()
                .map(OrganizacaoResponseDTO::new) // Construtor de DTO que recebe a entidade
                .collect(Collectors.toList());
    }

    /**
     * Atualiza uma organização existente. Realiza validações de unicidade de CNPJ e e-mail (excluindo a própria ONG).
     * @param id O ID da organização a ser atualizada.
     * @param requestDTO Dados atualizados da organização.
     * @return DTO da organização atualizada.
     * @throws ResourceNotFoundException Se a organização não for encontrada.
     * @throws ConflictException Se o CNPJ ou e-mail de contato já existirem em outra organização.
     */
    @Transactional
    public OrganizacaoResponseDTO atualizarOrganizacao(Long id, OrganizacaoRequestDTO requestDTO) {
        logger.info("Tentando atualizar organização com ID: {}", id);
        // Busca a organização existente, lança exceção se não encontrar
        Organizacao organizacaoExistente = organizacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organização não encontrada para atualização com ID: " + id));

        // Validação de unicidade do CNPJ (garante que o CNPJ não pertence a outra ONG)
        organizacaoRepository.findByCnpj(requestDTO.getCnpj())
                .ifPresent(ong -> {
                    if (!ong.getId().equals(id)) { // Se o CNPJ já existe e pertence a OUTRA ONG
                        logger.warn("Atualização de organização ID {} falhou: CNPJ {} já existe em outra organização.", id, requestDTO.getCnpj());
                        throw new ConflictException("CNPJ já cadastrado para outra organização.");
                    }
                });

        // Validação de unicidade do E-mail de Contato (garante que o e-mail não pertence a outra ONG)
        organizacaoRepository.findByEmailContato(requestDTO.getEmailContato())
                .ifPresent(ong -> {
                    if (!ong.getId().equals(id)) { // Se o e-mail já existe e pertence a OUTRA ONG
                        logger.warn("Atualização de organização ID {} falhou: E-mail de contato {} já existe em outra organização.", id, requestDTO.getEmailContato());
                        throw new ConflictException("E-mail de contato da organização já cadastrado para outra organização.");
                    }
                });

        // Atualiza os campos da entidade existente com os dados do DTO de requisição
        organizacaoExistente.setNomeFantasia(requestDTO.getNomeFantasia());
        organizacaoExistente.setEmailContato(requestDTO.getEmailContato());
        organizacaoExistente.setCnpj(requestDTO.getCnpj());
        organizacaoExistente.setTelefoneContato(requestDTO.getTelefoneContato());
        organizacaoExistente.setEndereco(requestDTO.getEndereco());
        organizacaoExistente.setDescricao(requestDTO.getDescricao());
        organizacaoExistente.setWebsite(requestDTO.getWebsite());

        Organizacao updatedOrganizacao = organizacaoRepository.save(organizacaoExistente);
        logger.info("Organização com ID {} atualizada com sucesso.", id);
        return new OrganizacaoResponseDTO(updatedOrganizacao); // Mapeia a entidade atualizada para um DTO de resposta
    }

    /**
     * Deleta uma organização pelo seu ID.
     * @param id O ID da organização a ser deletada.
     * @throws ResourceNotFoundException Se a organização não for encontrada.
     */
    @Transactional
    public void deletarOrganizacao(Long id) {
        logger.info("Tentando deletar organização com ID: {}", id);
        // Verifica se a organização existe antes de tentar deletar
        if (!organizacaoRepository.existsById(id)) {
            logger.warn("Deleção de organização falhou: Organização não encontrada com ID: {}", id);
            throw new ResourceNotFoundException("Organização não encontrada para deleção com ID: " + id);
        }
        organizacaoRepository.deleteById(id);
        logger.info("Organização com ID {} deletada com sucesso.", id);
    }
}