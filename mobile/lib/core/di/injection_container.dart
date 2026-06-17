import 'package:dio/dio.dart';
import 'package:get_it/get_it.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:mybuddy_app/core/cache/cache_service.dart';
import 'package:mybuddy_app/core/network/dio_client.dart';
import 'package:mybuddy_app/core/network/token_refresh_service_impl.dart';
import 'package:mybuddy_app/core/services/image_picker_service.dart';
import 'package:mybuddy_app/features/auth/data/repositories/auth_repository_mock.dart';
import 'package:mybuddy_app/features/auth/domain/repositories/auth_repository.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/data/repositories/user_repository_mock.dart';
import 'package:mybuddy_app/features/auth/domain/repositories/user_repository.dart';
import 'package:mybuddy_app/features/pets/domain/repositories/pets_repository.dart';
import 'package:mybuddy_app/features/pets/data/repositories/pets_repository_mock.dart';
import 'package:mybuddy_app/features/pets/domain/repositories/pet_repository.dart';
import 'package:mybuddy_app/features/pets/data/repositories/pet_repository_mock.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/image_picker_cubit.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/pets_cubit.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/favoritos_cubit.dart';
import 'package:mybuddy_app/features/adocao/presentation/bloc/adocao_cubit.dart';
import 'package:mybuddy_app/features/marketplace/domain/repositories/products_repository.dart';
import 'package:mybuddy_app/features/marketplace/data/repositories/products_repository_mock.dart';
import 'package:mybuddy_app/features/marketplace/presentation/bloc/products_cubit.dart';
import 'package:mybuddy_app/features/marketplace/presentation/bloc/cart_cubit.dart';
import 'package:mybuddy_app/shared/theme/theme_cubit.dart';

final sl = GetIt.instance;

Future<void> init() async {
  final prefs = await SharedPreferences.getInstance();
  sl.registerLazySingleton<CacheService>(() => CacheService(prefs));

  _registerCore();
  _registerAuth();
  _registerPets();
  _registerMarketplace();
}

void _registerCore() {
  // Dio
  sl.registerLazySingleton<Dio>(() => DioClient.create());

  // Theme
  sl.registerLazySingleton<ThemeCubit>(() => ThemeCubit());

  // Image Picker Service
  sl.registerLazySingleton<ImagePickerService>(() => ImagePickerService());

  // Token Refresh Service
  sl.registerLazySingleton<TokenRefreshServiceImpl>(
    () => TokenRefreshServiceImpl(),
  );
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

void _registerPets() {
  // Repositories
  sl.registerLazySingleton<PetsRepository>(
    () => PetsRepositoryMock(),
  );
  
  sl.registerLazySingleton<PetRepository>(
    () => PetRepositoryMock(),
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

  // Image Picker Cubit
  sl.registerFactory<ImagePickerCubit>(
    () => ImagePickerCubit(service: sl()),
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
  );

  sl.registerLazySingleton<CartCubit>(
    () => CartCubit(),
  );
}
