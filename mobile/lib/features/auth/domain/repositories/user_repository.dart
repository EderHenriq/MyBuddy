import 'package:dio/dio.dart';
import 'package:get_it/get_it.dart';
import 'package:mybuddy_app/core/network/dio_client.dart';
import 'package:mybuddy_app/features/auth/data/repositories/auth_repository_mock.dart';
import 'package:mybuddy_app/features/auth/domain/repositories/auth_repository.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/data/repositories/user_repository_mock.dart';
import 'package:mybuddy_app/features/auth/domain/repositories/user_repository.dart';
import 'package:mybuddy_app/features/pets/domain/repositories/pets_repository.dart';
import 'package:mybuddy_app/features/pets/data/repositories/pets_repository_mock.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/pets_cubit.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/favoritos_cubit.dart';
import 'package:mybuddy_app/features/adocao/presentation/bloc/adocao_cubit.dart';
import 'package:mybuddy_app/features/marketplace/domain/repositories/products_repository.dart';
import 'package:mybuddy_app/features/marketplace/data/repositories/products_repository_mock.dart';
import 'package:mybuddy_app/features/marketplace/presentation/bloc/products_cubit.dart';
import 'package:mybuddy_app/shared/theme/theme_cubit.dart';

final sl = GetIt.instance;

Future<void> init() async {
  _registerCore();
  _registerAuth();
  _registerPets();
  _registerMarketplace();
}

void _registerCore() {
  sl.registerLazySingleton<Dio>(() => DioClient.create());
  sl.registerLazySingleton<ThemeCubit>(() => ThemeCubit());
}

void _registerAuth() {
  sl.registerFactory<AuthBloc>(
        () => AuthBloc(authRepository: sl()),
  );
  sl.registerLazySingleton<UserRepository>(
        () => UserRepositoryMock(),
  );
  sl.registerLazySingleton<AuthRepository>(
        () => AuthRepositoryMock(),
  );
}

void _registerPets() {
  sl.registerLazySingleton<PetsRepository>(
        () => PetsRepositoryMock(),
  );
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
  sl.registerLazySingleton<ProductsRepository>(
        () => ProductsRepositoryMock(),
  );
  sl.registerLazySingleton<ProductsCubit>(
        () => ProductsCubit(productsRepository: sl()),
  );
}