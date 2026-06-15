import 'package:dartz/dartz.dart';
import 'package:mybuddy_app/core/errors/failures.dart';
import 'package:mybuddy_app/features/auth/domain/entities/user.dart';
import 'package:mybuddy_app/features/auth/domain/repositories/user_repository.dart';

class UserRepositoryMock implements UserRepository {
  User _user = const User(
    id: '5df8a715-5321-4dc7-b388-5325ce4253d1',
    email: 'adotante@mybuddy.com',
    nome: 'Adotante MyBuddy',
    roles: ['ROLE_ADOTANTE'],
  );

  @override
  Future<Either<Failure, User>> getProfile() async {
    await Future.delayed(const Duration(milliseconds: 300));
    return Right(_user);
  }

  @override
  Future<Either<Failure, User>> updateProfile({
    String? nome,
    String? telefone,
  }) async {
    await Future.delayed(const Duration(milliseconds: 500));

    _user = User(
      id: _user.id,
      email: _user.email,
      nome: nome ?? _user.nome,
      telefone: telefone ?? _user.telefone,
      roles: _user.roles,
    );

    return Right(_user);
  }
}