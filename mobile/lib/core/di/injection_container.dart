import 'package:get_it/get_it.dart';
import 'package:mybuddy_app/features/auth/data/repositories/auth_repository_mock.dart';
import 'package:mybuddy_app/features/auth/domain/repositories/auth_repository.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';

final sl = GetIt.instance;

Future<void> init() async {
  // BLoC
  sl.registerFactory(() => AuthBloc(authRepository: sl()));

  // Repository
  sl.registerLazySingleton<AuthRepository>(() => AuthRepositoryMock());
}
