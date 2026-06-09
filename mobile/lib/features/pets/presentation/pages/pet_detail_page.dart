import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:mybuddy_app/features/pets/domain/entities/pet.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/favoritos_cubit.dart';
import 'package:mybuddy_app/shared/widgets/app_button.dart';
import 'package:mybuddy_app/shared/widgets/app_card.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';

class PetDetailPage extends StatelessWidget {
  final String petId;

  const PetDetailPage({super.key, required this.petId});

  static final List<Pet> _allPets = [
    const Pet(
      id: '1',
      nome: 'Pipoca',
      especie: 'Cachorro',
      raca: 'Golden Retriever',
      idade: 2,
      sexo: 'Macho',
      porte: 'Grande',
      cidade: 'Maringá',
      estado: 'PR',
      imagemUrl: 'https://images.unsplash.com/photo-1552053831-71594a27632d?q=80&w=400&auto=format&fit=crop',
      vacinado: true,
      castrado: true,
    ),
    const Pet(
      id: '2',
      nome: 'Mia',
      especie: 'Gato',
      raca: 'Siamês',
      idade: 1,
      sexo: 'Fêmea',
      porte: 'Pequeno',
      cidade: 'Sarandi',
      estado: 'PR',
      imagemUrl: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=400&auto=format&fit=crop',
      vacinado: true,
      castrado: false,
    ),
    const Pet(
      id: '3',
      nome: 'Bidu',
      especie: 'Cachorro',
      raca: 'Poodle',
      idade: 4,
      sexo: 'Macho',
      porte: 'Médio',
      cidade: 'Maringá',
      estado: 'PR',
      imagemUrl: 'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?q=80&w=400&auto=format&fit=crop',
      vacinado: false,
      castrado: true,
    ),
    const Pet(
      id: '4',
      nome: 'Thor',
      especie: 'Cachorro',
      raca: 'Pastor Alemão',
      idade: 3,
      sexo: 'Macho',
      porte: 'Grande',
      cidade: 'Londrina',
      estado: 'PR',
      imagemUrl: 'https://images.unsplash.com/photo-1589941013453-ec89f33b5e95?q=80&w=400&auto=format&fit=crop',
      vacinado: true,
      castrado: true,
    ),
    const Pet(
      id: '5',
      nome: 'Luna',
      especie: 'Gato',
      raca: 'Persa',
      idade: 2,
      sexo: 'Fêmea',
      porte: 'Pequeno',
      cidade: 'Maringá',
      estado: 'PR',
      imagemUrl: 'https://images.unsplash.com/photo-1618826411640-d6df44dd3f7a?q=80&w=400&auto=format&fit=crop',
      vacinado: true,
      castrado: true,
    ),
    const Pet(
      id: '6',
      nome: 'Simba',
      especie: 'Gato',
      raca: 'Vira-lata',
      idade: 1,
      sexo: 'Macho',
      porte: 'Médio',
      cidade: 'Paranavaí',
      estado: 'PR',
      imagemUrl: 'https://images.unsplash.com/photo-1519052537078-e6302a4968d4?q=80&w=400&auto=format&fit=crop',
      vacinado: false,
      castrado: true,
    ),
  ];

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    final pet = _allPets.firstWhere(
      (p) => p.id == petId,
      orElse: () => _allPets.first,
    );

