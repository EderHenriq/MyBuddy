import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mybuddy_app/features/pets/domain/entities/pet.dart';

class FavoritosState {
  final List<Pet> favoritos;
  const FavoritosState(this.favoritos);
}

class FavoritosCubit extends Cubit<FavoritosState> {
  FavoritosCubit() : super(FavoritosState(_mockPets));

  void toggleFavorito(Pet pet) {
    final list = List<Pet>.from(state.favoritos);
    if (list.any((p) => p.id == pet.id)) {
      list.removeWhere((p) => p.id == pet.id);
    } else {
      list.add(pet);
    }
    emit(FavoritosState(list));
  }

  bool isFavorito(Pet pet) {
    return state.favoritos.any((p) => p.id == pet.id);
  }

  static final List<Pet> _mockPets = [
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
  ];
}
