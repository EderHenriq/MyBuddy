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

    if (password != 'Senha123') {
      return const Left(AuthFailure('Senha incorreta! Use Senha123'));
    }

    if (email == 'user@mybuddy.com') {
      _currentUser = const User(
        id: 'adotante-id-123',
        email: 'user@mybuddy.com',
        nome: 'Adotante MyBuddy',
        roles: ['ROLE_ADOTANTE'],
      );
      _isAuthenticated = true;
      return Right(_currentUser!);
    } else if (email == 'ong@mybuddy.com') {
      _currentUser = const User(
        id: 'ong-id-123',
        email: 'ong@mybuddy.com',
        nome: 'ONG Amigo Fiel',
        roles: ['ROLE_ONG'],
      );
      _isAuthenticated = true;
      return Right(_currentUser!);
    } else if (email == 'petshop@mybuddy.com') {
      _currentUser = const User(
        id: 'petshop-id-123',
        email: 'petshop@mybuddy.com',
        nome: 'Petshop Parceiro',
        roles: ['ROLE_PETSHOP'],
      );
      _isAuthenticated = true;
      return Right(_currentUser!);
    } else if (email == 'admin@mybuddy.com') {
      _currentUser = const User(
        id: 'admin-id-123',
        email: 'admin@mybuddy.com',
        nome: 'Administrador Geral',
        roles: ['ROLE_ADMIN'],
      );
      _isAuthenticated = true;
      return Right(_currentUser!);
    }

    return const Left(AuthFailure('Email ou senha inválidos.'));
  }

  @override
  Future<Either<Failure, User>> loginWithKeycloak() async {
    await Future.delayed(const Duration(milliseconds: 1500));
    _currentUser = const User(
      id: 'adotante-id-123',
      email: 'user@mybuddy.com',
      nome: 'Eder Henrique (SSO)',
      roles: ['ROLE_ADOTANTE'],
    );
    _isAuthenticated = true;
    return Right(_currentUser!);
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
    return const Left(AuthFailure('Usuário não autenticado.'));
  }

  @override
  Future<bool> isAuthenticated() async => _isAuthenticated;
}
