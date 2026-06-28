import 'package:dio/dio.dart';
import 'package:mybuddy_app/core/errors/failures.dart';
import 'package:mybuddy_app/core/network/token_refresh_service.dart';

class ErrorInterceptor extends Interceptor {
  final TokenRefreshService _tokenRefreshService;
  final Dio _dio;

  ErrorInterceptor({
    required TokenRefreshService tokenRefreshService,
    required Dio dio,
  })  : _tokenRefreshService = tokenRefreshService,
        _dio = dio;

  @override
  Future<void> onError(
    DioException err,
    ErrorInterceptorHandler handler,
  ) async {
    final statusCode = err.response?.statusCode;

    if (statusCode == 401) {
      try {
        final success = await _tokenRefreshService.refresh();
        if (success) {
          final token = await _tokenRefreshService.getAccessToken();
          final options = err.requestOptions;
          if (token != null) {
            options.headers['Authorization'] = 'Bearer $token';
          }
          // Tenta realizar a requisição novamente com o novo token
          final response = await _dio.fetch(options);
          return handler.resolve(response);
        }
      } catch (_) {
        // Se falhar o refresh, segue para a rejeição original
      }

      handler.reject(
        DioException(
          requestOptions: err.requestOptions,
          response: err.response,
          error: const UnauthorizedFailure(),
          type: err.type,
        ),
      );
    } else if (statusCode != null && statusCode >= 500) {
      handler.reject(
        DioException(
          requestOptions: err.requestOptions,
          response: err.response,
          error: const ServerFailure('Erro no servidor. Tente novamente mais tarde.'),
          type: err.type,
        ),
      );
    } else if (err.type == DioExceptionType.connectionTimeout ||
        err.type == DioExceptionType.receiveTimeout ||
        err.type == DioExceptionType.connectionError) {
      handler.reject(
        DioException(
          requestOptions: err.requestOptions,
          response: err.response,
          error: const NetworkFailure('Sem conexão com a internet.'),
          type: err.type,
        ),
      );
    } else {
      handler.next(err);
    }
  }
}