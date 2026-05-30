import 'package:dio/dio.dart';
import 'package:get_it/get_it.dart';
import 'package:mybuddy_app/core/network/dio_client.dart';
import 'package:mybuddy_app/features/auth/data/repositories/auth_repository_mock.dart';
import 'package:mybuddy_app/features/auth/domain/repositories/auth_repository.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';

final sl = GetIt.instance;

Future<void> init() async {
  _registerCore();
  _registerAuth();
}

void _registerCore() {
  // Dio
  sl.registerLazySingleton<Dio>(() => DioClient.create());
}

void _registerAuth() {
  // BLoC — factory pra nova instância por uso
  sl.registerFactory<AuthBloc>(
    () => AuthBloc(authRepository: sl()),
  );

  // Repository — mock por enquanto, trocar por impl real na MY-322
  sl.registerLazySingleton<AuthRepository>(
    () => AuthRepositoryMock(),
  );
}
