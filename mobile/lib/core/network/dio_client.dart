import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:mybuddy_app/core/constants/app_config.dart';
import 'package:mybuddy_app/core/network/auth_interceptor.dart';
import 'package:mybuddy_app/core/network/error_interceptor.dart';
import 'package:mybuddy_app/core/network/token_refresh_service.dart';

class DioClient {
  static Dio create({
    required FlutterSecureStorage storage,
    required TokenRefreshService tokenRefreshService,
  }) {
    final dio = Dio(
      BaseOptions(
        baseUrl: AppConfig.apiBaseUrl,
        connectTimeout: const Duration(seconds: 10),
        receiveTimeout: const Duration(seconds: 10),
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
      ),
    );

    dio.interceptors.add(AuthInterceptor(storage: storage));

    dio.interceptors.add(ErrorInterceptor(
      tokenRefreshService: tokenRefreshService,
      dio: dio,
    ));

    if (AppConfig.showLogs) {
      dio.interceptors.add(
        LogInterceptor(
          requestBody: true,
          responseBody: true,
          error: true,
          logPrint: (o) => debugPrint(o.toString()),
        ),
      );
    }

    return dio;
  }
}
