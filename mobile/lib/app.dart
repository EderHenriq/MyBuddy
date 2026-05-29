import 'package:flutter/material.dart';
import 'package:mybuddy_app/core/router/app_router.dart';
import 'package:mybuddy_app/shared/theme/app_theme.dart';

class MyBuddyApp extends StatelessWidget {
  const MyBuddyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'MyBuddy',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.light,
      routerConfig: AppRouter.router,
    );
  }
}
