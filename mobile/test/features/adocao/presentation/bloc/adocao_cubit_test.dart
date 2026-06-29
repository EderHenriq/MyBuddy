import 'package:dartz/dartz.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:mybuddy_app/features/adocao/domain/entities/solicitacao_adocao.dart';
import 'package:mybuddy_app/features/adocao/domain/repositories/adocao_repository.dart';
import 'package:mybuddy_app/features/adocao/presentation/bloc/adocao_cubit.dart';

class MockAdocaoRepository extends Mock implements AdocaoRepository {}

void main() {
  late AdocaoCubit adocaoCubit;
  late MockAdocaoRepository mockAdocaoRepository;

  const testSolicitacao = SolicitacaoAdocao(
    id: '1',
    petId: '101',
    petNome: 'Pipoca',
    petImagemUrl: '',
    ongId: 'ong-id-123',
    adotanteId: 'client-1',
    adotanteNome: 'Eder',
    adotanteEmail: 'eder@gmail.com',
    adotanteTelefone: '4499999999',
    data: '18/06/2026',
    status: 'Aguardando Entrevista',
  );

  setUpAll(() {
    registerFallbackValue(const SolicitacaoAdocao(
      id: '',
      petId: '',
      petNome: '',
      petImagemUrl: '',
      ongId: '',
      adotanteId: '',
      adotanteNome: '',
      adotanteEmail: '',
      adotanteTelefone: '',
      data: '',
      status: '',
    ));
  });

  setUp(() {
    mockAdocaoRepository = MockAdocaoRepository();
    adocaoCubit = AdocaoCubit(adocaoRepository: mockAdocaoRepository);
  });

  tearDown(() {
    adocaoCubit.close();
  });

  group('AdocaoCubit', () {
    test('initial state should be AdocaoInitial', () {
      expect(adocaoCubit.state, isA<AdocaoInitial>());
    });

    group('loadSolicitacoes', () {
      test('should emit [AdocaoLoading, AdocaoLoaded] when repository returns data', () async {
        // Arrange
        when(() => mockAdocaoRepository.getSolicitacoes()).thenAnswer((_) async => const Right([testSolicitacao]));

        // Assert later
        final expectation = expectLater(
          adocaoCubit.stream,
          emitsInOrder([
            isA<AdocaoLoading>(),
            isA<AdocaoLoaded>(),
          ]),
        );

        // Act
        adocaoCubit.loadSolicitacoes();

        await expectation;
      });
    });

    group('solicitarAdocao', () {
      test('should return true and load solicitacoes when successful', () async {
        // Arrange
        when(() => mockAdocaoRepository.solicitarAdocao(any())).thenAnswer((_) async => const Right(testSolicitacao));
        when(() => mockAdocaoRepository.getSolicitacoes()).thenAnswer((_) async => const Right([testSolicitacao]));

        // Act
        final result = await adocaoCubit.solicitarAdocao(
          petId: '101',
          petNome: 'Pipoca',
          petImagemUrl: '',
          clienteId: 'client-1',
          clienteNome: 'Eder',
          clienteEmail: 'eder@gmail.com',
          clienteTelefone: '4499999999',
        );

        // Assert
        expect(result, true);
        verify(() => mockAdocaoRepository.solicitarAdocao(any())).called(1);
      });

      test('should return false if there is already an active request', () async {
        // Arrange
        // Simulate Loaded state with an active request for pet 101 by adopter client-1
        when(() => mockAdocaoRepository.getSolicitacoes()).thenAnswer((_) async => const Right([testSolicitacao]));
        
        // Assert loaded state first
        final expectation = expectLater(
          adocaoCubit.stream,
          emitsInOrder([
            isA<AdocaoLoading>(),
            isA<AdocaoLoaded>(),
          ]),
        );
        adocaoCubit.loadSolicitacoes();
        await expectation;

        // Act
        final result = await adocaoCubit.solicitarAdocao(
          petId: '101',
          petNome: 'Pipoca',
          petImagemUrl: '',
          clienteId: 'client-1',
          clienteNome: 'Eder',
          clienteEmail: 'eder@gmail.com',
          clienteTelefone: '4499999999',
        );

        // Assert
        expect(result, false);
      });
    });

    group('atualizarStatus', () {
      test('should return true and reload when successful', () async {
        // Arrange
        when(() => mockAdocaoRepository.atualizarStatus(any(), any())).thenAnswer((_) async => const Right(testSolicitacao));
        when(() => mockAdocaoRepository.getSolicitacoes()).thenAnswer((_) async => const Right([testSolicitacao]));

        // Act
        final result = await adocaoCubit.atualizarStatus('1', 'Aprovado');

        // Assert
        expect(result, true);
        verify(() => mockAdocaoRepository.atualizarStatus('1', 'Aprovado')).called(1);
      });
    });
  });
}
