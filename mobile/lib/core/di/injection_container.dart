import 'package:get_it/get_it.dart';

final sl = GetIt.instance;

Future<void> init() async {
  // Auth
  // sl.registerFactory(() => AuthBloc(loginUsecase: sl()));

  // Core
  // sl.registerLazySingleton(() => DioClient());
}
