package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.OrganizacaoRequestDTO;
import com.Mybuddy.Myb.DTO.OrganizacaoResponseDTO;
import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.Organizacao;
import com.Mybuddy.Myb.Repository.mongo.OrganizacaoRepository;
import com.Mybuddy.Myb.Repository.mongo.PetRepository;
import com.Mybuddy.Myb.Repository.mongo.EventoOngRepository;
import com.Mybuddy.Myb.Repository.jpa.CampanhaDoacaoRepository;
import com.Mybuddy.Myb.Repository.jpa.PaymentRepository;
import com.Mybuddy.Myb.Repository.jpa.DonationSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class OrganizacaoService {

    private static final Logger log = LoggerFactory.getLogger(OrganizacaoService.class);

    private final OrganizacaoRepository organizacaoRepository;
    private final PetRepository petRepository;
    private final EventoOngRepository eventoOngRepository;
    private final CampanhaDoacaoRepository campanhaDoacaoRepository;
    private final PaymentRepository paymentRepository;
    private final DonationSubscriptionRepository donationSubscriptionRepository;

    @Transactional
    public Organizacao criarOrganizacao(Organizacao organizacao) {
        log.info("Tentando criar nova organização (via entidade): {}", organizacao.getNomeFantasia());

        if (organizacaoRepository.existsByCnpj(organizacao.getCnpj())) {
            log.warn("Criação de organização falhou: CNPJ {} já existe.", organizacao.getCnpj());
            throw new ConflictException("CNPJ já cadastrado.");
        }

        if (organizacaoRepository.existsByEmailContato(organizacao.getEmailContato())) {
            log.warn("Criação de organização falhou: E-mail de contato {} já existe.", organizacao.getEmailContato());
            throw new ConflictException("E-mail de contato da organização já cadastrado.");
        }

        Organizacao savedOrganizacao = organizacaoRepository.save(organizacao);
        log.info("Organização criada com sucesso com ID: {}", savedOrganizacao.getId());
        return savedOrganizacao;
    }

    @Transactional
    public OrganizacaoResponseDTO criarOrganizacao(OrganizacaoRequestDTO requestDTO) {
        log.info("Tentando criar nova organização (via DTO): {}", requestDTO.getNomeFantasia());

        if (organizacaoRepository.existsByCnpj(requestDTO.getCnpj())) {
            log.warn("Criação de organização falhou: CNPJ {} já existe.", requestDTO.getCnpj());
            throw new ConflictException("CNPJ já cadastrado.");
        }

        if (organizacaoRepository.existsByEmailContato(requestDTO.getEmailContato())) {
            log.warn("Criação de organização falhou: E-mail de contato {} já existe.", requestDTO.getEmailContato());
            throw new ConflictException("E-mail de contato da organização já cadastrado.");
        }

        Organizacao organizacao = new Organizacao();
        organizacao.setNomeFantasia(requestDTO.getNomeFantasia());
        organizacao.setEmailContato(requestDTO.getEmailContato());
        organizacao.setCnpj(requestDTO.getCnpj());
        organizacao.setTelefoneContato(requestDTO.getTelefoneContato());
        organizacao.setEndereco(requestDTO.getEndereco());
        organizacao.setDescricao(requestDTO.getDescricao());
        organizacao.setWebsite(requestDTO.getWebsite());
        Organizacao savedOrganizacao = organizacaoRepository.save(organizacao);
        log.info("Organização criada com sucesso com ID: {}", savedOrganizacao.getId());
        return new OrganizacaoResponseDTO(savedOrganizacao);
    }

    @Transactional(readOnly = true)
    public OrganizacaoResponseDTO buscarOrganizacaoPorId(Long id) {
        log.debug("Buscando organização com ID: {}", id);
        Organizacao organizacao = organizacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organização não encontrada com ID: " + id));
        return new OrganizacaoResponseDTO(organizacao);
    }

    @Transactional(readOnly = true)
    public Optional<Organizacao> buscarOrganizacaoPorCnpj(String cnpj) {
        log.debug("Buscando organização com CNPJ: {}", cnpj);
        return organizacaoRepository.findByCnpj(cnpj);
    }

    @Transactional(readOnly = true)
    public boolean existeOrganizacaoPorCnpj(String cnpj) {
        log.debug("Verificando existência de organização com CNPJ: {}", cnpj);
        return organizacaoRepository.existsByCnpj(cnpj);
    }

    @Transactional(readOnly = true)
    public List<OrganizacaoResponseDTO> listarTodasOrganizacoes() {
        log.debug("Listando todas as organizações.");
        return organizacaoRepository.findAll().stream()
                .map(OrganizacaoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrganizacaoResponseDTO atualizarOrganizacao(Long id, OrganizacaoRequestDTO requestDTO) {
        log.info("Tentando atualizar organização com ID: {}", id);
        Organizacao organizacaoExistente = organizacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organização não encontrada para atualização com ID: " + id));

        organizacaoRepository.findByCnpj(requestDTO.getCnpj())
                .ifPresent(ong -> {
                    if (!ong.getId().equals(id)) {
                        log.warn("Atualização de organização ID {} falhou: CNPJ {} já existe em outra organização.", id, requestDTO.getCnpj());
                        throw new ConflictException("CNPJ já cadastrado para outra organização.");
                    }
                });

        organizacaoRepository.findByEmailContato(requestDTO.getEmailContato())
                .ifPresent(ong -> {
                    if (!ong.getId().equals(id)) {
                        log.warn("Atualização de organização ID {} falhou: E-mail de contato {} já existe em outra organização.", id, requestDTO.getEmailContato());
                        throw new ConflictException("E-mail de contato da organização já cadastrado para outra organização.");
                    }
                });

        organizacaoExistente.setNomeFantasia(requestDTO.getNomeFantasia());
        organizacaoExistente.setEmailContato(requestDTO.getEmailContato());
        organizacaoExistente.setCnpj(requestDTO.getCnpj());
        organizacaoExistente.setTelefoneContato(requestDTO.getTelefoneContato());
        organizacaoExistente.setEndereco(requestDTO.getEndereco());
        organizacaoExistente.setDescricao(requestDTO.getDescricao());
        organizacaoExistente.setWebsite(requestDTO.getWebsite());

        Organizacao updatedOrganizacao = organizacaoRepository.save(organizacaoExistente);
        log.info("Organização com ID {} atualizada com sucesso.", id);
        return new OrganizacaoResponseDTO(updatedOrganizacao);
    }

    @Transactional
    public void deletarOrganizacao(Long id) {
        log.info("Tentando deletar organização com ID: {}", id);
        if (!organizacaoRepository.existsById(id)) {
            log.warn("Deleção de organização falhou: Organização não encontrada com ID: {}", id);
            throw new ResourceNotFoundException("Organização não encontrada para deleção com ID: " + id);
        }

        // 1. Verificar se existem Pets vinculados
        if (!petRepository.findByOrganizacaoId(id).isEmpty()) {
            log.warn("Deleção de organização ID {} falhou: existem pets vinculados.", id);
            throw new ConflictException("Não é possível deletar a organização pois existem pets vinculados a ela.");
        }

        // 2. Verificar se existem EventoOng vinculados
        if (!eventoOngRepository.findByOrganizacaoId(id).isEmpty()) {
            log.warn("Deleção de organização ID {} falhou: existem eventos vinculados.", id);
            throw new ConflictException("Não é possível deletar a organização pois existem eventos vinculados a ela.");
        }

        // 3. Verificar se existem CampanhasDoacao vinculadas
        if (campanhaDoacaoRepository.existsByOrganizacaoId(id)) {
            log.warn("Deleção de organização ID {} falhou: existem campanhas de doação vinculadas.", id);
            throw new ConflictException("Não é possível deletar a organização pois existem campanhas de doação vinculadas a ela.");
        }

        // 4. Verificar se existem assinaturas de doação ativas (tudo exceto canceladas)
        if (donationSubscriptionRepository.existsByOrganizacaoIdAndStatusNot(id, "cancelled")) {
            log.warn("Deleção de organização ID {} falhou: existem assinaturas de doação ativas.", id);
            throw new ConflictException("Não é possível deletar a organização pois existem assinaturas de doação ativas vinculadas a ela.");
        }

        // 5. Nullificar payments.organizacao_id
        paymentRepository.nullifyOrganizacaoId(id);

        organizacaoRepository.deleteById(id);
        log.info("Organização com ID {} deletada com sucesso.", id);
    }
}