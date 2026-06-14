import 'package:dio/dio.dart';
import 'package:get_it/get_it.dart';
import 'package:mybuddy_app/core/network/dio_client.dart';
import 'package:mybuddy_app/features/auth/data/repositories/auth_repository_mock.dart';
import 'package:mybuddy_app/features/auth/domain/repositories/auth_repository.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
<<<<<<< HEAD
import 'package:mybuddy_app/features/auth/data/repositories/user_repository_mock.dart';
import 'package:mybuddy_app/features/auth/domain/repositories/user_repository.dart';
import 'package:mybuddy_app/features/pets/data/repositories/pet_repository_mock.dart';
import 'package:mybuddy_app/features/pets/domain/repositories/pet_repository.dart';
=======
import 'package:mybuddy_app/features/pets/domain/repositories/pets_repository.dart';
import 'package:mybuddy_app/features/pets/data/repositories/pets_repository_mock.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/pets_cubit.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/favoritos_cubit.dart';
import 'package:mybuddy_app/features/adocao/presentation/bloc/adocao_cubit.dart';
import 'package:mybuddy_app/features/marketplace/domain/repositories/products_repository.dart';
import 'package:mybuddy_app/features/marketplace/data/repositories/products_repository_mock.dart';
import 'package:mybuddy_app/features/marketplace/presentation/bloc/products_cubit.dart';
import 'package:mybuddy_app/shared/theme/theme_cubit.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:mybuddy_app/core/cache/cache_service.dart';
>>>>>>> fa45dcc3bdfdf6ea45f4e56cb4b983410870dfcf

final sl = GetIt.instance;

Future<void> init() async {
  final prefs = await SharedPreferences.getInstance();
  sl.registerLazySingleton<CacheService>(() => CacheService(prefs));

  _registerCore();
  _registerAuth();
  _registerPets();
<<<<<<< HEAD
=======
  _registerMarketplace();
>>>>>>> fa45dcc3bdfdf6ea45f4e56cb4b983410870dfcf
}

void _registerCore() {
  // Dio
  sl.registerLazySingleton<Dio>(() => DioClient.create());

<<<<<<< HEAD
  sl.registerLazySingleton<TokenRefreshServiceImpl>(
        () => TokenRefreshServiceImpl(),
  );
=======
  // Theme
  sl.registerLazySingleton<ThemeCubit>(() => ThemeCubit());
>>>>>>> fa45dcc3bdfdf6ea45f4e56cb4b983410870dfcf
}

void _registerAuth() {
  // BLoC
  sl.registerFactory<AuthBloc>(
    () => AuthBloc(authRepository: sl()),
  );
  sl.registerLazySingleton<UserRepository>(
        () => UserRepositoryMock(),
  );

  // Repository
  sl.registerLazySingleton<AuthRepository>(
    () => AuthRepositoryMock(),
  );
}
<<<<<<< HEAD
void _registerPets() {
  sl.registerLazySingleton<PetRepository>(
        () => PetRepositoryMock(),
=======

void _registerPets() {
  // Repositories
  sl.registerLazySingleton<PetsRepository>(
    () => PetsRepositoryMock(),
  );

  // Cubits
  sl.registerLazySingleton<FavoritosCubit>(
    () => FavoritosCubit(),
  );
  sl.registerLazySingleton<PetsCubit>(
    () => PetsCubit(petsRepository: sl()),
  );
  sl.registerLazySingleton<AdocaoCubit>(
    () => AdocaoCubit(),
  );
}

void _registerMarketplace() {
  // Repositories
  sl.registerLazySingleton<ProductsRepository>(
    () => ProductsRepositoryMock(),
  );

  // Cubits
  sl.registerLazySingleton<ProductsCubit>(
    () => ProductsCubit(productsRepository: sl()),
>>>>>>> fa45dcc3bdfdf6ea45f4e56cb4b983410870dfcf
  );
}
