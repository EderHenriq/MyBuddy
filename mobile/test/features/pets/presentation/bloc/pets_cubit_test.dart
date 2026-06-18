import 'package:dartz/dartz.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:mybuddy_app/features/pets/domain/entities/pet.dart';
import 'package:mybuddy_app/features/pets/domain/repositories/pets_repository.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/pets_cubit.dart';
import 'package:mybuddy_app/core/errors/failures.dart';

class MockPetsRepository extends Mock implements PetsRepository {}

void main() {
  late PetsCubit petsCubit;
  late MockPetsRepository mockPetsRepository;

  const testPet = Pet(
    id: '1',
    nome: 'Pipoca',
    especie: 'Cachorro',
    raca: 'Poodle',
    idade: 2,
    sexo: 'Fêmea',
    porte: 'Pequeno',
    cor: 'Branco',
    statusAdocao: 'DISPONIVEL',
    cidade: 'Maringá',
    estado: 'PR',
    imagemUrl: '',
  );

  setUpAll(() {
    registerFallbackValue(const Pet(
      id: '',
      nome: '',
      especie: '',
      raca: '',
      idade: 0,
      sexo: '',
      porte: '',
      cor: '',
      statusAdocao: '',
      cidade: '',
      estado: '',
      imagemUrl: '',
    ));
  });

  setUp(() {
    mockPetsRepository = MockPetsRepository();
    petsCubit = PetsCubit(petsRepository: mockPetsRepository);
  });

  tearDown(() {
    petsCubit.close();
  });

  group('PetsCubit', () {
    test('initial state should be PetsInitial', () {
      expect(petsCubit.state, isA<PetsInitial>());
    });

    group('loadPets', () {
      test('should emit [PetsLoading, PetsLoaded] when repository returns data', () async {
        // Arrange
        when(() => mockPetsRepository.getPets()).thenAnswer((_) async => const Right([testPet]));

        // Assert later
        final expectation = expectLater(
          petsCubit.stream,
          emitsInOrder([
            isA<PetsLoading>(),
            isA<PetsLoaded>(),
          ]),
        );

        // Act
        petsCubit.loadPets();

        await expectation;
      });

      test('should emit [PetsLoading, PetsError] when repository returns failure', () async {
        // Arrange
        when(() => mockPetsRepository.getPets()).thenAnswer((_) async => const Left(ServerFailure('Error')));

        // Assert later
        final expectation = expectLater(
          petsCubit.stream,
          emitsInOrder([
            isA<PetsLoading>(),
            isA<PetsError>(),
          ]),
        );

        // Act
        petsCubit.loadPets();

        await expectation;
      });
    });

    group('cadastrarPet', () {
      test('should return true and call loadPets when registration is successful', () async {
        // Arrange
        when(() => mockPetsRepository.cadastrarPet(any(), ongId: any(named: 'ongId')))
            .thenAnswer((_) async => const Right(testPet));
        when(() => mockPetsRepository.getPets()).thenAnswer((_) async => const Right([testPet]));

        // Act
        final result = await petsCubit.cadastrarPet(testPet, ongId: 'ong-123');

        // Assert
        expect(result, true);
        verify(() => mockPetsRepository.cadastrarPet(testPet, ongId: 'ong-123')).called(1);

        // Let background loadPets finish
        await Future.delayed(const Duration(milliseconds: 5));
      });

      test('should return false when registration fails', () async {
        // Arrange
        when(() => mockPetsRepository.cadastrarPet(any(), ongId: any(named: 'ongId')))
            .thenAnswer((_) async => const Left(ServerFailure('Failed')));

        // Act
        final result = await petsCubit.cadastrarPet(testPet, ongId: 'ong-123');

        // Assert
        expect(result, false);
      });
    });

    group('getPetsPorOng', () {
      test('should return list of pets when successful', () async {
        // Arrange
        when(() => mockPetsRepository.getPetsPorOng(any())).thenAnswer((_) async => const Right([testPet]));

        // Act
        final result = await petsCubit.getPetsPorOng('ong-123');

        // Assert
        expect(result, [testPet]);
        verify(() => mockPetsRepository.getPetsPorOng('ong-123')).called(1);
      });
    });
  });
}
