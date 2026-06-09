import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_state.dart';
import 'package:mybuddy_app/features/auth/presentation/pages/login_page.dart';
import 'package:mybuddy_app/features/auth/presentation/pages/splash_page.dart';
import 'package:mybuddy_app/features/auth/presentation/pages/onboarding_page.dart';
import 'package:mybuddy_app/features/auth/presentation/pages/cadastro_page.dart';
import 'package:mybuddy_app/features/pets/presentation/pages/pets_page.dart';
import 'package:mybuddy_app/features/pets/presentation/pages/favoritos_page.dart';
import 'package:mybuddy_app/features/pets/presentation/pages/perfil_page.dart';
import 'package:mybuddy_app/features/marketplace/presentation/pages/marketplace_page.dart';
import 'package:mybuddy_app/features/adocao/presentation/pages/adocao_page.dart';
import 'package:mybuddy_app/shared/widgets/main_scaffold.dart';

class AppRouter {
  static CustomTransitionPage<void> _fadeRoute(GoRouterState state, Widget child) {
    return CustomTransitionPage<void>(
      key: state.pageKey,
      child: child,
      transitionDuration: const Duration(milliseconds: 250),
      transitionsBuilder: (context, animation, secondaryAnimation, child) {
        return FadeTransition(opacity: animation, child: child);
      },
    );
  }

  static CustomTransitionPage<void> _slideRoute(GoRouterState state, Widget child) {
    return CustomTransitionPage<void>(
      key: state.pageKey,
      child: child,
      transitionDuration: const Duration(milliseconds: 350),
      transitionsBuilder: (context, animation, secondaryAnimation, child) {
        const begin = Offset(1.0, 0.0);
        const end = Offset.zero;
        const curve = Curves.easeInOutCubic;
        final tween = Tween(begin: begin, end: end).chain(CurveTween(curve: curve));
        return SlideTransition(position: animation.drive(tween), child: child);
      },
    );
  }

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
        final isCadastro = location == '/cadastro';

        if (!isAuthenticated) {
          // Se não estiver logado, permite acessar apenas splash, onboarding, login e cadastro
          if (isSplash || isOnboarding || isLogin || isCadastro) return null;
          return '/splash';
        }

        // Se estiver autenticado e tentar ir para as telas de auth, manda para o feed
        if (isAuthenticated && (isSplash || isOnboarding || isLogin || isCadastro)) {
          return '/pets';
        }

        return null;
      },
      routes: [
        GoRoute(
          path: '/splash',
          name: 'splash',
          pageBuilder: (context, state) => _fadeRoute(state, const SplashPage()),
        ),
        GoRoute(
          path: '/onboarding',
          name: 'onboarding',
          pageBuilder: (context, state) => _slideRoute(state, const OnboardingPage()),
        ),
        GoRoute(
          path: '/login',
          name: 'login',
          pageBuilder: (context, state) => _slideRoute(state, const LoginPage()),
        ),
        GoRoute(
          path: '/cadastro',
          name: 'cadastro',
          pageBuilder: (context, state) => _slideRoute(state, const CadastroPage()),
        ),
        ShellRoute(
          builder: (context, state, child) => MainScaffold(child: child),
          routes: [
            GoRoute(
              path: '/pets',
              name: 'pets',
              pageBuilder: (context, state) => _fadeRoute(state, const PetsPage()),
            ),
            GoRoute(
              path: '/favoritos',
              name: 'favoritos',
              pageBuilder: (context, state) => _fadeRoute(state, const FavoritosPage()),
            ),
            GoRoute(
              path: '/marketplace',
              name: 'marketplace',
              pageBuilder: (context, state) => _fadeRoute(state, const MarketplacePage()),
            ),
            GoRoute(
              path: '/adocao',
              name: 'adocao',
              pageBuilder: (context, state) => _fadeRoute(state, const AdocaoPage()),
            ),
            GoRoute(
              path: '/perfil',
              name: 'perfil',
              pageBuilder: (context, state) => _fadeRoute(state, const PerfilPage()),
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
