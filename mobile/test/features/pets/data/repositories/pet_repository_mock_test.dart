import 'package:dartz/dartz.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mybuddy_app/core/errors/failures.dart';
import 'package:mybuddy_app/features/pets/data/repositories/pet_repository_mock.dart';
import 'package:mybuddy_app/features/pets/domain/entities/pet.dart';

void main() {
  late PetRepositoryMock repository;

  setUp(() {
    repository = PetRepositoryMock();
  });

  group('PetRepositoryMock', () {
    group('getPets', () {
      test('deve retornar lista de pets com sucesso', () async {
        final result = await repository.getPets();

        expect(result.isRight(), true);
        result.fold(
              (failure) => fail('Esperava sucesso'),
              (pets) => expect(pets.length, 2),
        );
      });

      test('deve filtrar pets por especie', () async {
        final result = await repository.getPets(especie: 'CAO');

        result.fold(
              (failure) => fail('Esperava sucesso'),
              (pets) {
            expect(pets.length, 1);
            expect(pets.first.nome, 'Rex');
          },
        );
      });

      test('deve filtrar pets por porte', () async {
        final result = await repository.getPets(porte: 'PEQUENO');

        result.fold(
              (failure) => fail('Esperava sucesso'),
              (pets) {
            expect(pets.length, 1);
            expect(pets.first.nome, 'Mia');
          },
        );
      });

      test('deve filtrar pets por cidade', () async {
        final result = await repository.getPets(cidade: 'São Paulo');

        result.fold(
              (failure) => fail('Esperava sucesso'),
              (pets) {
            expect(pets.length, 1);
            expect(pets.first.nome, 'Rex');
          },
        );
      });

      test('deve filtrar pets por estado', () async {
        final result = await repository.getPets(estado: 'SP');

        result.fold(
              (failure) => fail('Esperava sucesso'),
              (pets) => expect(pets.length, 2),
        );
      });

      test('deve retornar lista vazia quando nenhum pet corresponde ao filtro',
              () async {
            final result = await repository.getPets(especie: 'OUTRO');

            result.fold(
                  (failure) => fail('Esperava sucesso'),
                  (pets) => expect(pets.isEmpty, true),
            );
          });
    });

    group('getPetById', () {
      test('deve retornar pet por id com sucesso', () async {
        final result = await repository.getPetById('1');

        result.fold(
              (failure) => fail('Esperava sucesso'),
              (pet) {
            expect(pet.id, '1');
            expect(pet.nome, 'Rex');
            expect(pet.especie, 'CAO');
          },
        );
      });

      test('deve retornar ServerFailure quando pet não encontrado', () async {
        final result = await repository.getPetById('999');

        result.fold(
              (failure) {
            expect(failure, isA<ServerFailure>());
            expect(failure.message, 'Pet não encontrado');
          },
              (pet) => fail('Esperava falha'),
        );
      });
    });

    group('getPetsByOrganizacao', () {
      test('deve retornar pets da organização com sucesso', () async {
        final result = await repository.getPetsByOrganizacao('1');

        result.fold(
              (failure) => fail('Esperava sucesso'),
              (pets) {
            expect(pets.length, 1);
            expect(pets.first.nomeOrganizacao, 'ONG Patinhas');
          },
        );
      });

      test('deve retornar lista vazia para organização sem pets', () async {
        final result = await repository.getPetsByOrganizacao('999');

        result.fold(
              (failure) => fail('Esperava sucesso'),
              (pets) => expect(pets.isEmpty, true),
        );
      });
    });

    group('Pet entity', () {
      test('disponivel deve ser true quando statusAdocao é DISPONIVEL', () {
        const pet = Pet(
          id: '1',
          nome: 'Rex',
          especie: 'CAO',
          raca: 'Labrador',
          idade: 2,
          porte: 'GRANDE',
          cor: 'Amarelo',
          sexo: 'M',
          fotosUrls: [],
          statusAdocao: 'DISPONIVEL',
          microchipado: false,
          vacinado: true,
          castrado: true,
        );

        expect(pet.disponivel, true);
      });

      test('disponivel deve ser false quando statusAdocao é ADOTADO', () {
        const pet = Pet(
          id: '1',
          nome: 'Rex',
          especie: 'CAO',
          raca: 'Labrador',
          idade: 2,
          porte: 'GRANDE',
          cor: 'Amarelo',
          sexo: 'M',
          fotosUrls: [],
          statusAdocao: 'ADOTADO',
          microchipado: false,
          vacinado: true,
          castrado: true,
        );

        expect(pet.disponivel, false);
      });
    });
  });
}