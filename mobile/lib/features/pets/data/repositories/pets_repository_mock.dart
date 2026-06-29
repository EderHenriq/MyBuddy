import 'package:dartz/dartz.dart';
import 'package:mybuddy_app/core/errors/failures.dart';
import 'package:mybuddy_app/core/mock/mock_data.dart';
import 'package:mybuddy_app/features/pets/domain/entities/pet.dart';
import 'package:mybuddy_app/features/pets/domain/repositories/pets_repository.dart';

class PetsRepositoryMock implements PetsRepository {
  final List<Pet> _pets = List.from(MockData.pets);

  // Armazena quem cadastrou qual pet.
  // Mapeia petId -> ongId.
  final Map<String, String> _petOngMap = {
    '1': 'ong-id-123',
    '2': 'ong-id-123',
    '3': 'outra-ong',
    '4': 'outra-ong',
    '5': 'ong-id-123',
    '6': 'outra-ong',
  };

  @override
  Future<Either<Failure, List<Pet>>> getPets({
    String? especie,
    String? porte,
    String? cidade,
    String? estado,
    int page = 0,
    int size = 10,
  }) async {
    await Future.delayed(const Duration(milliseconds: 300));
    
    var filtered = _pets.where((pet) {
      if (especie != null && especie.isNotEmpty && pet.especie.toLowerCase() != especie.toLowerCase()) return false;
      if (porte != null && porte.isNotEmpty && pet.porte.toLowerCase() != porte.toLowerCase()) return false;
      if (cidade != null && cidade.isNotEmpty && pet.cidade?.toLowerCase() != cidade.toLowerCase()) return false;
      if (estado != null && estado.isNotEmpty && pet.estado?.toLowerCase() != estado.toLowerCase()) return false;
      return true;
    }).toList();

    final startIndex = page * size;
    if (startIndex >= filtered.length) {
      return const Right([]);
    }
    final endIndex = (startIndex + size) > filtered.length ? filtered.length : (startIndex + size);
    final paginated = filtered.sublist(startIndex, endIndex);

    return Right(paginated);
  }

  @override
  Future<Either<Failure, Pet>> getPetById(String id) async {
    final pet = _pets.firstWhere((p) => p.id == id, orElse: () => _pets.first);
    return Right(pet);
  }

  @override
  Future<Either<Failure, Pet>> cadastrarPet(Pet pet, {String? ongId}) async {
    await Future.delayed(const Duration(milliseconds: 500));
    final newPet = Pet(
      id: (DateTime.now().millisecondsSinceEpoch).toString(),
      nome: pet.nome,
      especie: pet.especie,
      raca: pet.raca,
      idade: pet.idade,
      sexo: pet.sexo,
      porte: pet.porte,
      cor: pet.cor,
      statusAdocao: pet.statusAdocao,
      cidade: pet.cidade,
      estado: pet.estado,
      imagemUrl: pet.imagemUrl.isEmpty 
          ? 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=400&auto=format&fit=crop'
          : pet.imagemUrl,
      vacinado: pet.vacinado,
      castrado: pet.castrado,
    );
    _pets.add(newPet);
    // Usa o ongId passado, ou fallback para 'ong-id-123'
    _petOngMap[newPet.id] = ongId ?? 'ong-id-123';
    return Right(newPet);
  }

  @override
  Future<Either<Failure, List<Pet>>> getPetsPorOng(String ongId) async {
    await Future.delayed(const Duration(milliseconds: 300));
    final ongPets = _pets.where((p) => _petOngMap[p.id] == ongId).toList();
    return Right(ongPets);
  }
}
