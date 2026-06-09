import 'package:dartz/dartz.dart';
import 'package:mybuddy_app/core/errors/failures.dart';
import 'package:mybuddy_app/features/pets/domain/entities/pet.dart';

abstract class PetsRepository {
  Future<Either<Failure, List<Pet>>> getPets();
  Future<Either<Failure, Pet>> getPetById(String id);
  Future<Either<Failure, Pet>> cadastrarPet(Pet pet, {String? ongId});
  Future<Either<Failure, List<Pet>>> getPetsPorOng(String ongId);
}
