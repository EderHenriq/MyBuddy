import 'package:dio/dio.dart';
import 'package:get_it/get_it.dart';
import 'package:mybuddy_app/core/network/dio_client.dart';
import 'package:mybuddy_app/features/auth/data/repositories/auth_repository_mock.dart';
import 'package:mybuddy_app/features/auth/domain/repositories/auth_repository.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/data/repositories/user_repository_mock.dart';
import 'package:mybuddy_app/features/auth/domain/repositories/user_repository.dart';
import 'package:mybuddy_app/features/pets/data/repositories/pet_repository_mock.dart';
import 'package:mybuddy_app/features/pets/domain/repositories/pet_repository.dart';

final sl = GetIt.instance;

Future<void> init() async {
  _registerCore();
  _registerAuth();
  _registerPets();
}

void _registerCore() {
  // Dio
  sl.registerLazySingleton<Dio>(() => DioClient.create());

  sl.registerLazySingleton<TokenRefreshServiceImpl>(
        () => TokenRefreshServiceImpl(),
  );
}

void _registerAuth() {
  // BLoC — factory pra nova instância por uso
  sl.registerFactory<AuthBloc>(
    () => AuthBloc(authRepository: sl()),
  );
  sl.registerLazySingleton<UserRepository>(
        () => UserRepositoryMock(),
  );

  // Repository — mock por enquanto, trocar por impl real na MY-322
  sl.registerLazySingleton<AuthRepository>(
    () => AuthRepositoryMock(),
  );
}
void _registerPets() {
  sl.registerLazySingleton<PetRepository>(
        () => PetRepositoryMock(),
  );
}
