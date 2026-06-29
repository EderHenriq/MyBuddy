import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';
import 'package:mybuddy_app/features/adocao/domain/entities/solicitacao_adocao.dart';
import 'package:mybuddy_app/features/adocao/domain/repositories/adocao_repository.dart';

abstract class AdocaoState extends Equatable {
  const AdocaoState();
  @override
  List<Object?> get props => [];
}

class AdocaoInitial extends AdocaoState {}

class AdocaoLoading extends AdocaoState {}

class AdocaoLoaded extends AdocaoState {
  final List<SolicitacaoAdocao> solicitacoes;
  const AdocaoLoaded(this.solicitacoes);
  @override
  List<Object?> get props => [solicitacoes];
}

class AdocaoCubit extends Cubit<AdocaoState> {
  final AdocaoRepository adocaoRepository;

  AdocaoCubit({required this.adocaoRepository}) : super(AdocaoInitial());

  /// Mapa dinâmico: petId -> ongId
  /// Inicializado com os pets mock; atualizado quando novos pets são cadastrados
  final Map<String, String> _petOngMap = {
    '1': 'ong-id-123',
    '2': 'ong-id-123',
    '3': 'outra-ong',
    '4': 'outra-ong',
    '5': 'ong-id-123',
    '6': 'outra-ong',
  };

  /// Registra a associação petId -> ongId quando um novo pet é cadastrado.
  /// Deve ser chamado pelo [CadastrarPetPage] após cadastro bem-sucedido.
  void registrarPetOng(String petId, String ongId) {
    _petOngMap[petId] = ongId;
  }

  Future<void> loadSolicitacoes() async {
    emit(AdocaoLoading());
    final result = await adocaoRepository.getSolicitacoes();
    result.fold(
      (failure) => emit(const AdocaoLoaded([])),
      (solicitacoes) => emit(AdocaoLoaded(solicitacoes)),
    );
  }

  /// Verifica se já existe solicitação pendente ou aprovada do adotante para este pet
  bool jaTemSolicitacaoAtiva(String petId, String adotanteId) {
    if (state is AdocaoLoaded) {
      return (state as AdocaoLoaded).solicitacoes.any((s) =>
          s.petId == petId &&
          s.adotanteId == adotanteId &&
          !s.status.contains('Recusado'));
    }
    return false;
  }

  /// Retorna apenas as solicitações de adoção para a ONG específica
  List<SolicitacaoAdocao> getSolicitacoesPorOng(String ongId) {
    final lista = state is AdocaoLoaded
        ? (state as AdocaoLoaded).solicitacoes
        : <SolicitacaoAdocao>[];
    return lista.where((s) => s.ongId == ongId).toList();
  }

  /// Retorna apenas as solicitações do adotante (por ID)
  List<SolicitacaoAdocao> getSolicitacoesPorAdotante(String adotanteId) {
    final lista = state is AdocaoLoaded
        ? (state as AdocaoLoaded).solicitacoes
        : <SolicitacaoAdocao>[];
    return lista.where((s) => s.adotanteId == adotanteId).toList();
  }

  Future<bool> solicitarAdocao({
    required String petId,
    required String petNome,
    required String petImagemUrl,
    required String clienteId,
    required String clienteNome,
    required String clienteEmail,
    required String clienteTelefone,
  }) async {
    // Verifica duplicata antes de prosseguir
    if (jaTemSolicitacaoAtiva(petId, clienteId)) {
      return false; // Caller deve tratar como duplicata
    }

    final now = DateTime.now();
    final dataStr =
        '${now.day.toString().padLeft(2, '0')}/${now.month.toString().padLeft(2, '0')}/${now.year}';

    // Identifica a ONG dona do pet pelo mapa dinâmico; fallback para 'ong-id-123'
    final ongId = _petOngMap[petId] ?? 'ong-id-123';

    final newSolicitacao = SolicitacaoAdocao(
      id: (DateTime.now().millisecondsSinceEpoch).toString(),
      petId: petId,
      petNome: petNome,
      petImagemUrl: petImagemUrl,
      ongId: ongId,
      adotanteId: clienteId,
      adotanteNome: clienteNome,
      adotanteEmail: clienteEmail,
      adotanteTelefone: clienteTelefone,
      data: dataStr,
      status: 'Aguardando Entrevista',
    );

    final result = await adocaoRepository.solicitarAdocao(newSolicitacao);
    return result.fold(
      (failure) => false,
      (_) async {
        await loadSolicitacoes();
        return true;
      },
    );
  }

  Future<bool> atualizarStatus(String id, String novoStatus) async {
    final result = await adocaoRepository.atualizarStatus(id, novoStatus);
    return result.fold(
      (failure) => false,
      (_) async {
        await loadSolicitacoes();
        return true;
      },
    );
  }
}
