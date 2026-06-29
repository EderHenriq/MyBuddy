import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:mybuddy_app/features/pets/domain/entities/pet.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/favoritos_cubit.dart';
import 'package:mybuddy_app/shared/widgets/app_card.dart';
import 'package:mybuddy_app/shared/widgets/app_button.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';
import 'package:mybuddy_app/shared/widgets/app_image.dart';

class FavoritosPage extends StatelessWidget {
  const FavoritosPage({super.key});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Meus Favoritos'),
      ),
      body: BlocBuilder<FavoritosCubit, FavoritosState>(
        builder: (context, state) {
          if (state.favoritos.isEmpty) {
            return _buildEmptyState(context, isDark, theme);
          }

          return GridView.builder(
            padding: const EdgeInsets.all(16),
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 2,
              childAspectRatio: 0.72,
              crossAxisSpacing: 16,
              mainAxisSpacing: 16,
            ),
            itemCount: state.favoritos.length,
            itemBuilder: (context, index) {
              final pet = state.favoritos[index];
              return _buildPetCard(context, pet, isDark, theme);
            },
          );
        },
      ),
    );
  }

  Widget _buildEmptyState(BuildContext context, bool isDark, ThemeData theme) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(32.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              height: 120,
              width: 120,
              decoration: BoxDecoration(
                color: AppColors.primary.withAlpha(15),
                shape: BoxShape.circle,
              ),
              child: const Icon(
                Icons.favorite_border_rounded,
                size: 60,
                color: AppColors.primary,
              ),
            ),
            const SizedBox(height: 24),
            Text(
              'Nenhum pet favoritado',
              style: theme.textTheme.titleLarge?.copyWith(
                fontWeight: FontWeight.bold,
                color: isDark ? Colors.white : Colors.black87,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'Os pets que você favoritar na tela inicial aparecerão aqui para fácil acesso.',
              textAlign: TextAlign.center,
              style: theme.textTheme.bodyMedium?.copyWith(
                color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                height: 1.4,
              ),
            ),
            const SizedBox(height: 32),
            AppButton(
              text: 'Explorar Pets',
              width: 180,
              onPressed: () {
                context.go('/pets');
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildPetCard(BuildContext context, Pet pet, bool isDark, ThemeData theme) {
    return AppCard(
      padding: EdgeInsets.zero,
      onTap: () {
        // Para futuras implementações de detalhes (MY-330)
      },
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          // Imagem do Pet com botão de desfavoritar sobreposto
          Expanded(
            child: Stack(
              fit: StackFit.expand,
              children: [
                ClipRRect(
                  borderRadius: const BorderRadius.vertical(top: Radius.circular(12)),
                  child: AppImage(
                    imageUrl: pet.imagemUrl,
                    fit: BoxFit.cover,
                  ),
                ),
                // Botão de Favorito (Sempre preenchido aqui porque está na tela de favoritos)
                Positioned(
                  top: 8,
                  right: 8,
                  child: Material(
                    color: Colors.white.withAlpha(200),
                    shape: const CircleBorder(),
                    elevation: 2,
                    child: IconButton(
                      icon: const Icon(
                        Icons.favorite_rounded,
                        color: AppColors.primary,
                        size: 20,
                      ),
                      onPressed: () {
                        context.read<FavoritosCubit>().toggleFavorito(pet);
                        ScaffoldMessenger.of(context).showSnackBar(
                          SnackBar(
                            content: Text('${pet.nome} foi removido dos favoritos.'),
                            duration: const Duration(seconds: 2),
                            action: SnackBarAction(
                              label: 'Desfazer',
                              onPressed: () {
                                context.read<FavoritosCubit>().toggleFavorito(pet);
                              },
                            ),
                          ),
                        );
                      },
                      constraints: const BoxConstraints(),
                      padding: const EdgeInsets.all(6),
                    ),
                  ),
                ),
              ],
            ),
          ),
          
          // Dados do Pet
          Padding(
            padding: const EdgeInsets.all(12),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Expanded(
                      child: Text(
                        pet.nome,
                        style: theme.textTheme.titleMedium?.copyWith(
                          fontWeight: FontWeight.bold,
                          color: isDark ? Colors.white : Colors.black87,
                        ),
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                      ),
                    ),
                    Icon(
                      pet.sexo == 'Macho' ? Icons.male_rounded : Icons.female_rounded,
                      size: 18,
                      color: pet.sexo == 'Macho' ? Colors.blue : Colors.pink,
                    ),
                  ],
                ),
                const SizedBox(height: 2),
                Text(
                  pet.raca,
                  style: theme.textTheme.bodySmall?.copyWith(
                    color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                    fontWeight: FontWeight.w500,
                  ),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
                const SizedBox(height: 6),
                Row(
                  children: [
                    const Icon(
                      Icons.location_on_outlined,
                      size: 14,
                      color: AppColors.primary,
                    ),
                    const SizedBox(width: 2),
                    Expanded(
                      child: Text(
                        '${pet.cidade} - ${pet.estado}',
                        style: theme.textTheme.bodySmall?.copyWith(
                          color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                          fontSize: 11,
                        ),
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
