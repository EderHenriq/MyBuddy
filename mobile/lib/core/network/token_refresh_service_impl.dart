import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:mybuddy_app/core/constants/app_config.dart';
import 'package:mybuddy_app/core/network/token_refresh_service.dart';

class TokenRefreshServiceImpl implements TokenRefreshService {
  final FlutterSecureStorage _storage;
  final Dio _dio;

  static const _accessTokenKey = 'access_token';
  static const _refreshTokenKey = 'refresh_token';

  TokenRefreshServiceImpl({FlutterSecureStorage? storage, Dio? dio})
    : _storage = storage ?? const FlutterSecureStorage(),
      _dio =
          dio ??
          Dio(
            BaseOptions(
              connectTimeout: const Duration(seconds: 10),
              receiveTimeout: const Duration(seconds: 10),
            ),
          );

  @override
  Future<bool> refresh() async {
    try {
      final refreshToken = await _storage.read(key: _refreshTokenKey);
      if (refreshToken == null || refreshToken.isEmpty) return false;

      final url =
          '${AppConfig.keycloakUrl}/realms/mybuddy/protocol/openid-connect/token';

      final response = await _dio.post(
        url,
        data: {
          'grant_type': 'refresh_token',
          'client_id': 'mybuddy-backend',
          'refresh_token': refreshToken,
        },
        options: Options(contentType: 'application/x-www-form-urlencoded'),
      );

      final newAccessToken = response.data['access_token'] as String?;
      final newRefreshToken = response.data['refresh_token'] as String?;

      if (newAccessToken == null) return false;

      await _storage.write(key: _accessTokenKey, value: newAccessToken);
      if (newRefreshToken != null) {
        await _storage.write(key: _refreshTokenKey, value: newRefreshToken);
      }

      return true;
    } catch (_) {
      return false;
    }
  }

  @override
  Future<String?> getAccessToken() async {
    return _storage.read(key: _accessTokenKey);
  }

  Future<void> saveTokens({
    required String accessToken,
    required String refreshToken,
  }) async {
    await _storage.write(key: _accessTokenKey, value: accessToken);
    await _storage.write(key: _refreshTokenKey, value: refreshToken);
  }

  Future<void> clearTokens() async {
    await _storage.delete(key: _accessTokenKey);
    await _storage.delete(key: _refreshTokenKey);
  }
}
