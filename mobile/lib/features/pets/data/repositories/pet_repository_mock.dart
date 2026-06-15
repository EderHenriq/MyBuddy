import 'package:dartz/dartz.dart';
import 'package:mybuddy_app/core/errors/failures.dart';
import 'package:mybuddy_app/features/pets/domain/entities/pet.dart';
import 'package:mybuddy_app/features/pets/domain/repositories/pet_repository.dart';

class PetRepositoryMock implements PetRepository {
  final List<Pet> _pets = const [
    Pet(
      id: '1',
      nome: 'Rex',
      especie: 'CAO',
      raca: 'Labrador',
      idade: 2,
      porte: 'GRANDE',
      cor: 'Amarelo',
      sexo: 'M',
      fotosUrls: [],
      imagemUrl: '',
      statusAdocao: 'DISPONIVEL',
      nomeOrganizacao: 'ONG Patinhas',
      organizacaoId: '1',
      microchipado: false,
      vacinado: true,
      castrado: true,
      cidade: 'São Paulo',
      estado: 'SP',
    ),
    Pet(
      id: '2',
      nome: 'Mia',
      especie: 'GATO',
      raca: 'Siamês',
      idade: 3,
      porte: 'PEQUENO',
      cor: 'Branco',
      sexo: 'F',
      fotosUrls: [],
      imagemUrl: '',
      statusAdocao: 'DISPONIVEL',
      nomeOrganizacao: 'ONG Amigos dos Bichos',
      organizacaoId: '2',
      microchipado: false,
      vacinado: true,
      castrado: false,
      cidade: 'Campinas',
      estado: 'SP',
    ),
  ];

  @override
  Future<Either<Failure, List<Pet>>> getPets({
    String? especie,
    String? porte,
    String? cidade,
    String? estado,
    int page = 0,
    int size = 10,
  }) async {
    await Future.delayed(const Duration(milliseconds: 500));

    var result = _pets.where((pet) {
      if (especie != null && pet.especie != especie) return false;
      if (porte != null && pet.porte != porte) return false;
      if (cidade != null && pet.cidade != cidade) return false;
      if (estado != null && pet.estado != estado) return false;
      return true;
    }).toList();

    return Right(result);
  }

  @override
  Future<Either<Failure, Pet>> getPetById(String id) async {
    await Future.delayed(const Duration(milliseconds: 300));

    final pet = _pets.where((p) => p.id == id).firstOrNull;
    if (pet == null) {
      return const Left(ServerFailure('Pet não encontrado'));
    }
    return Right(pet);
  }

  @override
  Future<Either<Failure, List<Pet>>> getPetsByOrganizacao(
      String organizacaoId) async {
    await Future.delayed(const Duration(milliseconds: 300));

    final pets =
    _pets.where((p) => p.organizacaoId == organizacaoId).toList();
    return Right(pets);
  }
}