import 'package:dartz/dartz.dart';
import 'package:mybuddy_app/core/errors/failures.dart';
import 'package:mybuddy_app/features/auth/domain/entities/user.dart';
import 'package:mybuddy_app/features/auth/domain/repositories/auth_repository.dart';

class AuthRepositoryMock implements AuthRepository {
  bool _isAuthenticated = false;
  User? _currentUser;

  @override
  Future<Either<Failure, User>> login(String email, String password) async {
    await Future.delayed(const Duration(seconds: 1));

    if (email == 'adotante@mybuddy.com' && password == 'T1234567') {
      _currentUser = const User(
        id: '5df8a715-5321-4dc7-b388-5325ce4253d1',
        email: 'adotante@mybuddy.com',
        nome: 'Adotante MyBuddy',
        roles: ['ROLE_ADOTANTE'],
      );
      _isAuthenticated = true;
      return Right(_currentUser!);
    }

    return const Left(AuthFailure('Email ou senha inválidos'));
  }

  @override
  Future<Either<Failure, void>> logout() async {
    _isAuthenticated = false;
    _currentUser = null;
    return const Right(null);
  }

  @override
  Future<Either<Failure, User>> getProfile() async {
    if (_currentUser != null) return Right(_currentUser!);
    return const Left(AuthFailure('Usuário não autenticado'));
  }

  @override
  Future<bool> isAuthenticated() async => _isAuthenticated;
}
