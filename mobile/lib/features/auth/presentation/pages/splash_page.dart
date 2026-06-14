import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_state.dart';

class SplashPage extends StatefulWidget {
  const SplashPage({super.key});

  @override
  State<SplashPage> createState() => _SplashPageState();
}

class _SplashPageState extends State<SplashPage> with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _fadeAnimation;
  late Animation<double> _scaleAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1500),
    );

    _fadeAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeIn),
    );

    _scaleAnimation = Tween<double>(begin: 0.8, end: 1.0).animate(
      CurvedAnimation(parent: _controller, curve: Curves.elasticOut),
    );

    _controller.forward();
    _navigateToNextScreen();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  Future<void> _navigateToNextScreen() async {
    // Aguarda o tempo da animação (1.8 segundos)
    await Future.delayed(const Duration(milliseconds: 2000));
    if (!mounted) return;

    final authState = context.read<AuthBloc>().state;
    
    if (authState is AuthAuthenticated) {
      context.go('/pets');
    } else {
      const storage = FlutterSecureStorage();
      final seenOnboarding = await storage.read(key: 'onboarding_seen') == 'true';
      if (!mounted) return;
      
      if (seenOnboarding) {
        context.go('/login');
      } else {
        context.go('/onboarding');
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Scaffold(
      body: Center(
        child: FadeTransition(
          opacity: _fadeAnimation,
          child: ScaleTransition(
            scale: _scaleAnimation,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Container(
                  padding: const EdgeInsets.all(24),
                  decoration: BoxDecoration(
                    color: theme.colorScheme.primary.withAlpha(20),
                    shape: BoxShape.circle,
                  ),
                  child: Icon(
                    Icons.pets,
                    size: 100,
                    color: theme.colorScheme.primary,
                  ),
                ),
                const SizedBox(height: 24),
                Text(
                  'MyBuddy',
                  style: theme.textTheme.displaySmall?.copyWith(
                    fontWeight: FontWeight.bold,
                    color: isDark ? Colors.white : Colors.black87,
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  'Conectando pets a lares',
                  style: theme.textTheme.bodyLarge?.copyWith(
                    color: Colors.grey,
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
