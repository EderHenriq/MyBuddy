import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mybuddy_app/core/di/injection_container.dart' as di;
import 'package:mybuddy_app/core/router/app_router.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_event.dart';
import 'package:mybuddy_app/shared/theme/app_theme.dart';

class MyBuddyApp extends StatefulWidget {
  const MyBuddyApp({super.key});

  static final navigatorKey = GlobalKey<NavigatorState>();

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
    return BlocProvider.value(
      value: _authBloc,
      child: Builder(
        builder: (context) {
          return MaterialApp.router(
            title: 'MyBuddy',
            debugShowCheckedModeBanner: false,
            theme: AppTheme.light,
            routerConfig: AppRouter.router(context),
          );
        },
      ),
    );
  }
}
