import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';

class SolicitacaoAdocao {
  final String id;
  final String petId;
  final String petNome;
  final String petImagemUrl;
  final String ongId; // ID da ONG dona do pet
  final String adotanteId; // ID do adotante
  final String adotanteNome;
  final String adotanteEmail;
  final String adotanteTelefone;
  final String data;
  String status;

  SolicitacaoAdocao({
    required this.id,
    required this.petId,
    required this.petNome,
    required this.petImagemUrl,
    required this.ongId,
    required this.adotanteId,
    required this.adotanteNome,
    required this.adotanteEmail,
    required this.adotanteTelefone,
    required this.data,
    required this.status,
  });
}

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
  AdocaoCubit() : super(AdocaoInitial());

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

  final List<SolicitacaoAdocao> _solicitacoes = [
    SolicitacaoAdocao(
      id: '1',
      petId: '2',
      petNome: 'Mia',
      petImagemUrl:
          'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=400&auto=format&fit=crop',
      ongId: 'ong-id-123',
      adotanteId: 'adotante-id-123',
      adotanteNome: 'Adotante MyBuddy',
      adotanteEmail: 'user@mybuddy.com',
      adotanteTelefone: '(44) 99999-8888',
      data: '08/06/2026',
      status: 'Aprovado! Aguardando Retirada',
    ),
    SolicitacaoAdocao(
      id: '2',
      petId: '1',
      petNome: 'Pipoca',
      petImagemUrl:
          'https://images.unsplash.com/photo-1552053831-71594a27632d?q=80&w=400&auto=format&fit=crop',
      ongId: 'ong-id-123',
      adotanteId: 'adotante-id-123',
      adotanteNome: 'Adotante MyBuddy',
      adotanteEmail: 'user@mybuddy.com',
      adotanteTelefone: '(44) 99999-8888',
      data: '09/06/2026',
      status: 'Aguardando Entrevista',
    ),
  ];

  /// Registra a associação petId -> ongId quando um novo pet é cadastrado.
  /// Deve ser chamado pelo [CadastrarPetPage] após cadastro bem-sucedido.
  void registrarPetOng(String petId, String ongId) {
    _petOngMap[petId] = ongId;
  }

  Future<void> loadSolicitacoes() async {
    emit(AdocaoLoading());
    await Future.delayed(const Duration(milliseconds: 150));
    emit(AdocaoLoaded(List.from(_solicitacoes)));
  }

  /// Verifica se já existe solicitação pendente ou aprovada do adotante para este pet
  bool jaTemSolicitacaoAtiva(String petId, String adotanteId) {
    return _solicitacoes.any((s) =>
        s.petId == petId &&
        s.adotanteId == adotanteId &&
        !s.status.contains('Recusado'));
  }

  /// Retorna apenas as solicitações de adoção para a ONG específica
  List<SolicitacaoAdocao> getSolicitacoesPorOng(String ongId) {
    final lista = state is AdocaoLoaded
        ? (state as AdocaoLoaded).solicitacoes
        : _solicitacoes;
    return lista.where((s) => s.ongId == ongId).toList();
  }

  /// Retorna apenas as solicitações do adotante (por ID)
  List<SolicitacaoAdocao> getSolicitacoesPorAdotante(String adotanteId) {
    final lista = state is AdocaoLoaded
        ? (state as AdocaoLoaded).solicitacoes
        : _solicitacoes;
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

    await Future.delayed(const Duration(milliseconds: 400));
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

    _solicitacoes.add(newSolicitacao);
    await loadSolicitacoes();
    return true;
  }

  Future<bool> atualizarStatus(String id, String novoStatus) async {
    await Future.delayed(const Duration(milliseconds: 200));
    final index = _solicitacoes.indexWhere((s) => s.id == id);
    if (index != -1) {
      _solicitacoes[index].status = novoStatus;
      await loadSolicitacoes();
      return true;
    }
    return false;
  }
}
