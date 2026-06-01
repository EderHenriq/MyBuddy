import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_state.dart';
import 'package:mybuddy_app/features/auth/presentation/pages/login_page.dart';
import 'package:mybuddy_app/features/pets/presentation/pages/pets_page.dart';
import 'package:mybuddy_app/features/marketplace/presentation/pages/marketplace_page.dart';
import 'package:mybuddy_app/features/adocao/presentation/pages/adocao_page.dart';
import 'package:mybuddy_app/shared/widgets/main_scaffold.dart';

class AppRouter {
  static GoRouter router(BuildContext context) {
    return GoRouter(
      initialLocation: '/login',
      redirect: (context, state) {
        final authState = context.read<AuthBloc>().state;
        final isAuthenticated = authState is AuthAuthenticated;
        final isLoggingIn = state.matchedLocation == '/login';

        if (!isAuthenticated && !isLoggingIn) return '/login';
        if (isAuthenticated && isLoggingIn) return '/pets';
        return null;
      },
      routes: [
        GoRoute(
          path: '/login',
          name: 'login',
          builder: (context, state) => const LoginPage(),
        ),
        ShellRoute(
          builder: (context, state, child) => MainScaffold(child: child),
          routes: [
            GoRoute(
              path: '/pets',
              name: 'pets',
              builder: (context, state) => const PetsPage(),
            ),
            GoRoute(
              path: '/marketplace',
              name: 'marketplace',
              builder: (context, state) => const MarketplacePage(),
            ),
            GoRoute(
              path: '/adocao',
              name: 'adocao',
              builder: (context, state) => const AdocaoPage(),
            ),
          ],
        ),
      ],
      errorBuilder: (context, state) => Scaffold(
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.error_outline, size: 64, color: Colors.red),
              const SizedBox(height: 16),
              Text(
                'Página não encontrada',
                style: Theme.of(context).textTheme.headlineSmall,
              ),
              const SizedBox(height: 8),
              TextButton(
                onPressed: () => context.go('/pets'),
                child: const Text('Voltar ao início'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
