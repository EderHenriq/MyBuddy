package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.DTO.DadosUsuarioExportDTO;
import com.Mybuddy.Myb.Exception.ConflictException;
import com.Mybuddy.Myb.Exception.ResourceNotFoundException;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Model.Pedido;
import com.Mybuddy.Myb.Model.EnderecoEntrega;
import com.Mybuddy.Myb.Model.InteresseAdocao;
import com.Mybuddy.Myb.Repository.mongo.UsuarioRepository;
import com.Mybuddy.Myb.Repository.jpa.PedidoRepository;
import com.Mybuddy.Myb.Repository.mongo.InteresseAdocaoRepository;
import com.Mybuddy.Myb.Repository.jpa.AgendamentoRepository;
import com.Mybuddy.Myb.Repository.jpa.DonationSubscriptionRepository;
import com.Mybuddy.Myb.Model.DonationSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class UsuarioService {

    public static final String KeycloakUserSyncService = null;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final InteresseAdocaoRepository interesseAdocaoRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final DonationSubscriptionRepository donationSubscriptionRepository;

    public Usuario criarUsuario(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new ConflictException("O e-mail informado já está em uso.");
        }
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> buscarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario atualizarUsuario(long id, Usuario dadosUsuario) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com ID " + id + " não encontrado."));

        usuarioExistente.setNome(dadosUsuario.getNome());
        usuarioExistente.setEmail(dadosUsuario.getEmail());
        usuarioExistente.setTelefone(dadosUsuario.getTelefone());
        usuarioExistente.setUrlAvatar(dadosUsuario.getUrlAvatar());

        return usuarioRepository.save(usuarioExistente);
    }

    @Transactional(readOnly = true)
    public DadosUsuarioExportDTO exportarDadosUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com ID " + id + " não encontrado."));

        List<DadosUsuarioExportDTO.InteresseExportDTO> interesses = interesseAdocaoRepository.findByUsuarioId(id)
                .stream()
                .map(i -> new DadosUsuarioExportDTO.InteresseExportDTO(
                        i.getId(),
                        i.getPet() != null ? i.getPet().getId() : null,
                        i.getStatus() != null ? i.getStatus().name() : null,
                        i.getCriadoEm()
                ))
                .toList();

        List<DadosUsuarioExportDTO.PedidoExportDTO> pedidos = pedidoRepository.findByClienteId(id)
                .stream()
                .map(p -> new DadosUsuarioExportDTO.PedidoExportDTO(
                        p.getId(),
                        p.getStatus() != null ? p.getStatus().name() : null,
                        p.getDataCriacao()
                ))
                .toList();

        return new DadosUsuarioExportDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getDataCriacao(),
                interesses,
                pedidos
        );
    }

    @Transactional
    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário com ID " + id + " não encontrado.");
        }

        // 0.1 Verificar se existem agendamentos ativos no PostgreSQL (tudo exceto cancelados)
        if (agendamentoRepository.existsByClienteIdAndStatusNot(id, com.Mybuddy.Myb.Model.StatusAgendamento.CANCELADO)) {
            throw new ConflictException("Não é possível deletar o usuário pois existem agendamentos ativos vinculados a ele.");
        }

        // 0.2 Buscar assinaturas de doações ativas, tentar cancelar no Mercado Pago e remover localmente
        List<DonationSubscription> assinaturas = donationSubscriptionRepository.findByUsuarioId(id);
        for (DonationSubscription assinatura : assinaturas) {
            if (!"cancelled".equals(assinatura.getStatus())) {
                try {
                    com.mercadopago.client.preapproval.PreapprovalClient client = new com.mercadopago.client.preapproval.PreapprovalClient();
                    com.mercadopago.client.preapproval.PreapprovalUpdateRequest cancelRequest = 
                            com.mercadopago.client.preapproval.PreapprovalUpdateRequest.builder()
                                    .status("cancelled")
                                    .build();
                    client.update(assinatura.getMpPreapprovalId(), cancelRequest);
                } catch (Exception e) {
                    org.slf4j.LoggerFactory.getLogger(UsuarioService.class)
                            .error("Erro ao cancelar assinatura no Mercado Pago: " + assinatura.getMpPreapprovalId(), e);
                }
            }
            donationSubscriptionRepository.delete(assinatura);
        }

        // 1. Anonimizar dados pessoais nos pedidos históricos do usuário
        List<Pedido> pedidos = pedidoRepository.findByClienteId(id);
        for (Pedido pedido : pedidos) {
            EnderecoEntrega endereco = pedido.getEnderecoEntrega();
            if (endereco != null) {
                endereco.setLogradouro("ANONIMIZADO");
                endereco.setNumero("0");
                endereco.setComplemento(null);
                endereco.setBairro("ANONIMIZADO");
                endereco.setCep("00000000");
                endereco.setCidade("ANONIMIZADO");
                endereco.setEstado("AN");
                endereco.setLatitude(null);
                endereco.setLongitude(null);
            }
            pedidoRepository.save(pedido);
        }

        // 2. Anonimizar dados pessoais nas triagens de adoção
        List<InteresseAdocao> interesses = interesseAdocaoRepository.findByUsuarioId(id);
        for (InteresseAdocao interesse : interesses) {
            interesse.setCpfAdotante(null);
            interesse.setIdadeAdotante(null);
            interesse.setMensagem(null);
            interesse.setMotivoAdocao(null);
            interesse.setTipoResidencia(null);
            interesse.setPossuiTelasProtecao(null);
            interesse.setOutrosAnimais(null);
            interesse.setTempoSozinhoHoras(null);
            interesse.setTodosCientes(null);
            interesse.setEspacoAdequado(null);
            interesse.setUsuario(null);
            interesseAdocaoRepository.save(interesse);
        }

        // 3. Deletar o usuário do banco de dados (MongoDB)
        usuarioRepository.deleteById(id);
    }
}