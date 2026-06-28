import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_event.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_state.dart';
import 'package:mybuddy_app/shared/widgets/app_button.dart';
import 'package:mybuddy_app/shared/widgets/app_input.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  final _formKey = GlobalKey<FormState>();

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  void _onLogin() {
    if (_formKey.currentState?.validate() ?? false) {
      context.read<AuthBloc>().add(LoginRequested(
            email: _emailController.text.trim(),
            password: _passwordController.text,
          ));
    }
  }

  @override
  Widget build(BuildContext context) {
    return BlocListener<AuthBloc, AuthState>(
      listener: (context, state) {
        if (state is AuthAuthenticated) {
          context.go('/home');
        } else if (state is AuthError) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text(state.message),
              backgroundColor: Colors.red,
            ),
          );
        }
      },
      child: Scaffold(
        body: SafeArea(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24),
            child: Form(
              key: _formKey,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  const SizedBox(height: 48),
                  Icon(
                    Icons.pets,
                    size: 80,
                    color: Theme.of(context).colorScheme.primary,
                  ),
                  const SizedBox(height: 16),
                  Text(
                    'MyBuddy',
                    style: Theme.of(context).textTheme.headlineLarge?.copyWith(
                          fontWeight: FontWeight.bold,
                        ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Conectando pets a lares',
                    style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                          color: Colors.grey,
                        ),
                  ),
                  const SizedBox(height: 48),
                  AppInput(
                    controller: _emailController,
                    labelText: 'Email',
                    hintText: 'Digite seu email',
                    keyboardType: TextInputType.emailAddress,
                    prefixIcon: Icons.email_outlined,
                    validator: (value) {
                      if (value == null || value.isEmpty) {
                        return 'Informe o email';
                      }
                      if (!value.contains('@')) {
                        return 'Email inválido';
                      }
                      return null;
                    },
                  ),
                  const SizedBox(height: 16),
                  AppInput(
                    controller: _passwordController,
                    labelText: 'Senha',
                    hintText: 'Digite sua senha',
                    isPassword: true,
                    prefixIcon: Icons.lock_outlined,
                    textInputAction: TextInputAction.done,
                    onFieldSubmitted: (_) => _onLogin(),
                    validator: (value) {
                      if (value == null || value.isEmpty) {
                        return 'Informe a senha';
                      }
                      return null;
                    },
                  ),
                  const SizedBox(height: 32),
                  BlocBuilder<AuthBloc, AuthState>(
                    builder: (context, state) {
                      final isDark = Theme.of(context).brightness == Brightness.dark;
                      final isLoading = state is AuthLoading;
                      return Column(
                        children: [
                          AppButton(
                            text: 'Entrar',
                            isLoading: isLoading && state is! LoginWithKeycloakRequested,
                            isDisabled: isLoading,
                            onPressed: _onLogin,
                          ),
                          const SizedBox(height: 24),
                          Row(
                            children: [
                              Expanded(child: Divider(color: isDark ? AppColors.darkBorder : AppColors.border)),
                              Padding(
                                padding: const EdgeInsets.symmetric(horizontal: 16),
                                child: Text(
                                  'ou continue com',
                                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                        color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                                      ),
                                ),
                              ),
                              Expanded(child: Divider(color: isDark ? AppColors.darkBorder : AppColors.border)),
                            ],
                          ),
                          const SizedBox(height: 20),
                          _SocialButton(
                            text: 'Continuar com Google',
                            logo: Image.network(
                              'https://developers.google.com/static/identity/images/g-logo.png',
                              width: 18,
                              height: 18,
                              errorBuilder: (context, error, stackTrace) => const Icon(
                                Icons.g_mobiledata,
                                size: 22,
                                color: Colors.blue,
                              ),
                            ),
                            isDisabled: isLoading,
                            onPressed: () {
                              context.read<AuthBloc>().add(LoginWithKeycloakRequested());
                            },
                          ),
                          const SizedBox(height: 12),
                          _SocialButton(
                            text: 'Continuar com Apple',
                            logo: Icon(
                              Icons.apple,
                              size: 20,
                              color: isDark ? Colors.white : Colors.black,
                            ),
                            isDisabled: isLoading,
                            onPressed: () {
                              context.read<AuthBloc>().add(LoginWithKeycloakRequested());
                            },
                          ),
                          const SizedBox(height: 12),
                          _SocialButton(
                            text: 'Entrar com Keycloak',
                            logo: Icon(
                              Icons.vpn_key_outlined,
                              size: 18,
                              color: isDark ? Colors.white70 : Colors.black87,
                            ),
                            isLoading: isLoading,
                            isDisabled: isLoading,
                            onPressed: () {
                              context.read<AuthBloc>().add(LoginWithKeycloakRequested());
                            },
                          ),
                          const SizedBox(height: 32),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Text(
                                'Não tem uma conta? ',
                                style: TextStyle(
                                  color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                                ),
                              ),
                              GestureDetector(
                                onTap: () => context.go('/cadastro'),
                                child: const Text(
                                  'Cadastre-se',
                                  style: TextStyle(
                                    color: AppColors.primary,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ),
                            ],
                          ),
                        ],
                      );
                    },
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class _SocialButton extends StatelessWidget {
  final String text;
  final Widget logo;
  final VoidCallback onPressed;
  final bool isLoading;
  final bool isDisabled;

  const _SocialButton({
    required this.text,
    required this.logo,
    required this.onPressed,
    this.isLoading = false,
    this.isDisabled = false,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;
    final isButtonDisabled = isDisabled || isLoading;

    return SizedBox(
      width: double.infinity,
      height: 52,
      child: OutlinedButton(
        onPressed: isButtonDisabled ? null : onPressed,
        style: OutlinedButton.styleFrom(
          backgroundColor: isDark ? AppColors.darkSurface : Colors.white,
          foregroundColor: isDark ? AppColors.darkTextPrimary : AppColors.textPrimary,
          side: BorderSide(
            color: isButtonDisabled
                ? (isDark ? Colors.white10 : Colors.grey.shade200)
                : (isDark ? AppColors.darkBorder : AppColors.border),
            width: 1.5,
          ),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          elevation: 0,
        ).copyWith(
          overlayColor: WidgetStateProperty.resolveWith<Color?>(
            (states) {
              if (states.contains(WidgetState.pressed)) {
                return (isDark ? Colors.white : Colors.black).withValues(alpha: 0.05);
              }
              return null;
            },
          ),
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            if (isLoading)
              SizedBox(
                width: 20,
                height: 20,
                child: CircularProgressIndicator(
                  strokeWidth: 2,
                  valueColor: AlwaysStoppedAnimation<Color>(
                    isDark ? AppColors.primary : AppColors.primary,
                  ),
                ),
              )
            else
              logo,
            const SizedBox(width: 12),
            Text(
              text,
              style: theme.textTheme.labelLarge?.copyWith(
                color: isButtonDisabled
                    ? (isDark ? Colors.white38 : Colors.grey.shade400)
                    : (isDark ? AppColors.darkTextPrimary : AppColors.textPrimary),
                fontSize: 15,
                fontWeight: FontWeight.w600,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
