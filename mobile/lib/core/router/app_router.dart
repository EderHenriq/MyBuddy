import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_state.dart';
import 'package:mybuddy_app/features/auth/presentation/pages/login_page.dart';
import 'package:mybuddy_app/features/auth/presentation/pages/splash_page.dart';
import 'package:mybuddy_app/features/auth/presentation/pages/onboarding_page.dart';
import 'package:mybuddy_app/features/pets/presentation/pages/pets_page.dart';
import 'package:mybuddy_app/features/marketplace/presentation/pages/marketplace_page.dart';
import 'package:mybuddy_app/features/adocao/presentation/pages/adocao_page.dart';
import 'package:mybuddy_app/shared/widgets/main_scaffold.dart';

class AppRouter {
  static GoRouter router(BuildContext context) {
    return GoRouter(
      initialLocation: '/splash',
      redirect: (context, state) {
        final authState = context.read<AuthBloc>().state;
        final isAuthenticated = authState is AuthAuthenticated;
        final location = state.matchedLocation;

        final isSplash = location == '/splash';
        final isOnboarding = location == '/onboarding';
        final isLogin = location == '/login';

        if (!isAuthenticated) {
          // Se não estiver logado, permite acessar apenas splash, onboarding e login
          if (isSplash || isOnboarding || isLogin) return null;
          return '/splash';
        }

        // Se estiver autenticado e tentar ir para as telas de auth, manda para o feed
        if (isAuthenticated && (isSplash || isOnboarding || isLogin)) {
          return '/pets';
        }

        return null;
      },
      routes: [
        GoRoute(
          path: '/splash',
          name: 'splash',
          builder: (context, state) => const SplashPage(),
        ),
        GoRoute(
          path: '/onboarding',
          name: 'onboarding',
          builder: (context, state) => const OnboardingPage(),
        ),
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
