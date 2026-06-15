import 'package:dartz/dartz.dart';
import 'package:mybuddy_app/core/errors/failures.dart';
import 'package:mybuddy_app/features/pets/domain/entities/pet.dart';
import 'package:mybuddy_app/features/pets/domain/repositories/pets_repository.dart';

class PetsRepositoryMock implements PetsRepository {
  final List<Pet> _pets = [
    const Pet(
      id: '1',
      nome: 'Pipoca',
      especie: 'Cachorro',
      raca: 'Golden Retriever',
      idade: 2,
      sexo: 'Macho',
      porte: 'Grande',
      cor: 'Dourado',
      statusAdocao: 'DISPONIVEL',
      cidade: 'Maringá',
      estado: 'PR',
      imagemUrl: 'https://images.unsplash.com/photo-1552053831-71594a27632d?q=80&w=400&auto=format&fit=crop',
      vacinado: true,
      castrado: true,
    ),
    const Pet(
      id: '2',
      nome: 'Mia',
      especie: 'Gato',
      raca: 'Siamês',
      idade: 1,
      sexo: 'Fêmea',
      porte: 'Pequeno',
      cor: 'Branco',
      statusAdocao: 'DISPONIVEL',
      cidade: 'Sarandi',
      estado: 'PR',
      imagemUrl: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=400&auto=format&fit=crop',
      vacinado: true,
      castrado: false,
    ),
    const Pet(
      id: '3',
      nome: 'Bidu',
      especie: 'Cachorro',
      raca: 'Poodle',
      idade: 4,
      sexo: 'Macho',
      porte: 'Médio',
      cor: 'Branco',
      statusAdocao: 'DISPONIVEL',
      cidade: 'Maringá',
      estado: 'PR',
      imagemUrl: 'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?q=80&w=400&auto=format&fit=crop',
      vacinado: false,
      castrado: true,
    ),
    const Pet(
      id: '4',
      nome: 'Thor',
      especie: 'Cachorro',
      raca: 'Pastor Alemão',
      idade: 3,
      sexo: 'Macho',
      porte: 'Grande',
      cor: 'Capa Preta',
      statusAdocao: 'DISPONIVEL',
      cidade: 'Londrina',
      estado: 'PR',
      imagemUrl: 'https://images.unsplash.com/photo-1589941013453-ec89f33b5e95?q=80&w=400&auto=format&fit=crop',
      vacinado: true,
      castrado: true,
    ),
    const Pet(
      id: '5',
      nome: 'Luna',
      especie: 'Gato',
      raca: 'Persa',
      idade: 2,
      sexo: 'Fêmea',
      porte: 'Pequeno',
      cor: 'Cinza',
      statusAdocao: 'DISPONIVEL',
      cidade: 'Maringá',
      estado: 'PR',
      imagemUrl: 'https://images.unsplash.com/photo-1618826411640-d6df44dd3f7a?q=80&w=400&auto=format&fit=crop',
      vacinado: true,
      castrado: true,
    ),
    const Pet(
      id: '6',
      nome: 'Simba',
      especie: 'Gato',
      raca: 'Vira-lata',
      idade: 1,
      sexo: 'Macho',
      porte: 'Médio',
      cor: 'Laranja',
      statusAdocao: 'DISPONIVEL',
      cidade: 'Paranavaí',
      estado: 'PR',
      imagemUrl: 'https://images.unsplash.com/photo-1519052537078-e6302a4968d4?q=80&w=400&auto=format&fit=crop',
      vacinado: false,
      castrado: true,
    ),
  ];

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
  Future<Either<Failure, List<Pet>>> getPets() async {
    await Future.delayed(const Duration(milliseconds: 300));
    return Right(List.from(_pets));
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
