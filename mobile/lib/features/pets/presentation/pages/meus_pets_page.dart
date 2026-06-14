import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_state.dart';
import 'package:mybuddy_app/features/pets/domain/entities/pet.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/pets_cubit.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';
import 'package:mybuddy_app/shared/widgets/app_card.dart';

class MeusPetsPage extends StatefulWidget {
  const MeusPetsPage({super.key});

  @override
  State<MeusPetsPage> createState() => _MeusPetsPageState();
}

class _MeusPetsPageState extends State<MeusPetsPage> {
  List<Pet> _meusPets = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _carregarMeusPets();
  }

  Future<void> _carregarMeusPets() async {
    setState(() => _isLoading = true);

    final authState = context.read<AuthBloc>().state;
    String ongId = 'ong-id-123';
    if (authState is AuthAuthenticated) {
      ongId = authState.user.id;
    }

    final pets = await context.read<PetsCubit>().getPetsPorOng(ongId);
    if (mounted) {
      setState(() {
        _meusPets = pets;
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Meus Pets Cadastrados'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh_rounded),
            onPressed: _carregarMeusPets,
            tooltip: 'Atualizar lista',
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () async {
          await context.push('/cadastrar-pet');
          // Recarregar a lista ao voltar da tela de cadastro
          _carregarMeusPets();
        },
        icon: const Icon(Icons.add_rounded),
        label: const Text('Cadastrar Pet'),
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _meusPets.isEmpty
              ? Center(
                  child: Padding(
                    padding: const EdgeInsets.all(32.0),
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.pets, size: 72, color: Colors.grey.shade400),
                        const SizedBox(height: 16),
                        Text(
                          'Nenhum pet cadastrado ainda',
                          style: theme.textTheme.titleMedium?.copyWith(color: Colors.grey),
                          textAlign: TextAlign.center,
                        ),
                        const SizedBox(height: 8),
                        Text(
                          'Toque no botão "Cadastrar Pet" para adicionar o primeiro!',
                          textAlign: TextAlign.center,
                          style: theme.textTheme.bodyMedium?.copyWith(color: Colors.grey.shade500),
                        ),
                      ],
                    ),
                  ),
                )
              : RefreshIndicator(
                  onRefresh: _carregarMeusPets,
                  child: ListView.builder(
                    padding: const EdgeInsets.fromLTRB(24, 24, 24, 120),
                    itemCount: _meusPets.length,
                    itemBuilder: (context, index) {
                      final pet = _meusPets[index];
                      return Padding(
                        padding: const EdgeInsets.only(bottom: 16.0),
                        child: AppCard(
                          padding: const EdgeInsets.all(12),
                          child: Row(
                            children: [
                              ClipRRect(
                                borderRadius: BorderRadius.circular(10),
                                child: Image.network(
                                  pet.imagemUrl,
                                  height: 75,
                                  width: 75,
                                  fit: BoxFit.cover,
                                  errorBuilder: (context, error, stackTrace) => Container(
                                    height: 75,
                                    width: 75,
                                    color: isDark ? Colors.grey.shade800 : Colors.grey.shade200,
                                    child: const Icon(Icons.pets, color: Colors.grey),
                                  ),
                                ),
                              ),
                              const SizedBox(width: 16),
                              Expanded(
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Text(
                                      pet.nome,
                                      style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                                    ),
                                    const SizedBox(height: 4),
                                    Text(
                                      '${pet.especie} • ${pet.raca}',
                                      style: theme.textTheme.bodyMedium?.copyWith(
                                        color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                                      ),
                                    ),
                                    const SizedBox(height: 4),
                                    Row(
                                      children: [
                                        const Icon(Icons.location_on_outlined, size: 12, color: AppColors.primary),
                                        const SizedBox(width: 2),
                                        Text(
                                          '${pet.cidade} - ${pet.estado}',
                                          style: theme.textTheme.bodySmall?.copyWith(
                                            color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                                            fontSize: 11,
                                          ),
                                        ),
                                      ],
                                    ),
                                  ],
                                ),
                              ),
                              Column(
                                crossAxisAlignment: CrossAxisAlignment.end,
                                children: [
                                  Container(
                                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                                    decoration: BoxDecoration(
                                      color: Colors.green.withAlpha(20),
                                      borderRadius: BorderRadius.circular(20),
                                      border: Border.all(color: Colors.green.withAlpha(80)),
                                    ),
                                    child: const Text(
                                      'Disponível',
                                      style: TextStyle(
                                        color: Colors.green,
                                        fontSize: 10,
                                        fontWeight: FontWeight.bold,
                                      ),
                                    ),
                                  ),
                                  const SizedBox(height: 6),
                                  Text(
                                    '${pet.idade} ${pet.idade == 1 ? 'ano' : 'anos'}',
                                    style: theme.textTheme.bodySmall?.copyWith(
                                      color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                                      fontSize: 11,
                                    ),
                                  ),
                                ],
                              ),
                            ],
                          ),
                        ),
                      );
                    },
                  ),
                ),
    );
  }
}