    void showAdoptionDialog() {
      showModalBottomSheet(
        context: context,
        backgroundColor: Colors.transparent,
        builder: (context) {
          return Container(
            decoration: BoxDecoration(
              color: isDark ? AppColors.darkSurface : Colors.white,
              borderRadius: const BorderRadius.vertical(top: Radius.circular(24)),
            ),
            padding: const EdgeInsets.all(32),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Container(
                  width: 50,
                  height: 5,
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
                  'A ONG responsável pelo(a) ${pet.nome} foi notificada e entrará em contato com você em breve para realizar a entrevista de adoção.',
                  textAlign: TextAlign.center,
                  style: theme.textTheme.bodyMedium?.copyWith(
                    color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                    height: 1.4,
                  ),
                ),
                const SizedBox(height: 32),
                AppButton(
                  text: 'Entendido',
                  onPressed: () {
                    Navigator.pop(context);
                    context.go('/pets');
                  },
                ),
              ],
            ),
          );
        },
      );
    }

    return Scaffold(
      body: Stack(
        children: [
          // Background/Scrollable Content
          SingleChildScrollView(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                // Hero Image
                Hero(
                  tag: 'pet_img_${pet.id}',
                  child: Image.network(
                    pet.imagemUrl,
                    height: 380,
                    fit: BoxFit.cover,
                  ),
                ),
                
                // Pet Info Overlay Container
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
                                  pet.nome,
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
                                      '${pet.cidade} - ${pet.estado}',
                                      style: theme.textTheme.bodyMedium?.copyWith(
                                        color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                                      ),
                                    ),
                                  ],
                                ),
                              ],
                            ),
                            Icon(
                              pet.sexo == 'Macho' ? Icons.male_rounded : Icons.female_rounded,
                              size: 32,
                              color: pet.sexo == 'Macho' ? Colors.blue : Colors.pink,
                            ),
                          ],
                        ),
                        const SizedBox(height: 24),
                        
                        // Technical specs row
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            _buildSpecCard(context, 'Raça', pet.raca, isDark),
                            _buildSpecCard(context, 'Idade', '${pet.idade} ${pet.idade == 1 ? 'ano' : 'anos'}', isDark),
                            _buildSpecCard(context, 'Porte', pet.porte, isDark),
                          ],
                        ),
                        const SizedBox(height: 32),

                        // Vaccine & Castration Badges
                        Row(
                          children: [
                            if (pet.vacinado)
                              _buildBadge(context, 'Vacinado', Icons.check_circle_outline, Colors.green),
                            if (!pet.vacinado)
                              _buildBadge(context, 'Vacina Pendente', Icons.info_outline, Colors.orange),
                            const SizedBox(width: 12),
                            if (pet.castrado)
                              _buildBadge(context, 'Castrado', Icons.check_circle_outline, Colors.green),
                            if (!pet.castrado)
                              _buildBadge(context, 'Não Castrado', Icons.info_outline, Colors.orange),
                          ],
                        ),
                        const SizedBox(height: 32),

                        // About description
                        Text(
                          'Sobre mim',
                          style: theme.textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
                        ),
                        const SizedBox(height: 12),
                        Text(
                          'Olá! Sou o(a) ${pet.nome}, um(a) lindo(a) ${pet.especie} da raça ${pet.raca}. Estou à procura de uma família que me dê muito amor, carinho e espaço para brincar. Sou muito brincalhão(a), me dou bem com outros animais e adoro passear ao ar livre. Venha me conhecer!',
                          style: theme.textTheme.bodyLarge?.copyWith(
                            color: isDark ? AppColors.darkTextPrimary : AppColors.textPrimary,
                            height: 1.5,
                          ),
                        ),
                        const SizedBox(height: 32),

                        // Caregiver / NGO Info Card
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
                        const SizedBox(height: 100), // Spacing for bottom button
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
                onPressed: () => context.go('/pets'),
              ),
            ),
          ),

          // Floating Favorite Button
          Positioned(
            top: 48,
            right: 20,
            child: BlocBuilder<FavoritosCubit, FavoritosState>(
              builder: (context, state) {
                final isFav = context.read<FavoritosCubit>().isFavorito(pet);
                return CircleAvatar(
                  backgroundColor: Colors.white.withAlpha(200),
                  child: IconButton(
                    icon: Icon(
                      isFav ? Icons.favorite_rounded : Icons.favorite_border_rounded,
                      color: isFav ? AppColors.primary : Colors.black87,
                      size: 20,
                    ),
                    onPressed: () {
                      context.read<FavoritosCubit>().toggleFavorito(pet);
                    },
                  ),
                );
              },
            ),
          ),

          // Bottom Fixed Action Button
          Positioned(
            bottom: 24,
            left: 24,
            right: 24,
            child: AppButton(
              text: 'Quero Adotar',
              onPressed: showAdoptionDialog,
            ),
          ),
        ],
      ),
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
