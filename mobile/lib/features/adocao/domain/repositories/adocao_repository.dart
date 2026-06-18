import 'package:dartz/dartz.dart';
import 'package:mybuddy_app/core/errors/failures.dart';
import 'package:mybuddy_app/features/adocao/domain/entities/solicitacao_adocao.dart';

abstract class AdocaoRepository {
  Future<Either<Failure, List<SolicitacaoAdocao>>> getSolicitacoes();
  Future<Either<Failure, SolicitacaoAdocao>> solicitarAdocao(SolicitacaoAdocao solicitacao);
  Future<Either<Failure, SolicitacaoAdocao>> atualizarStatus(String id, String novoStatus);
}
