import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:mybuddy_app/features/pets/domain/entities/pet.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/favoritos_cubit.dart';
import 'package:mybuddy_app/shared/widgets/app_card.dart';
import 'package:mybuddy_app/shared/widgets/app_input.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';

class PetsPage extends StatefulWidget {
  const PetsPage({super.key});

  @override
  State<PetsPage> createState() => _PetsPageState();
}

class _PetsPageState extends State<PetsPage> {
  final _searchController = TextEditingController();
  String _selectedCategory = 'Todos';
  String _searchQuery = '';

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
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  List<Pet> get _filteredPets {
    return _allPets.where((pet) {
      final matchesSearch = pet.nome.toLowerCase().contains(_searchQuery.toLowerCase()) ||
          pet.raca.toLowerCase().contains(_searchQuery.toLowerCase());
      
      final matchesCategory = _selectedCategory == 'Todos' ||
          (physicsCategoryMap[_selectedCategory] == pet.especie);

      return matchesSearch && matchesCategory;
    }).toList();
  }

  static const physicsCategoryMap = {
    'Cães': 'Cachorro',
    'Gatos': 'Gato',
  };

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Scaffold(
      body: SafeArea(
        child: Column(
          children: [
            // Top Bar
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 16.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Encontre seu parceiro',
                        style: theme.textTheme.headlineSmall?.copyWith(
                          fontWeight: FontWeight.bold,
                          color: isDark ? Colors.white : Colors.black87,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        'Adote um amigo hoje',
                        style: theme.textTheme.bodyMedium?.copyWith(
                          color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                        ),
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
            ),

            // Search Bar
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24.0),
              child: AppInput(
                controller: _searchController,
                labelText: 'Buscar pets',
                hintText: 'Buscar por nome ou raça...',
                prefixIcon: Icons.search_rounded,
                onChanged: (value) {
                  setState(() {
                    _searchQuery = value;
                  });
                },
              ),
            ),
            const SizedBox(height: 16),

            // Categories horizontal list
            SizedBox(
              height: 44,
              child: ListView(
                scrollDirection: Axis.horizontal,
                padding: const EdgeInsets.symmetric(horizontal: 24.0),
                children: ['Todos', 'Cães', 'Gatos'].map((category) {
                  final isSelected = _selectedCategory == category;
                  return Padding(
                    padding: const EdgeInsets.only(right: 12.0),
                    child: GestureDetector(
                      onTap: () {
                        setState(() {
                          _selectedCategory = category;
                        });
                      },
                      child: AnimatedContainer(
                        duration: const Duration(milliseconds: 200),
                        padding: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 10.0),
                        decoration: BoxDecoration(
                          color: isSelected
                              ? AppColors.primary
                              : (isDark ? AppColors.darkSurface : Colors.white),
                          borderRadius: BorderRadius.circular(20),
                          border: Border.all(
                            color: isSelected
                                ? AppColors.primary
                                : (isDark ? AppColors.darkBorder : AppColors.border),
                            width: 1,
                          ),
                        ),
                        child: Row(
                          children: [
                            Icon(
                              category == 'Todos'
                                  ? Icons.grid_view_rounded
                                  : (category == 'Cães' ? Icons.pets : Icons.pets_outlined),
                              size: 16,
                              color: isSelected
                                  ? Colors.white
                                  : (isDark ? AppColors.darkTextPrimary : AppColors.textPrimary),
                            ),
                            const SizedBox(width: 8),
                            Text(
                              category,
                              style: theme.textTheme.bodyMedium?.copyWith(
                                fontWeight: FontWeight.bold,
                                color: isSelected
                                    ? Colors.white
                                    : (isDark ? AppColors.darkTextPrimary : AppColors.textPrimary),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  );
                }).toList(),
              ),
            ),
            const SizedBox(height: 16),

            // Grid Feed of Pets
            Expanded(
              child: BlocBuilder<FavoritosCubit, FavoritosState>(
                builder: (context, state) {
                  final pets = _filteredPets;

                  if (pets.isEmpty) {
                    return Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          const Icon(Icons.pets, size: 64, color: Colors.grey),
                          const SizedBox(height: 16),
                          Text(
                            'Nenhum pet encontrado',
                            style: theme.textTheme.titleMedium?.copyWith(color: Colors.grey),
                          ),
                        ],
                      ),
                    );
                  }

                  return GridView.builder(
                    padding: const EdgeInsets.all(24),
                    gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 2,
                      childAspectRatio: 0.72,
                      crossAxisSpacing: 16,
                      mainAxisSpacing: 16,
                    ),
                    itemCount: pets.length,
                    itemBuilder: (context, index) {
                      final pet = pets[index];
                      final isFav = context.read<FavoritosCubit>().isFavorito(pet);

                      return AppCard(
                        padding: EdgeInsets.zero,
                        onTap: () {
                          context.go('/pets/${pet.id}');
                        },
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.stretch,
                          children: [
                            // Pet Image & Favorite Icon
                            Expanded(
                              child: Stack(
                                fit: StackFit.expand,
                                children: [
                                  ClipRRect(
                                    borderRadius: const BorderRadius.vertical(top: Radius.circular(12)),
                                    child: Image.network(
                                      pet.imagemUrl,
                                      fit: BoxFit.cover,
                                      errorBuilder: (context, error, stackTrace) => Container(
                                        color: isDark ? Colors.grey.shade800 : Colors.grey.shade200,
                                        child: const Icon(Icons.broken_image_outlined, size: 40),
                                      ),
                                    ),
                                  ),
                                  Positioned(
                                    top: 8,
                                    right: 8,
                                    child: Material(
                                      color: Colors.white.withAlpha(200),
                                      shape: const CircleBorder(),
                                      elevation: 2,
                                      child: IconButton(
                                        icon: Icon(
                                          isFav ? Icons.favorite_rounded : Icons.favorite_border_rounded,
                                          color: isFav ? AppColors.primary : AppColors.textLight,
                                          size: 20,
                                        ),
                                        onPressed: () {
                                          context.read<FavoritosCubit>().toggleFavorito(pet);
                                        },
                                        constraints: const BoxConstraints(),
                                        padding: const EdgeInsets.all(6),
                                      ),
                                    ),
                                  ),
                                ],
                              ),
                            ),
                            // Pet Information
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
                    },
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}
