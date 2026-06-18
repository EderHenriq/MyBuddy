import 'package:dartz/dartz.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:mybuddy_app/features/auth/domain/entities/user.dart';
import 'package:mybuddy_app/features/auth/domain/repositories/auth_repository.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_event.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_state.dart';
import 'package:mybuddy_app/core/errors/failures.dart';

class MockAuthRepository extends Mock implements AuthRepository {}

void main() {
  late AuthBloc authBloc;
  late MockAuthRepository mockAuthRepository;

  const testUser = User(
    id: '1',
    nome: 'Ed',
    email: 'ed@mybuddy.com',
    roles: [],
  );

  setUp(() {
    mockAuthRepository = MockAuthRepository();
    authBloc = AuthBloc(authRepository: mockAuthRepository);
  });

  tearDown(() {
    authBloc.close();
  });

  group('AuthBloc', () {
    test('initial state should be AuthInitial', () {
      expect(authBloc.state, isA<AuthInitial>());
    });

    group('LoginRequested', () {
      test('should emit [AuthLoading, AuthAuthenticated] when login is successful', () async {
        // Arrange
        when(() => mockAuthRepository.login(any(), any()))
            .thenAnswer((_) async => const Right(testUser));

        // Act
        authBloc.add(const LoginRequested(email: 'ed@mybuddy.com', password: 'password'));

        // Assert
        await expectLater(
          authBloc.stream,
          emitsInOrder([
            isA<AuthLoading>(),
            isA<AuthAuthenticated>(),
          ]),
        );
      });

      test('should emit [AuthLoading, AuthError] when login fails', () async {
        // Arrange
        when(() => mockAuthRepository.login(any(), any()))
            .thenAnswer((_) async => const Left(ServerFailure('Invalid credentials')));

        // Act
        authBloc.add(const LoginRequested(email: 'ed@mybuddy.com', password: 'password'));

        // Assert
        await expectLater(
          authBloc.stream,
          emitsInOrder([
            isA<AuthLoading>(),
            isA<AuthError>(),
          ]),
        );
      });
    });

    group('LogoutRequested', () {
      test('should emit [AuthUnauthenticated] when logout is requested', () async {
        // Arrange
        when(() => mockAuthRepository.logout())
            .thenAnswer((_) async => const Right(null));

        // Act
        authBloc.add(LogoutRequested());

        // Assert
        await expectLater(
          authBloc.stream,
          emitsInOrder([
            isA<AuthUnauthenticated>(),
          ]),
        );
      });
    });

    group('CheckAuthStatus', () {
      test('should emit [AuthAuthenticated] when already authenticated and profile is loaded successfully', () async {
        // Arrange
        when(() => mockAuthRepository.isAuthenticated()).thenAnswer((_) async => true);
        when(() => mockAuthRepository.getProfile()).thenAnswer((_) async => const Right(testUser));

        // Act
        authBloc.add(CheckAuthStatus());

        // Assert
        await expectLater(
          authBloc.stream,
          emitsInOrder([
            isA<AuthAuthenticated>(),
          ]),
        );
      });

      test('should emit [AuthUnauthenticated] when not authenticated', () async {
        // Arrange
        when(() => mockAuthRepository.isAuthenticated()).thenAnswer((_) async => false);

        // Act
        authBloc.add(CheckAuthStatus());

        // Assert
        await expectLater(
          authBloc.stream,
          emitsInOrder([
            isA<AuthUnauthenticated>(),
          ]),
        );
      });
    });
  });
}
