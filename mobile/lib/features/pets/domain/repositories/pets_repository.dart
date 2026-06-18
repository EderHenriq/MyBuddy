import 'package:dartz/dartz.dart';
import 'package:mybuddy_app/core/errors/failures.dart';
import 'package:mybuddy_app/features/pets/domain/entities/pet.dart';

abstract class PetsRepository {
  Future<Either<Failure, List<Pet>>> getPets({
    String? especie,
    String? porte,
    String? cidade,
    String? estado,
    int page = 0,
    int size = 10,
  });
  Future<Either<Failure, Pet>> getPetById(String id);
  Future<Either<Failure, Pet>> cadastrarPet(Pet pet, {String? ongId});
  Future<Either<Failure, List<Pet>>> getPetsPorOng(String ongId);
}
