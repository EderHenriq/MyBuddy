import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_state.dart';
import 'package:mybuddy_app/features/pets/domain/entities/pet.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';
import 'package:mybuddy_app/shared/widgets/app_card.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  static final List<Pet> _destaquePets = [
    const Pet(
      id: '1',
      nome: 'Pipoca',
      especie: 'Cachorro',
      raca: 'Golden Retriever',
      idade: 2,
      sexo: 'Macho',
      porte: 'Grande',
      cor: 'Dourado',
      statusAdocao: 'DISPONIVEL',
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
      cor: 'Branco',
      statusAdocao: 'DISPONIVEL',
      cidade: 'Sarandi',
      estado: 'PR',
      imagemUrl: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=400&auto=format&fit=crop',
      vacinado: true,
      castrado: false,
    ),
    const Pet(
      id: '5',
      nome: 'Luna',
      especie: 'Gato',
      raca: 'Persa',
      idade: 2,
      sexo: 'Fêmea',
      porte: 'Pequeno',
      cor: 'Cinza',
      statusAdocao: 'DISPONIVEL',
      cidade: 'Maringá',
      estado: 'PR',
      imagemUrl: 'https://images.unsplash.com/photo-1618826411640-d6df44dd3f7a?q=80&w=400&auto=format&fit=crop',
      vacinado: true,
      castrado: true,
    ),
  ];

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    // Obter nome do usuário do estado de autenticação
    final authState = context.read<AuthBloc>().state;
    String userName = 'Eder Henrique';
    if (authState is AuthAuthenticated) {
      userName = authState.user.nome;
    }

    return Scaffold(
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Top Bar / Boas-vindas
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Row(
                    children: [
                      CircleAvatar(
                        radius: 22,
                        backgroundColor: AppColors.primary.withAlpha(20),
                        child: const Icon(Icons.person_rounded, color: AppColors.primary),
                      ),
                      const SizedBox(width: 12),
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Olá,',
                            style: theme.textTheme.bodySmall?.copyWith(
                              color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                            ),
                          ),
                          Text(
                            userName,
                            style: theme.textTheme.titleMedium?.copyWith(
                              fontWeight: FontWeight.bold,
                              color: isDark ? Colors.white : Colors.black87,
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                  Container(
                    decoration: BoxDecoration(
                      color: isDark ? AppColors.darkSurface : Colors.white,
                      shape: BoxShape.circle,
                      border: Border.all(
                        color: isDark ? AppColors.darkBorder : AppColors.border,
                        width: 1,
                      ),
                    ),
                    child: IconButton(
                      icon: const Icon(Icons.notifications_none_rounded, color: AppColors.primary),
                      onPressed: () => context.go('/notificacoes'),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 24),

              // Banner de Destaque
              AppCard(
                padding: EdgeInsets.zero,
                child: Container(
                  width: double.infinity,
                  height: 160,
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(12),
                    gradient: const LinearGradient(
                      colors: [AppColors.primary, Color(0xFFFFA07A)],
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                    ),
                  ),
                  child: Stack(
                    children: [
                      Positioned(
                        right: -10,
                        bottom: -10,
                        child: Icon(
                          Icons.pets_rounded,
                          size: 150,
                          color: Colors.white.withAlpha(30),
                        ),
                      ),
                      Padding(
                        padding: const EdgeInsets.all(20.0),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            const Text(
                              'Faça um novo amigo',
                              style: TextStyle(
                                color: Colors.white,
                                fontSize: 20,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                            const SizedBox(height: 6),
                            const Text(
                              'Adote e mude uma vida hoje.',
                              style: TextStyle(
                                color: Colors.white70,
                                fontSize: 13,
                              ),
                            ),
                            const SizedBox(height: 12),
                            ElevatedButton(
                              style: ElevatedButton.styleFrom(
                                backgroundColor: Colors.white,
                                foregroundColor: AppColors.primary,
                                shape: RoundedRectangleBorder(
                                  borderRadius: BorderRadius.circular(20),
                                ),
                                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                              ),
                              onPressed: () => context.go('/pets'),
                              child: const Text(
                                'Explorar Pets',
                                style: TextStyle(
                                  fontSize: 12,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 28),

              // Atalhos Rápidos
              Text(
                'Menu Rápido',
                style: theme.textTheme.titleMedium?.copyWith(
                  fontWeight: FontWeight.bold,
                  color: isDark ? Colors.white : Colors.black87,
                ),
              ),
              const SizedBox(height: 12),
              GridView.count(
                crossAxisCount: 2,
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                mainAxisSpacing: 12,
                crossAxisSpacing: 12,
                childAspectRatio: 1.5,
                children: [
                  _buildMenuCard(
                    context,
                    'Adotar Pet',
                    'Ache seu buddy',
                    Icons.pets_rounded,
                    AppColors.primary,
                    () => context.go('/pets'),
                    isDark,
                  ),
                  _buildMenuCard(
                    context,
                    'Eventos',
                    'Feiras de adoção',
                    Icons.event_note_rounded,
                    Colors.orange,
                    () => context.go('/eventos'),
                    isDark,
                  ),
                  _buildMenuCard(
                    context,
                    'Marketplace',
                    'Produtos de pet',
                    Icons.storefront_rounded,
                    Colors.blue,
                    () => context.go('/marketplace'),
                    isDark,
                  ),
                  _buildMenuCard(
                    context,
                    'Perfil',
                    'Ver minha conta',
                    Icons.person_rounded,
                    Colors.purple,
                    () => context.go('/perfil'),
                    isDark,
                  ),
                ],
              ),
              const SizedBox(height: 28),

              // Pets em Destaque
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    'Destaques de Adoção',
                    style: theme.textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: isDark ? Colors.white : Colors.black87,
                    ),
                  ),
                  TextButton(
                    onPressed: () => context.go('/pets'),
                    child: const Text('Ver todos', style: TextStyle(color: AppColors.primary)),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              SizedBox(
                height: 210,
                child: ListView.builder(
                  scrollDirection: Axis.horizontal,
                  itemCount: _destaquePets.length,
                  itemBuilder: (context, index) {
                    final pet = _destaquePets[index];
                    return GestureDetector(
                      onTap: () => context.go('/pets/${pet.id}'),
                      child: Container(
                        width: 140,
                        margin: const EdgeInsets.only(right: 16),
                        child: AppCard(
                          padding: EdgeInsets.zero,
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.stretch,
                            children: [
                              Expanded(
                                child: ClipRRect(
                                  borderRadius: const BorderRadius.vertical(top: Radius.circular(12)),
                                  child: Image.network(
                                    pet.imagemUrl,
                                    fit: BoxFit.cover,
                                    errorBuilder: (context, error, stackTrace) => Container(
                                      color: isDark ? Colors.grey.shade800 : Colors.grey.shade200,
                                      child: const Icon(Icons.broken_image_outlined),
                                    ),
                                  ),
                                ),
                              ),
                              Padding(
                                padding: const EdgeInsets.all(8.0),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Text(
                                      pet.nome,
                                      style: theme.textTheme.bodyMedium?.copyWith(
                                        fontWeight: FontWeight.bold,
                                        color: isDark ? Colors.white : Colors.black87,
                                      ),
                                      maxLines: 1,
                                      overflow: TextOverflow.ellipsis,
                                    ),
                                    const SizedBox(height: 2),
                                    Text(
                                      '${pet.cidade} - ${pet.estado}',
                                      style: theme.textTheme.bodySmall?.copyWith(
                                        color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                                        fontSize: 10,
                                      ),
                                      maxLines: 1,
                                      overflow: TextOverflow.ellipsis,
                                    ),
                                  ],
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                    );
                  },
                ),
              ),
              const SizedBox(height: 28),

              // Dica do Dia
              Text(
                'Dica de Cuidado',
                style: theme.textTheme.titleMedium?.copyWith(
                  fontWeight: FontWeight.bold,
                  color: isDark ? Colors.white : Colors.black87,
                ),
              ),
              const SizedBox(height: 12),
              AppCard(
                child: Row(
                  children: [
                    Container(
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: Colors.green.withAlpha(20),
                        shape: BoxShape.circle,
                      ),
                      child: const Icon(Icons.lightbulb_outline_rounded, color: Colors.green, size: 28),
                    ),
                    const SizedBox(width: 16),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Alimentação Saudável',
                            style: theme.textTheme.bodyLarge?.copyWith(fontWeight: FontWeight.bold),
                          ),
                          const SizedBox(height: 4),
                          Text(
                            'Certifique-se de dar porções equilibradas de ração para manter o peso correto do seu buddy.',
                            style: theme.textTheme.bodySmall?.copyWith(
                              color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                              height: 1.3,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildMenuCard(
    BuildContext context,
    String title,
    String subtitle,
    IconData icon,
    Color color,
    VoidCallback onTap,
    bool isDark,
  ) {
    final theme = Theme.of(context);
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(12),
      child: AppCard(
        padding: const EdgeInsets.all(12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Icon(icon, color: color, size: 28),
                Icon(
                  Icons.arrow_forward_rounded,
                  color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                  size: 16,
                ),
              ],
            ),
            const SizedBox(height: 10),
            Text(
              title,
              style: theme.textTheme.bodyMedium?.copyWith(
                fontWeight: FontWeight.bold,
                color: isDark ? Colors.white : Colors.black87,
              ),
            ),
            Text(
              subtitle,
              style: theme.textTheme.bodySmall?.copyWith(
                color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                fontSize: 10,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
