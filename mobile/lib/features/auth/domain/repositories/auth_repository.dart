import 'package:dartz/dartz.dart';
import 'package:mybuddy_app/core/errors/failures.dart';
import 'package:mybuddy_app/features/auth/domain/entities/user.dart';

abstract class AuthRepository {
  Future<Either<Failure, User>> login(String email, String password);
  Future<Either<Failure, User>> loginWithKeycloak();
  Future<Either<Failure, void>> logout();
  Future<Either<Failure, User>> getProfile();
  Future<bool> isAuthenticated();
}
