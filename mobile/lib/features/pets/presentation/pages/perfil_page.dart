import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_event.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_state.dart';
import 'package:mybuddy_app/shared/widgets/app_button.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';

class PerfilPage extends StatelessWidget {
  const PerfilPage({super.key});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;
    
    // Pegando dados do estado de auth se estiver disponível
    final authState = context.read<AuthBloc>().state;
    String userName = 'Eder Henrique';
    String userEmail = 'eder@mybuddy.com';

    if (authState is AuthAuthenticated) {
      userName = authState.user.nome;
      userEmail = authState.user.email;
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('Meu Perfil'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            const SizedBox(height: 20),
            // Avatar
            CircleAvatar(
              radius: 60,
              backgroundColor: AppColors.primary.withAlpha(20),
              child: const Icon(
                Icons.person_rounded,
                size: 70,
                color: AppColors.primary,
              ),
            ),
            const SizedBox(height: 20),
            // Nome do usuário
            Text(
              userName,
              style: theme.textTheme.headlineSmall?.copyWith(
                fontWeight: FontWeight.bold,
                color: isDark ? Colors.white : Colors.black87,
              ),
            ),
            const SizedBox(height: 4),
            // Email do usuário
            Text(
              userEmail,
              style: theme.textTheme.bodyMedium?.copyWith(
                color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
              ),
            ),
            const SizedBox(height: 40),

            // Informações adicionais fictícias para parecer completo
            _buildProfileItem(context, Icons.edit_outlined, 'Editar Dados', isDark),
            _buildProfileItem(context, Icons.pets_outlined, 'Meus Pets para Adoção', isDark),
            _buildProfileItem(context, Icons.history_rounded, 'Histórico de Doações', isDark),
            _buildProfileItem(context, Icons.settings_outlined, 'Configurações', isDark),

            const SizedBox(height: 40),
            // Botão de Sair (Logout)
            AppButton(
              text: 'Sair da Conta',
              type: AppButtonType.outline,
              onPressed: () {
                context.read<AuthBloc>().add(LogoutRequested());
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildProfileItem(BuildContext context, IconData icon, String title, bool isDark) {
    final theme = Theme.of(context);
    return Padding(
      padding: const EdgeInsets.only(bottom: 16.0),
      child: Container(
        decoration: BoxDecoration(
          color: isDark ? AppColors.darkSurface : Colors.white,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(
            color: isDark ? AppColors.darkBorder : AppColors.border,
            width: 1,
          ),
        ),
        child: ListTile(
          leading: Icon(icon, color: AppColors.primary),
          title: Text(
            title,
            style: theme.textTheme.bodyLarge?.copyWith(
              fontWeight: FontWeight.w600,
              color: isDark ? Colors.white : Colors.black87,
            ),
          ),
          trailing: Icon(
            Icons.chevron_right_rounded,
            color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
          ),
          onTap: () {
            // Futura implementação
          },
        ),
      ),
    );
  }
}
