import 'package:equatable/equatable.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mybuddy_app/core/cache/cache_service.dart';
import 'package:mybuddy_app/features/pets/domain/entities/pet.dart';
import 'package:mybuddy_app/features/pets/domain/repositories/pets_repository.dart';

class FavoritosState extends Equatable {
  final List<Pet> favoritos;
  const FavoritosState(this.favoritos);

  @override
  List<Object?> get props => [favoritos];
}

class FavoritosCubit extends Cubit<FavoritosState> {
  final CacheService _cacheService;
  final PetsRepository _petsRepository;
  static const _favsKey = 'favoritos_ids';

  FavoritosCubit(this._cacheService, this._petsRepository)
      : super(const FavoritosState([])) {
    _loadFavoritos();
  }

  Future<void> _loadFavoritos() async {
    final cachedIds = _cacheService.getStringList(_favsKey);
    if (cachedIds == null) {
      // Valores mock iniciais na primeira inicialização
      final initialIds = ['1', '2', '3'];
      await _cacheService.setStringList(_favsKey, initialIds);
      final result = await _petsRepository.getPets();
      result.fold(
        (failure) {},
        (allPets) {
          final initialPets = allPets.where((p) => initialIds.contains(p.id)).toList();
          emit(FavoritosState(initialPets));
        },
      );
      return;
    }

    final result = await _petsRepository.getPets();
    result.fold(
      (failure) {},
      (allPets) {
        final favPets = allPets.where((p) => cachedIds.contains(p.id)).toList();
        emit(FavoritosState(favPets));
      },
    );
  }

  Future<void> toggleFavorito(Pet pet) async {
    final cachedIds = _cacheService.getStringList(_favsKey) ?? [];
    final list = List<Pet>.from(state.favoritos);

    if (list.any((p) => p.id == pet.id)) {
      list.removeWhere((p) => p.id == pet.id);
      cachedIds.remove(pet.id);
    } else {
      list.add(pet);
      cachedIds.add(pet.id);
    }
    
    await _cacheService.setStringList(_favsKey, cachedIds);
    emit(FavoritosState(list));
  }

  bool isFavorito(Pet pet) {
    return state.favoritos.any((p) => p.id == pet.id);
  }
}
