import 'package:dartz/dartz.dart';
import 'package:dio/dio.dart';
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
  final Dio _dio;

  static const _accessTokenKey = 'access_token';
  static const _refreshTokenKey = 'refresh_token';
  static const _idTokenKey = 'id_token';
  static const _clientId = 'mybuddy-frontend';
  static const _redirectUrl = 'com.mybuddy.app://callback';
  static const _scopes = ['openid', 'profile', 'email'];

  AuthRepositoryImpl({
    FlutterAppAuth? appAuth,
    FlutterSecureStorage? storage,
    Dio? dio,
  })  : _appAuth = appAuth ?? const FlutterAppAuth(),
        _storage = storage ?? const FlutterSecureStorage(),
        _dio = dio ?? Dio();

  String get _issuer =>
      '${AppConfig.keycloakUrl}/realms/mybuddy';

  @override
  Future<Either<Failure, User>> login(String email, String password) async {
    try {
      final response = await _dio.post(
        '${AppConfig.keycloakUrl}/realms/mybuddy/protocol/openid-connect/token',
        data: {
          'grant_type': 'password',
          'client_id': _clientId,
          'username': email,
          'password': password,
          'scope': _scopes.join(' '),
        },
        options: Options(
          contentType: 'application/x-www-form-urlencoded',
        ),
      );

      final accessToken = response.data['access_token'] as String?;
      final refreshToken = response.data['refresh_token'] as String?;
      final idToken = response.data['id_token'] as String?;

      if (accessToken == null) {
        return const Left(AuthFailure('Token de acesso não recebido.'));
      }

      await _storage.write(key: _accessTokenKey, value: accessToken);
      if (refreshToken != null) {
        await _storage.write(key: _refreshTokenKey, value: refreshToken);
      }
      if (idToken != null) {
        await _storage.write(key: _idTokenKey, value: idToken);
      }

      return Right(_parseUser(accessToken));
    } on DioException catch (e) {
      final message = e.response?.data['error_description'] ?? e.message ?? e.toString();
      return Left(AuthFailure('Erro ao fazer login: $message'));
    } catch (e) {
      return Left(AuthFailure('Erro ao fazer login: ${e.toString()}'));
    }
  }

  @override
  Future<Either<Failure, User>> loginWithKeycloak() async {
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

      final accessToken = result.accessToken;
      final refreshToken = result.refreshToken;
      final idToken = result.idToken;

      if (accessToken == null) {
        return const Left(AuthFailure('Login cancelado ou falhou.'));
      }

      await _storage.write(key: _accessTokenKey, value: accessToken);
      if (refreshToken != null) {
        await _storage.write(key: _refreshTokenKey, value: refreshToken);
      }
      if (idToken != null) {
        await _storage.write(key: _idTokenKey, value: idToken);
      }

      return Right(_parseUser(accessToken));
    } catch (e) {
      return Left(AuthFailure('Erro ao fazer login com Keycloak: ${e.toString()}'));
    }
  }

  @override
  Future<Either<Failure, void>> logout() async {
    try {
      final idToken = await _storage.read(key: _idTokenKey);
      await _storage.deleteAll();

      if (idToken != null) {
        await _appAuth.endSession(
          EndSessionRequest(
            issuer: _issuer,
            idTokenHint: idToken,
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

      final accessToken = result.accessToken;
      final newRefreshToken = result.refreshToken;
      final idToken = result.idToken;

      if (accessToken == null) return null;

      await _storage.write(key: _accessTokenKey, value: accessToken);
      if (newRefreshToken != null) {
        await _storage.write(key: _refreshTokenKey, value: newRefreshToken);
      }
      if (idToken != null) {
        await _storage.write(key: _idTokenKey, value: idToken);
      }

      return accessToken;
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
