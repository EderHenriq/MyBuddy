import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_state.dart';
import 'package:mybuddy_app/features/adocao/presentation/bloc/adocao_cubit.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';
import 'package:mybuddy_app/shared/widgets/app_card.dart';

class SolicitacoesOngPage extends StatelessWidget {
  const SolicitacoesOngPage({super.key});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    // Pega o ID da ONG logada
    final authState = context.read<AuthBloc>().state;
    String ongId = 'ong-id-123';
    if (authState is AuthAuthenticated) {
      ongId = authState.user.id;
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('Solicitações de Adoção'),
      ),
      body: BlocBuilder<AdocaoCubit, AdocaoState>(
        builder: (context, state) {
          if (state is AdocaoLoading) {
            return const Center(child: CircularProgressIndicator());
          }

          if (state is AdocaoLoaded) {
            // Filtra apenas as solicitações para esta ONG
            final solicitacoes = state.solicitacoes
                .where((s) => s.ongId == ongId)
                .toList();

            if (solicitacoes.isEmpty) {
              return Center(
                child: Padding(
                  padding: const EdgeInsets.all(32.0),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(Icons.receipt_long_outlined, size: 72, color: Colors.grey.shade400),
                      const SizedBox(height: 16),
                      Text(
                        'Nenhuma solicitação recebida',
                        style: theme.textTheme.titleMedium?.copyWith(color: Colors.grey),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        'Quando um usuário solicitar adoção de um dos seus pets, aparecerá aqui.',
                        textAlign: TextAlign.center,
                        style: theme.textTheme.bodyMedium?.copyWith(color: Colors.grey.shade500),
                      ),
                    ],
                  ),
                ),
              );
            }

            // Contadores de status para o header
            final pendentes = solicitacoes.where((s) => s.status == 'Aguardando Entrevista').length;
            final aprovados = solicitacoes.where((s) => s.status.contains('Aprovado')).length;

            return Column(
              children: [
                // Header com resumo
                Container(
                  margin: const EdgeInsets.fromLTRB(24, 16, 24, 0),
                  padding: const EdgeInsets.all(16),
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                      colors: [AppColors.primary.withAlpha(20), AppColors.secondary.withAlpha(10)],
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                    ),
                    borderRadius: BorderRadius.circular(16),
                    border: Border.all(color: AppColors.primary.withAlpha(40)),
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      _buildStatusCounter(context, 'Total', solicitacoes.length.toString(), Colors.blue, isDark),
                      _buildStatusCounter(context, 'Pendentes', pendentes.toString(), Colors.orange, isDark),
                      _buildStatusCounter(context, 'Aprovados', aprovados.toString(), AppColors.success, isDark),
                    ],
                  ),
                ),
                const SizedBox(height: 8),
                Expanded(
                  child: ListView.builder(
                    padding: const EdgeInsets.all(24.0),
                    itemCount: solicitacoes.length,
                    itemBuilder: (context, index) {
                      final solicitacao = solicitacoes[index];
                      final bool isPendente = solicitacao.status == 'Aguardando Entrevista';

                      Color statusColor = Colors.orange;
                      if (solicitacao.status.contains('Aprovado')) statusColor = AppColors.success;
                      if (solicitacao.status.contains('Recusado')) statusColor = Colors.red;

                      return Padding(
                        padding: const EdgeInsets.only(bottom: 16.0),
                        child: AppCard(
                          padding: const EdgeInsets.all(16),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              // Pet details
                              Row(
                                children: [
                                  ClipRRect(
                                    borderRadius: BorderRadius.circular(10),
                                    child: Image.network(
                                      solicitacao.petImagemUrl,
                                      height: 56,
                                      width: 56,
                                      fit: BoxFit.cover,
                                      errorBuilder: (context, error, stackTrace) => Container(
                                        height: 56,
                                        width: 56,
                                        color: isDark ? Colors.grey.shade800 : Colors.grey.shade200,
                                        child: const Icon(Icons.pets, color: Colors.grey),
                                      ),
                                    ),
                                  ),
                                  const SizedBox(width: 12),
                                  Expanded(
                                    child: Column(
                                      crossAxisAlignment: CrossAxisAlignment.start,
                                      children: [
                                        Text(
                                          solicitacao.petNome,
                                          style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                                        ),
                                        const SizedBox(height: 2),
                                        Text(
                                          'Solicitado em: ${solicitacao.data}',
                                          style: theme.textTheme.bodySmall?.copyWith(color: Colors.grey),
                                        ),
                                      ],
                                    ),
                                  ),
                                  Container(
                                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                                    decoration: BoxDecoration(
                                      color: statusColor.withAlpha(20),
                                      borderRadius: BorderRadius.circular(12),
                                      border: Border.all(color: statusColor.withAlpha(80)),
                                    ),
                                    child: Text(
                                      solicitacao.status,
                                      style: TextStyle(
                                        color: statusColor,
                                        fontSize: 10,
                                        fontWeight: FontWeight.bold,
                                      ),
                                    ),
                                  ),
                                ],
                              ),
                              const Divider(height: 24),

                              // Adotante details
                              Text(
                                'Interessado(a):',
                                style: theme.textTheme.bodySmall?.copyWith(
                                  fontWeight: FontWeight.bold,
                                  color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                                ),
                              ),
                              const SizedBox(height: 6),
                              Text(
                                solicitacao.adotanteNome,
                                style: theme.textTheme.bodyLarge?.copyWith(fontWeight: FontWeight.w600),
                              ),
                              const SizedBox(height: 4),
                              Row(
                                children: [
                                  const Icon(Icons.email_outlined, size: 13, color: Colors.grey),
                                  const SizedBox(width: 4),
                                  Expanded(
                                    child: Text(
                                      solicitacao.adotanteEmail,
                                      style: theme.textTheme.bodySmall?.copyWith(color: Colors.grey),
                                      overflow: TextOverflow.ellipsis,
                                    ),
                                  ),
                                ],
                              ),
                              const SizedBox(height: 2),
                              Row(
                                children: [
                                  const Icon(Icons.phone_outlined, size: 13, color: Colors.grey),
                                  const SizedBox(width: 4),
                                  Text(
                                    solicitacao.adotanteTelefone,
                                    style: theme.textTheme.bodySmall?.copyWith(color: Colors.grey),
                                  ),
                                ],
                              ),

                              // Action buttons if pending
                              if (isPendente) ...[
                                const SizedBox(height: 20),
                                Row(
                                  children: [
                                    Expanded(
                                      child: OutlinedButton.icon(
                                        style: OutlinedButton.styleFrom(
                                          foregroundColor: Colors.red,
                                          side: const BorderSide(color: Colors.red),
                                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                                          padding: const EdgeInsets.symmetric(vertical: 12),
                                        ),
                                        onPressed: () {
                                          context.read<AdocaoCubit>().atualizarStatus(solicitacao.id, 'Recusado');
                                        },
                                        icon: const Icon(Icons.close_rounded, size: 18),
                                        label: const Text('Recusar'),
                                      ),
                                    ),
                                    const SizedBox(width: 12),
                                    Expanded(
                                      child: ElevatedButton.icon(
                                        style: ElevatedButton.styleFrom(
                                          backgroundColor: AppColors.success,
                                          foregroundColor: Colors.white,
                                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                                          padding: const EdgeInsets.symmetric(vertical: 12),
                                        ),
                                        onPressed: () {
                                          context.read<AdocaoCubit>().atualizarStatus(solicitacao.id, 'Aprovado! Aguardando Retirada');
                                        },
                                        icon: const Icon(Icons.check_rounded, size: 18),
                                        label: const Text('Aprovar'),
                                      ),
                                    ),
                                  ],
                                ),
                              ],
                            ],
                          ),
                        ),
                      );
                    },
                  ),
                ),
              ],
            );
          }

          return const Center(child: Text('Erro ao carregar solicitações.'));
        },
      ),
    );
  }

  Widget _buildStatusCounter(BuildContext context, String label, String count, Color color, bool isDark) {
    final theme = Theme.of(context);
    return Column(
      children: [
        Text(
          count,
          style: theme.textTheme.headlineSmall?.copyWith(
            fontWeight: FontWeight.bold,
            color: color,
          ),
        ),
        const SizedBox(height: 2),
        Text(
          label,
          style: theme.textTheme.bodySmall?.copyWith(
            color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
          ),
        ),
      ],
    );
  }
}
