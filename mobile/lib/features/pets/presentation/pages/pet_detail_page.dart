import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_state.dart';
import 'package:mybuddy_app/features/pets/domain/entities/pet.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/pets_cubit.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/favoritos_cubit.dart';
import 'package:mybuddy_app/features/adocao/presentation/bloc/adocao_cubit.dart';
import 'package:mybuddy_app/shared/widgets/app_button.dart';
import 'package:mybuddy_app/shared/widgets/app_card.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';

class PetDetailPage extends StatelessWidget {
  final String petId;

  const PetDetailPage({super.key, required this.petId});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return BlocBuilder<PetsCubit, PetsState>(
      builder: (context, petsState) {
        Pet? pet;
        if (petsState is PetsLoaded && petsState.pets.isNotEmpty) {
          // Procura pelo ID exato; se não encontrar, retorna null (não usa fallback arriscado)
          try {
            pet = petsState.pets.firstWhere((p) => p.id == petId);
          } catch (_) {
            pet = null;
          }
        }

        if (pet == null) {
          return Scaffold(
            appBar: AppBar(),
            body: const Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.error_outline, size: 64, color: Colors.grey),
                  SizedBox(height: 16),
                  Text('Pet não encontrado', style: TextStyle(color: Colors.grey)),
                ],
              ),
            ),
          );
        }

        // Obter dados do usuário logado
        final authState = context.read<AuthBloc>().state;
        String clienteId = '';
        String clienteNome = 'Visitante';
        String clienteEmail = '';
        bool isOng = false;
        bool isLoggedIn = false;

        if (authState is AuthAuthenticated) {
          clienteId = authState.user.id;
          clienteNome = authState.user.nome;
          clienteEmail = authState.user.email;
          isOng = authState.user.isOng;
          isLoggedIn = true;
        }

        final currentPet = pet;

        void showAdoptionDialog() async {
          if (!isLoggedIn) {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(
                content: Text('Faça login para solicitar adoção.'),
                backgroundColor: Colors.orange,
              ),
            );
            return;
          }

          // Verifica se já tem solicitação ativa para este pet
          final adocaoCubit = context.read<AdocaoCubit>();
          if (adocaoCubit.jaTemSolicitacaoAtiva(currentPet.id, clienteId)) {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(
                content: Text('Você já possui uma solicitação ativa para este pet!'),
                backgroundColor: Colors.orange,
              ),
            );
            return;
          }

          // Mostra diálogo de confirmação antes de submeter
          final confirmed = await showDialog<bool>(
            context: context,
            builder: (ctx) => AlertDialog(
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              title: const Text('Confirmar Solicitação'),
              content: Text(
                'Você deseja solicitar a adoção de ${currentPet.nome}?\n\nA ONG responsável entrará em contato pelo email:\n$clienteEmail',
              ),
              actions: [
                TextButton(
                  onPressed: () => Navigator.pop(ctx, false),
                  child: const Text('Cancelar'),
                ),
                ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppColors.primary,
                    foregroundColor: Colors.white,
                  ),
                  onPressed: () => Navigator.pop(ctx, true),
                  child: const Text('Confirmar'),
                ),
              ],
            ),
          );

          if (confirmed != true || !context.mounted) return;

          final success = await adocaoCubit.solicitarAdocao(
            petId: currentPet.id,
            petNome: currentPet.nome,
            petImagemUrl: currentPet.imagemUrl,
            clienteId: clienteId,
            clienteNome: clienteNome,
            clienteEmail: clienteEmail,
            clienteTelefone: '(44) 99999-0000',
          );

          if (!context.mounted) return;

          if (success) {
            showModalBottomSheet(
              context: context,
              backgroundColor: Colors.transparent,
              isScrollControlled: true,
              builder: (context) {
                return Container(
                  decoration: BoxDecoration(
                    color: isDark ? AppColors.darkSurface : Colors.white,
                    borderRadius: const BorderRadius.vertical(top: Radius.circular(24)),
                  ),
                  padding: const EdgeInsets.fromLTRB(32, 24, 32, 48),
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Container(
                        width: 40,
                        height: 4,
                        decoration: BoxDecoration(
                          color: Colors.grey.shade300,
                          borderRadius: BorderRadius.circular(10),
                        ),
                      ),
                      const SizedBox(height: 24),
                      const Icon(Icons.volunteer_activism_rounded, size: 72, color: AppColors.primary),
                      const SizedBox(height: 16),
                      Text(
                        'Solicitação Enviada!',
                        style: theme.textTheme.headlineSmall?.copyWith(fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 12),
                      Text(
                        'A ONG responsável pelo(a) ${currentPet.nome} foi notificada e entrará em contato com você em breve para realizar a entrevista de adoção.',
                        textAlign: TextAlign.center,
                        style: theme.textTheme.bodyMedium?.copyWith(
                          color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                          height: 1.5,
                        ),
                      ),
                      const SizedBox(height: 32),
                      AppButton(
                        text: 'Entendido',
                        onPressed: () {
                          Navigator.pop(context);
                          context.pop(); // Volta para o feed
                        },
                      ),
                    ],
                  ),
                );
              },
            );
          } else {
            // Tratamento de duplicata ou erro
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(
                content: Text('Você já possui uma solicitação ativa para este pet!'),
                backgroundColor: Colors.orange,
              ),
            );
          }
        }

        return Scaffold(
          body: Stack(
            children: [
              // Scrollable Content
              SingleChildScrollView(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    // Hero Image
                    Hero(
                      tag: 'pet_img_${currentPet.id}',
                      child: Image.network(
                        currentPet.imagemUrl,
                        height: 380,
                        fit: BoxFit.cover,
                        errorBuilder: (context, error, stackTrace) => Container(
                          height: 380,
                          color: isDark ? Colors.grey.shade800 : Colors.grey.shade200,
                          child: const Icon(Icons.broken_image_outlined, size: 80),
                        ),
                      ),
                    ),

                    // Pet Info Container
                    Transform.translate(
                      offset: const Offset(0, -20),
                      child: Container(
                        decoration: BoxDecoration(
                          color: isDark ? AppColors.darkBackground : AppColors.background,
                          borderRadius: const BorderRadius.vertical(top: Radius.circular(24)),
                        ),
                        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 32),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            // Name & Gender
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Text(
                                      currentPet.nome,
                                      style: theme.textTheme.headlineMedium?.copyWith(
                                        fontWeight: FontWeight.bold,
                                        color: isDark ? Colors.white : Colors.black87,
                                      ),
                                    ),
                                    const SizedBox(height: 4),
                                    Row(
                                      children: [
                                        const Icon(Icons.location_on_outlined, size: 16, color: AppColors.primary),
                                        const SizedBox(width: 4),
                                        Text(
                                          '${currentPet.cidade} - ${currentPet.estado}',
                                          style: theme.textTheme.bodyMedium?.copyWith(
                                            color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                                          ),
                                        ),
                                      ],
                                    ),
                                  ],
                                ),
                                Icon(
                                  currentPet.sexo == 'Macho' ? Icons.male_rounded : Icons.female_rounded,
                                  size: 32,
                                  color: currentPet.sexo == 'Macho' ? Colors.blue : Colors.pink,
                                ),
                              ],
                            ),
                            const SizedBox(height: 24),

                            // Technical specs
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                _buildSpecCard(context, 'Raça', currentPet.raca, isDark),
                                _buildSpecCard(context, 'Idade',
                                    '${currentPet.idade} ${currentPet.idade == 1 ? 'ano' : 'anos'}', isDark),
                                _buildSpecCard(context, 'Porte', currentPet.porte, isDark),
                              ],
                            ),
                            const SizedBox(height: 32),

                            // Vaccine & Castration Badges
                            Wrap(
                              spacing: 12,
                              runSpacing: 8,
                              children: [
                                if (currentPet.vacinado)
                                  _buildBadge(context, 'Vacinado', Icons.check_circle_outline, Colors.green)
                                else
                                  _buildBadge(context, 'Vacina Pendente', Icons.info_outline, Colors.orange),
                                if (currentPet.castrado)
                                  _buildBadge(context, 'Castrado', Icons.check_circle_outline, Colors.green)
                                else
                                  _buildBadge(context, 'Não Castrado', Icons.info_outline, Colors.orange),
                              ],
                            ),
                            const SizedBox(height: 32),

                            // About
                            Text(
                              'Sobre mim',
                              style: theme.textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
                            ),
                            const SizedBox(height: 12),
                            Text(
                              'Olá! Sou o(a) ${currentPet.nome}, um(a) lindo(a) ${currentPet.especie} da raça ${currentPet.raca}. Estou à procura de uma família que me dê muito amor, carinho e espaço para brincar. Sou muito brincalhão(a), me dou bem com outros animais e adoro passear ao ar livre. Venha me conhecer!',
                              style: theme.textTheme.bodyLarge?.copyWith(
                                color: isDark ? AppColors.darkTextPrimary : AppColors.textPrimary,
                                height: 1.5,
                              ),
                            ),
                            const SizedBox(height: 32),

                            // NGO Info Card
                            AppCard(
                              padding: const EdgeInsets.all(16),
                              child: Row(
                                children: [
                                  CircleAvatar(
                                    radius: 24,
                                    backgroundColor: AppColors.primary.withAlpha(20),
                                    child: const Icon(Icons.volunteer_activism_rounded, color: AppColors.primary),
                                  ),
                                  const SizedBox(width: 16),
                                  Expanded(
                                    child: Column(
                                      crossAxisAlignment: CrossAxisAlignment.start,
                                      children: [
                                        Text(
                                          'ONG Amigo Fiel',
                                          style: theme.textTheme.bodyLarge?.copyWith(fontWeight: FontWeight.bold),
                                        ),
                                        const SizedBox(height: 2),
                                        Text(
                                          'Organização Protetora Parceira',
                                          style: theme.textTheme.bodySmall?.copyWith(
                                            color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                                          ),
                                        ),
                                      ],
                                    ),
                                  ),
                                  IconButton(
                                    icon: const Icon(Icons.chat_bubble_outline_rounded, color: AppColors.primary),
                                    onPressed: () {
                                      ScaffoldMessenger.of(context).showSnackBar(
                                        const SnackBar(content: Text('Chat em tempo real em desenvolvimento!')),
                                      );
                                    },
                                  ),
                                ],
                              ),
                            ),
                            const SizedBox(height: 100), // Espaço para o botão fixo
                          ],
                        ),
                      ),
                    ),
                  ],
                ),
              ),

              // Floating Back Button
              Positioned(
                top: 48,
                left: 20,
                child: CircleAvatar(
                  backgroundColor: Colors.white.withAlpha(200),
                  child: IconButton(
                    icon: const Icon(Icons.arrow_back_ios_new_rounded, color: Colors.black87, size: 20),
                    onPressed: () => context.pop(),
                  ),
                ),
              ),

              // Floating Favorite Button
              Positioned(
                top: 48,
                right: 20,
                child: BlocBuilder<FavoritosCubit, FavoritosState>(
                  builder: (context, state) {
                    final isFav = context.read<FavoritosCubit>().isFavorito(currentPet);
                    return CircleAvatar(
                      backgroundColor: Colors.white.withAlpha(200),
                      child: IconButton(
                        icon: Icon(
                          isFav ? Icons.favorite_rounded : Icons.favorite_border_rounded,
                          color: isFav ? AppColors.primary : Colors.black87,
                          size: 20,
                        ),
                        onPressed: () {
                          context.read<FavoritosCubit>().toggleFavorito(currentPet);
                        },
                      ),
                    );
                  },
                ),
              ),

              // Bottom Fixed Action Button (apenas para Adotantes logados)
              if (!isOng)
                Positioned(
                  bottom: 24,
                  left: 24,
                  right: 24,
                  child: AppButton(
                    text: isLoggedIn ? 'Quero Adotar' : 'Faça Login para Adotar',
                    onPressed: showAdoptionDialog,
                  ),
                ),
            ],
          ),
        );
      },
    );
  }

  Widget _buildSpecCard(BuildContext context, String title, String value, bool isDark) {
    final theme = Theme.of(context);
    return Expanded(
      child: Container(
        margin: const EdgeInsets.symmetric(horizontal: 4),
        padding: const EdgeInsets.symmetric(vertical: 12),
        decoration: BoxDecoration(
          color: isDark ? AppColors.darkSurface : Colors.white,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(
            color: isDark ? AppColors.darkBorder : AppColors.border,
            width: 1,
          ),
        ),
        child: Column(
          children: [
            Text(
              title,
              style: theme.textTheme.bodySmall?.copyWith(
                color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              value,
              style: theme.textTheme.bodyMedium?.copyWith(
                fontWeight: FontWeight.bold,
                color: isDark ? Colors.white : Colors.black87,
              ),
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildBadge(BuildContext context, String text, IconData icon, Color color) {
    final theme = Theme.of(context);
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: color.withAlpha(20),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: color.withAlpha(80), width: 1),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 14, color: color),
          const SizedBox(width: 4),
          Text(
            text,
            style: theme.textTheme.bodySmall?.copyWith(
              color: color,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
    );
  }
}
