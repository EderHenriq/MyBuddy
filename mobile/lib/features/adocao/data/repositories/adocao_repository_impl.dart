import 'package:dartz/dartz.dart';
import 'package:dio/dio.dart';
import 'package:mybuddy_app/core/errors/failures.dart';
import 'package:mybuddy_app/features/adocao/domain/entities/solicitacao_adocao.dart';
import 'package:mybuddy_app/features/adocao/domain/repositories/adocao_repository.dart';

class AdocaoRepositoryImpl implements AdocaoRepository {
  final Dio _dio;

  AdocaoRepositoryImpl({required Dio dio}) : _dio = dio;

  @override
  Future<Either<Failure, List<SolicitacaoAdocao>>> getSolicitacoes() async {
    try {
      // Tenta buscar da API real
      final response = await _dio.get('/usuarios/me/interesses');
      
      final List<SolicitacaoAdocao> result = [];
      for (var item in response.data) {
        result.add(SolicitacaoAdocao(
          id: item['id'].toString(),
          petId: item['pet'] != null ? item['pet']['id'].toString() : '',
          petNome: item['pet'] != null ? item['pet']['nome'] : 'Pet',
          petImagemUrl: item['pet'] != null && item['pet']['imagemUrl'] != null 
              ? item['pet']['imagemUrl'] 
              : 'https://via.placeholder.com/150',
          ongId: item['pet'] != null && item['pet']['organizacao'] != null 
              ? item['pet']['organizacao']['id'].toString() : '',
          adotanteId: item['usuario'] != null ? item['usuario']['id'].toString() : '',
          adotanteNome: item['usuario'] != null ? item['usuario']['nome'] : '',
          adotanteEmail: item['usuario'] != null ? item['usuario']['email'] : '',
          adotanteTelefone: item['usuario'] != null ? item['usuario']['telefone'] ?? '' : '',
          data: item['criadoEm'] ?? DateTime.now().toIso8601String(),
          status: item['status'] ?? 'PENDENTE',
        ));
      }
      return Right(result);
    } catch (e) {
      // Se falhar (ex: backend offline), retornaremos erro para o mock ser acionado ou tratativa na UI
      return Left(ServerFailure(e.toString()));
    }
  }

  @override
  Future<Either<Failure, SolicitacaoAdocao>> solicitarAdocao(SolicitacaoAdocao solicitacao) async {
    try {
      final response = await _dio.post('/interesses', data: {
        'petId': int.tryParse(solicitacao.petId),
        'mensagem': 'Solicitação de adoção via App',
        'cpfAdotante': '000.000.000-00', // Mock de dados requeridos
        'idadeAdotante': 25,
        'motivoAdocao': 'Amor aos animais',
        'tipoResidencia': 'CASA',
        'possuiTelasProtecao': true,
        'outrosAnimais': 'Nao',
        'tempoSozinhoHoras': 4,
        'todosCientes': true,
        'espacoAdequado': true,
      });
      return Right(solicitacao.copyWith(
        id: response.data['id'].toString(),
        status: response.data['status'] ?? 'PENDENTE',
      ));
    } catch (e) {
      return Left(ServerFailure(e.toString()));
    }
  }

  @override
  Future<Either<Failure, SolicitacaoAdocao>> atualizarStatus(String id, String novoStatus) async {
    try {
      final response = await _dio.put('/interesses/$id/status', data: {
        'status': novoStatus,
      });
      return Right(SolicitacaoAdocao(
        id: response.data['id'].toString(),
        petId: response.data['pet'] != null ? response.data['pet']['id'].toString() : '',
        petNome: response.data['pet'] != null ? response.data['pet']['nome'] : 'Pet',
        petImagemUrl: response.data['pet'] != null && response.data['pet']['imagemUrl'] != null 
            ? response.data['pet']['imagemUrl'] 
            : 'https://via.placeholder.com/150',
        ongId: '',
        adotanteId: response.data['usuario'] != null ? response.data['usuario']['id'].toString() : '',
        adotanteNome: '',
        adotanteEmail: '',
        adotanteTelefone: '',
        data: response.data['atualizadoEm'] ?? '',
        status: response.data['status'] ?? novoStatus,
      ));
    } catch (e) {
      return Left(ServerFailure(e.toString()));
    }
  }
}
