import 'package:flutter_test/flutter_test.dart';
import 'package:mybuddy_app/core/errors/failures.dart';
import 'package:mybuddy_app/features/auth/data/repositories/auth_repository_mock.dart';

void main() {
  late AuthRepositoryMock repository;

  setUp(() {
    repository = AuthRepositoryMock();
  });

  group('AuthRepositoryMock', () {
    group('login', () {
      test('deve retornar User com sucesso com credenciais válidas', () async {
        final result = await repository.login(
          'user@mybuddy.com',
          'Senha123',
        );

        result.fold(
              (failure) => fail('Esperava sucesso'),
              (user) {
            expect(user.email, 'user@mybuddy.com');
            expect(user.nome, 'Adotante MyBuddy');
            expect(user.roles, contains('ROLE_ADOTANTE'));
            expect(user.isAdotante, true);
            expect(user.isAdmin, false);
            expect(user.isOng, false);
          },
        );
      });

      test('deve retornar AuthFailure com credenciais inválidas', () async {
        final result = await repository.login('errado@email.com', 'Senha123');

        result.fold(
              (failure) {
            expect(failure, isA<AuthFailure>());
            expect(failure.message, 'Email ou senha inválidos.');
          },
              (user) => fail('Esperava falha'),
        );
      });

      test('deve retornar AuthFailure com senha incorreta', () async {
        final result = await repository.login(
          'user@mybuddy.com',
          'senhaerrada',
        );

        result.fold(
              (failure) {
            expect(failure, isA<AuthFailure>());
            expect(failure.message, 'Senha incorreta! Use Senha123');
          },
              (user) => fail('Esperava falha'),
        );
      });
    });

    group('isAuthenticated', () {
      test('deve retornar false antes do login', () async {
        final result = await repository.isAuthenticated();
        expect(result, false);
      });

      test('deve retornar true após login com sucesso', () async {
        await repository.login('user@mybuddy.com', 'Senha123');

        final result = await repository.isAuthenticated();
        expect(result, true);
      });

      test('deve retornar false após logout', () async {
        await repository.login('user@mybuddy.com', 'Senha123');
        await repository.logout();

        final result = await repository.isAuthenticated();
        expect(result, false);
      });
    });

    group('getProfile', () {
      test('deve retornar User após login', () async {
        await repository.login('user@mybuddy.com', 'Senha123');

        final result = await repository.getProfile();

        result.fold(
              (failure) => fail('Esperava sucesso'),
              (user) => expect(user.email, 'user@mybuddy.com'),
        );
      });

      test('deve retornar AuthFailure antes do login', () async {
        final result = await repository.getProfile();

        result.fold(
              (failure) => expect(failure, isA<AuthFailure>()),
              (user) => fail('Esperava falha'),
        );
      });
    });

    group('logout', () {
      test('deve limpar usuário após logout', () async {
        await repository.login('user@mybuddy.com', 'Senha123');
        await repository.logout();

        final profile = await repository.getProfile();
        profile.fold(
              (failure) => expect(failure, isA<AuthFailure>()),
              (user) => fail('Esperava falha após logout'),
        );
      });
    });
  });
}