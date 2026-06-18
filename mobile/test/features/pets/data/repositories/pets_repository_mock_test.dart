import 'package:flutter_test/flutter_test.dart';
import 'package:mybuddy_app/features/pets/data/repositories/pets_repository_mock.dart';
import 'package:mybuddy_app/features/pets/domain/entities/pet.dart';

void main() {
  late PetsRepositoryMock repository;

  setUp(() {
    repository = PetsRepositoryMock();
  });

  group('PetsRepositoryMock', () {
    group('getPets', () {
      test('deve retornar lista de pets com sucesso', () async {
        final result = await repository.getPets();

        expect(result.isRight(), true);
        result.fold(
          (failure) => fail('Esperava sucesso'),
          (pets) => expect(pets.isNotEmpty, true),
        );
      });

      test('deve filtrar pets por especie', () async {
        final result = await repository.getPets(especie: 'Cachorro');

        result.fold(
          (failure) => fail('Esperava sucesso'),
          (pets) {
            expect(pets.every((p) => p.especie.toLowerCase() == 'cachorro'), true);
          },
        );
      });

      test('deve retornar pet por id com sucesso', () async {
        final result = await repository.getPetById('1');

        result.fold(
          (failure) => fail('Esperava sucesso'),
          (pet) {
            expect(pet.id, '1');
            expect(pet.nome, 'Pipoca');
          },
        );
      });
    });

    group('cadastrarPet', () {
      test('deve cadastrar pet com sucesso', () async {
        const pet = Pet(
          id: '',
          nome: 'Bethoven',
          especie: 'Cachorro',
          raca: 'São Bernardo',
          idade: 3,
          sexo: 'Macho',
          porte: 'Grande',
          cor: 'Marrom/Branco',
          statusAdocao: 'DISPONIVEL',
          cidade: 'Maringá',
          estado: 'PR',
          imagemUrl: '',
        );

        final result = await repository.cadastrarPet(pet);

        expect(result.isRight(), true);
        result.fold(
          (failure) => fail('Esperava sucesso'),
          (newPet) {
            expect(newPet.id.isNotEmpty, true);
            expect(newPet.nome, 'Bethoven');
          },
        );
      });
    });
  });
}
