import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:mybuddy_app/features/adocao/presentation/bloc/adocao_cubit.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_state.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';
import 'package:mybuddy_app/shared/widgets/app_card.dart';

class AdocaoPage extends StatefulWidget {
  const AdocaoPage({super.key});

  @override
  State<AdocaoPage> createState() => _AdocaoPageState();
}

class _AdocaoPageState extends State<AdocaoPage> {
  @override
  void initState() {
    super.initState();
    context.read<AdocaoCubit>().loadSolicitacoes();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    final authState = context.read<AuthBloc>().state;
    String userEmail = '';
    String userId = '';
    if (authState is AuthAuthenticated) {
      userEmail = authState.user.email;
      userId = authState.user.id;
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('Processos de Adoção'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios_new_rounded),
          onPressed: () => context.pop(),
        ),
      ),
      body: BlocBuilder<AdocaoCubit, AdocaoState>(
        builder: (context, state) {
          if (state is AdocaoLoading) {
            return const Center(child: CircularProgressIndicator());
          }
          if (state is AdocaoLoaded) {
            final minhasSolicitacoes = userId.isNotEmpty
                ? state.solicitacoes.where((s) => s.adotanteId == userId).toList()
                : state.solicitacoes.where((s) => s.adotanteEmail == userEmail).toList();

            if (minhasSolicitacoes.isEmpty) {
              return Center(
                child: Padding(
                  padding: const EdgeInsets.all(32.0),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(
                        Icons.volunteer_activism_outlined,
                        size: 80,
                        color: isDark ? Colors.grey.shade700 : Colors.grey.shade300,
                      ),
                      const SizedBox(height: 24),
                      Text(
                        'Nenhuma adoção em andamento',
                        style: theme.textTheme.titleMedium?.copyWith(
                          fontWeight: FontWeight.bold,
                          color: isDark ? Colors.white70 : Colors.black54,
                        ),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        'Quando você solicitar a adoção de um pet, o andamento do processo aparecerá aqui.',
                        textAlign: TextAlign.center,
                        style: theme.textTheme.bodyMedium?.copyWith(
                          color: isDark ? Colors.grey.shade500 : Colors.grey.shade600,
                        ),
                      ),
                    ],
                  ),
                ),
              );
            }

            return ListView.builder(
              padding: const EdgeInsets.all(24.0),
              itemCount: minhasSolicitacoes.length,
              itemBuilder: (context, index) {
                final sol = minhasSolicitacoes[index];
                
                Color statusColor = Colors.orange;
                IconData statusIcon = Icons.pending_actions_rounded;
                if (sol.status.contains('Aprovado')) {
                  statusColor = AppColors.success;
                  statusIcon = Icons.check_circle_outline_rounded;
                } else if (sol.status.contains('Recusado')) {
                  statusColor = Colors.red;
                  statusIcon = Icons.cancel_outlined;
                }

                return Padding(
                  padding: const EdgeInsets.only(bottom: 16.0),
                  child: AppCard(
                    padding: const EdgeInsets.all(16.0),
                    child: Row(
                      children: [
                        ClipRRect(
                          borderRadius: BorderRadius.circular(12),
                          child: Image.network(
                            sol.petImagemUrl,
                            width: 70,
                            height: 70,
                            fit: BoxFit.cover,
                            errorBuilder: (context, error, stackTrace) => Container(
                              width: 70,
                              height: 70,
                              color: isDark ? Colors.grey.shade800 : Colors.grey.shade200,
                              child: const Icon(Icons.broken_image_outlined, size: 30),
                            ),
                          ),
                        ),
                        const SizedBox(width: 16),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                sol.petNome,
                                style: theme.textTheme.titleMedium?.copyWith(
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                              const SizedBox(height: 4),
                              Text(
                                'Solicitado em: ${sol.data}',
                                style: theme.textTheme.bodySmall?.copyWith(
                                  color: isDark ? Colors.grey.shade400 : Colors.grey.shade600,
                                ),
                              ),
                              const SizedBox(height: 8),
                              Row(
                                children: [
                                  Icon(statusIcon, size: 16, color: statusColor),
                                  const SizedBox(width: 6),
                                  Expanded(
                                    child: Text(
                                      sol.status,
                                      style: theme.textTheme.bodyMedium?.copyWith(
                                        color: statusColor,
                                        fontWeight: FontWeight.bold,
                                      ),
                                    ),
                                  ),
                                ],
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                );
              },
            );
          }
          return const Center(child: Text('Erro ao carregar processos de adoção.'));
        },
      ),
    );
  }
}
