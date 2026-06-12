import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mybuddy_app/core/di/injection_container.dart' as di;
import 'package:mybuddy_app/core/router/app_router.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_event.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/pets_cubit.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/favoritos_cubit.dart';
import 'package:mybuddy_app/features/adocao/presentation/bloc/adocao_cubit.dart';
import 'package:mybuddy_app/features/marketplace/presentation/bloc/products_cubit.dart';
import 'package:mybuddy_app/shared/theme/app_theme.dart';
import 'package:mybuddy_app/shared/theme/theme_cubit.dart';

class MyBuddyApp extends StatefulWidget {
  const MyBuddyApp({super.key});

  @override
  State<MyBuddyApp> createState() => _MyBuddyAppState();
}

class _MyBuddyAppState extends State<MyBuddyApp> {
  late final AuthBloc _authBloc;

  @override
  void initState() {
    super.initState();
    _authBloc = di.sl<AuthBloc>();
    _authBloc.add(CheckAuthStatus());
  }

  @override
  Widget build(BuildContext context) {
    return MultiBlocProvider(
      providers: [
        BlocProvider<AuthBloc>.value(value: _authBloc),
        BlocProvider<FavoritosCubit>(
          create: (context) => di.sl<FavoritosCubit>(),
        ),
        BlocProvider<ThemeCubit>(
          create: (context) => di.sl<ThemeCubit>(),
        ),
        BlocProvider<PetsCubit>(
          create: (context) => di.sl<PetsCubit>()..loadPets(),
        ),
        BlocProvider<ProductsCubit>(
          create: (context) => di.sl<ProductsCubit>()..loadProducts(),
        ),
        BlocProvider<AdocaoCubit>(
          create: (context) => di.sl<AdocaoCubit>()..loadSolicitacoes(),
        ),
      ],
      child: BlocBuilder<ThemeCubit, ThemeMode>(
        builder: (context, themeMode) {
          return MaterialApp.router(
            title: 'MyBuddy',
            debugShowCheckedModeBanner: false,
            theme: AppTheme.light,
            darkTheme: AppTheme.dark,
            themeMode: themeMode,
            routerConfig: AppRouter.router(context),
          );
        },
      ),
    );
  }
}
