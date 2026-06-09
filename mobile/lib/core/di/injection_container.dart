import 'package:dio/dio.dart';
import 'package:get_it/get_it.dart';
import 'package:mybuddy_app/core/network/dio_client.dart';
import 'package:mybuddy_app/features/auth/data/repositories/auth_repository_mock.dart';
import 'package:mybuddy_app/features/auth/domain/repositories/auth_repository.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/favoritos_cubit.dart';

final sl = GetIt.instance;

Future<void> init() async {
  _registerCore();
  _registerAuth();
  _registerPets();
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

void _registerPets() {
  // Cubit de favoritos persistente na sessão
  sl.registerLazySingleton<FavoritosCubit>(
    () => FavoritosCubit(),
  );
}

