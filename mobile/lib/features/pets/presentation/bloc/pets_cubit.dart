import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mybuddy_app/features/pets/domain/entities/pet.dart';
import 'package:mybuddy_app/features/pets/domain/repositories/pets_repository.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/pets_state.dart';
export 'pets_state.dart';

class PetsCubit extends Cubit<PetsState> {
  final PetsRepository petsRepository;
  
  PetsCubit({required this.petsRepository}) : super(PetsInitial());
  
  Future<void> loadPets() async {
    emit(PetsLoading());
    final result = await petsRepository.getPets();
    result.fold(
      (failure) => emit(const PetsError('Erro ao carregar os animais.')),
      (pets) => emit(PetsLoaded(pets)),
    );
  }

  Future<bool> cadastrarPet(Pet pet, {String? ongId}) async {
    final result = await petsRepository.cadastrarPet(pet, ongId: ongId);
    return result.fold(
      (failure) => false,
      (newPet) {
        loadPets();
        return true;
      },
    );
  }

  /// Retorna os pets cadastrados por uma ONG específica
  Future<List<Pet>> getPetsPorOng(String ongId) async {
    final result = await petsRepository.getPetsPorOng(ongId);
    return result.fold(
      (failure) => [],
      (pets) => pets,
    );
  }
}
