import 'package:dio/dio.dart';
import 'package:mybuddy_app/core/errors/failures.dart';

class ErrorInterceptor extends Interceptor {
  @override
  void onError(
    DioException err,
    ErrorInterceptorHandler handler,
  ) {
    final statusCode = err.response?.statusCode;

    if (statusCode == 401) {
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