import 'package:dartz/dartz.dart';
import 'package:mybuddy_app/core/errors/failures.dart';
import 'package:mybuddy_app/core/mock/mock_data.dart';
import 'package:mybuddy_app/features/adocao/domain/entities/solicitacao_adocao.dart';
import 'package:mybuddy_app/features/adocao/domain/repositories/adocao_repository.dart';

class AdocaoRepositoryMock implements AdocaoRepository {
  final List<SolicitacaoAdocao> _solicitacoes = List.from(MockData.solicitacoes);

  @override
  Future<Either<Failure, List<SolicitacaoAdocao>>> getSolicitacoes() async {
    await Future.delayed(const Duration(milliseconds: 200));
    return Right(List.from(_solicitacoes));
  }

  @override
  Future<Either<Failure, SolicitacaoAdocao>> solicitarAdocao(SolicitacaoAdocao solicitacao) async {
    await Future.delayed(const Duration(milliseconds: 300));
    _solicitacoes.add(solicitacao);
    return Right(solicitacao);
  }

  @override
  Future<Either<Failure, SolicitacaoAdocao>> atualizarStatus(String id, String novoStatus) async {
    await Future.delayed(const Duration(milliseconds: 200));
    final index = _solicitacoes.indexWhere((s) => s.id == id);
    if (index != -1) {
      final updated = _solicitacoes[index].copyWith(status: novoStatus);
      _solicitacoes[index] = updated;
      return Right(updated);
    }
    return const Left(ServerFailure('Solicitação não encontrada para atualizar status'));
  }
}
