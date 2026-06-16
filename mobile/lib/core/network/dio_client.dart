import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:mybuddy_app/core/constants/app_config.dart';
import 'package:mybuddy_app/core/network/auth_interceptor.dart';
import 'package:mybuddy_app/core/network/error_interceptor.dart';


class DioClient {
  static Dio create() {
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

    dio.interceptors.add(AuthInterceptor());

    dio.interceptors.add(ErrorInterceptor());

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
