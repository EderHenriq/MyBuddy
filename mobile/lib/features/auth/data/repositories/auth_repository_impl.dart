import 'package:dartz/dartz.dart';
import 'package:flutter_appauth/flutter_appauth.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:mybuddy_app/core/constants/app_config.dart';
import 'package:mybuddy_app/core/errors/failures.dart';
import 'package:mybuddy_app/features/auth/domain/entities/user.dart';
import 'package:mybuddy_app/features/auth/domain/repositories/auth_repository.dart';
import 'package:jwt_decoder/jwt_decoder.dart';

class AuthRepositoryImpl implements AuthRepository {
  final FlutterAppAuth _appAuth;
  final FlutterSecureStorage _storage;

  static const _accessTokenKey = 'access_token';
  static const _refreshTokenKey = 'refresh_token';
  static const _clientId = 'mybuddy-frontend';
  static const _redirectUrl = 'com.mybuddy.app://callback';
  static const _scopes = ['openid', 'profile', 'email'];

  AuthRepositoryImpl({
    FlutterAppAuth? appAuth,
    FlutterSecureStorage? storage,
  })  : _appAuth = appAuth ?? const FlutterAppAuth(),
        _storage = storage ?? const FlutterSecureStorage();

  String get _issuer =>
      '${AppConfig.keycloakUrl}/realms/mybuddy';

  @override
  Future<Either<Failure, User>> login(String email, String password) async {
    try {
      final result = await _appAuth.authorizeAndExchangeCode(
        AuthorizationTokenRequest(
          _clientId,
          _redirectUrl,
          issuer: _issuer,
          scopes: _scopes,
          promptValues: ['login'],
        ),
      );

      if (result == null) {
        return const Left(AuthFailure('Login cancelado pelo usuário'));
      }

      await _storage.write(key: _accessTokenKey, value: result.accessToken);
      await _storage.write(key: _refreshTokenKey, value: result.refreshToken);

      return Right(_parseUser(result.accessToken!));
    } catch (e) {
      return Left(AuthFailure('Erro ao fazer login: ${e.toString()}'));
    }
  }

  @override
  Future<Either<Failure, void>> logout() async {
    try {
      final token = await _storage.read(key: _accessTokenKey);
      await _storage.deleteAll();

      if (token != null) {
        await _appAuth.endSession(
          EndSessionRequest(
            issuer: _issuer,
            idTokenHint: token,
            postLogoutRedirectUrl: _redirectUrl,
          ),
        );
      }

      return const Right(null);
    } catch (e) {
      await _storage.deleteAll();
      return const Right(null);
    }
  }

  @override
  Future<Either<Failure, User>> getProfile() async {
    try {
      final accessToken = await _storage.read(key: _accessTokenKey);
      if (accessToken == null) {
        return const Left(UnauthorizedFailure());
      }

      if (JwtDecoder.isExpired(accessToken)) {
        final refreshed = await _refresh();
        if (refreshed == null) {
          return const Left(UnauthorizedFailure());
        }
        return Right(_parseUser(refreshed));
      }

      return Right(_parseUser(accessToken));
    } catch (e) {
      return Left(ServerFailure(e.toString()));
    }
  }

  @override
  Future<bool> isAuthenticated() async {
    final token = await _storage.read(key: _accessTokenKey);
    if (token == null) return false;

    if (JwtDecoder.isExpired(token)) {
      final refreshed = await _refresh();
      return refreshed != null;
    }

    return true;
  }

  Future<String?> _refresh() async {
    try {
      final refreshToken = await _storage.read(key: _refreshTokenKey);
      if (refreshToken == null) return null;

      final result = await _appAuth.token(
        TokenRequest(
          _clientId,
          _redirectUrl,
          issuer: _issuer,
          refreshToken: refreshToken,
          scopes: _scopes,
        ),
      );

      if (result?.accessToken == null) return null;

      await _storage.write(key: _accessTokenKey, value: result!.accessToken);
      if (result.refreshToken != null) {
        await _storage.write(key: _refreshTokenKey, value: result.refreshToken);
      }

      return result.accessToken;
    } catch (_) {
      await _storage.deleteAll();
      return null;
    }
  }

  User _parseUser(String accessToken) {
    final claims = JwtDecoder.decode(accessToken);

    final realmRoles = (claims['realm_access']?['roles'] as List<dynamic>?)
            ?.map((r) => r.toString())
            .toList() ??
        [];

    return User(
      id: claims['sub'] ?? '',
      email: claims['email'] ?? '',
      nome: claims['name'] ?? claims['preferred_username'] ?? '',
      roles: realmRoles,
    );
  }
}
