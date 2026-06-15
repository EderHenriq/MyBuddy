import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:mybuddy_app/features/adocao/presentation/bloc/adocao_cubit.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_state.dart';
import 'package:mybuddy_app/features/pets/domain/entities/pet.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/pets_cubit.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';
import 'package:mybuddy_app/shared/widgets/app_button.dart';
import 'package:mybuddy_app/shared/widgets/app_input.dart';
import 'dart:io';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mybuddy_app/core/di/injection_container.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/image_picker_cubit.dart';
import 'package:mybuddy_app/shared/widgets/image_picker_widget.dart';

class CadastrarPetPage extends StatefulWidget {
  const CadastrarPetPage({super.key});

  @override
  State<CadastrarPetPage> createState() => _CadastrarPetPageState();
}

class _CadastrarPetPageState extends State<CadastrarPetPage> {
  final _formKey = GlobalKey<FormState>();
  final _nomeController = TextEditingController();
  final _racaController = TextEditingController();
  final _idadeController = TextEditingController();
  final _cidadeController = TextEditingController();
  final _estadoController = TextEditingController();
  final _imagemUrlController = TextEditingController();
  File? _selectedImageFile;

  String _especie = 'Cachorro';
  String _sexo = 'Macho';
  String _porte = 'Médio';
  bool _vacinado = false;
  bool _castrado = false;
  bool _isLoading = false;

  @override
  void dispose() {
    _nomeController.dispose();
    _racaController.dispose();
    _idadeController.dispose();
    _cidadeController.dispose();
    _estadoController.dispose();
    _selectedImageFile = null;
    super.dispose();
  }

  void _salvarPet() async {
    if (_formKey.currentState?.validate() ?? false) {
      setState(() => _isLoading = true);

      final pet = Pet(
        id: '',
        nome: _nomeController.text.trim(),
        especie: _especie,
        raca: _racaController.text.trim(),
        idade: int.parse(_idadeController.text.trim()),
        sexo: _sexo,
        porte: _porte,
        cidade: _cidadeController.text.trim(),
        estado: _estadoController.text.trim().toUpperCase(),
        imagemUrl: _selectedImageFile?.path ?? '',
        vacinado: _vacinado,
        castrado: _castrado,
      );

      // Pega o ID da ONG logada para associar ao pet
      final authState = context.read<AuthBloc>().state;
      String? ongId;
      if (authState is AuthAuthenticated) {
        ongId = authState.user.id;
      }

      final petsCubit = context.read<PetsCubit>();
      final adocaoCubit = context.read<AdocaoCubit>();

      final success = await petsCubit.cadastrarPet(pet, ongId: ongId);

      if (success && ongId != null) {
        // Registra a associação petId -> ongId no AdocaoCubit
        // O novo pet tem um ID gerado por timestamp; buscamos o último pet cadastrado
        final petsState = petsCubit.state;
        if (petsState is PetsLoaded && petsState.pets.isNotEmpty) {
          final newPetId = petsState.pets.last.id;
          adocaoCubit.registrarPetOng(newPetId, ongId);
        }
      }

      setState(() => _isLoading = false);

      if (mounted) {
        if (success) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('Pet cadastrado com sucesso!'),
              backgroundColor: AppColors.success,
            ),
          );
          context.pop();
        } else {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('Erro ao cadastrar pet. Tente novamente.'),
              backgroundColor: Colors.red,
            ),
          );
        }
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Cadastrar Pet'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Text(
                'Cadastre um novo amigo para adoção',
                style: theme.textTheme.titleMedium?.copyWith(
                  color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                ),
              ),
              const SizedBox(height: 24),

              // Nome
              AppInput(
                controller: _nomeController,
                labelText: 'Nome do Pet',
                prefixIcon: Icons.pets_rounded,
                validator: (value) {
                  if (value == null || value.trim().isEmpty) return 'Informe o nome do pet';
                  return null;
                },
              ),
              const SizedBox(height: 16),

              // Espécie (Dropdown)
              DropdownButtonFormField<String>(
                value: _especie,
                decoration: InputDecoration(
                  labelText: 'Espécie',
                  prefixIcon: const Icon(Icons.category_outlined, color: AppColors.primary),
                  border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                ),
                items: const [
                  DropdownMenuItem(value: 'Cachorro', child: Text('Cachorro')),
                  DropdownMenuItem(value: 'Gato', child: Text('Gato')),
                ],
                onChanged: (val) {
                  if (val != null) setState(() => _especie = val);
                },
              ),
              const SizedBox(height: 16),

              // Raça
              AppInput(
                controller: _racaController,
                labelText: 'Raça',
                prefixIcon: Icons.info_outline,
                validator: (value) {
                  if (value == null || value.trim().isEmpty) return 'Informe a raça (ou escreva Vira-lata)';
                  return null;
                },
              ),
              const SizedBox(height: 16),

              // Idade
              AppInput(
                controller: _idadeController,
                labelText: 'Idade (em anos)',
                prefixIcon: Icons.calendar_today_outlined,
                keyboardType: TextInputType.number,
                validator: (value) {
                  if (value == null || value.trim().isEmpty) return 'Informe a idade';
                  if (int.tryParse(value) == null) return 'Digite um número válido';
                  return null;
                },
              ),
              const SizedBox(height: 16),

              // Sexo (Dropdown)
              DropdownButtonFormField<String>(
                value: _sexo,
                decoration: InputDecoration(
                  labelText: 'Sexo',
                  prefixIcon: const Icon(Icons.transgender_outlined, color: AppColors.primary),
                  border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                ),
                items: const [
                  DropdownMenuItem(value: 'Macho', child: Text('Macho')),
                  DropdownMenuItem(value: 'Fêmea', child: Text('Fêmea')),
                ],
                onChanged: (val) {
                  if (val != null) setState(() => _sexo = val);
                },
              ),
              const SizedBox(height: 16),

              // Porte (Dropdown)
              DropdownButtonFormField<String>(
                value: _porte,
                decoration: InputDecoration(
                  labelText: 'Porte',
                  prefixIcon: const Icon(Icons.height_outlined, color: AppColors.primary),
                  border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                ),
                items: const [
                  DropdownMenuItem(value: 'Pequeno', child: Text('Pequeno')),
                  DropdownMenuItem(value: 'Médio', child: Text('Médio')),
                  DropdownMenuItem(value: 'Grande', child: Text('Grande')),
                ],
                onChanged: (val) {
                  if (val != null) setState(() => _porte = val);
                },
              ),
              const SizedBox(height: 16),

              // Cidade e Estado (Lado a Lado)
              Row(
                children: [
                  Expanded(
                    flex: 3,
                    child: AppInput(
                      controller: _cidadeController,
                      labelText: 'Cidade',
                      prefixIcon: Icons.location_city_outlined,
                      validator: (value) {
                        if (value == null || value.trim().isEmpty) return 'Cidade';
                        return null;
                      },
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    flex: 1,
                    child: AppInput(
                      controller: _estadoController,
                      labelText: 'UF',
                      keyboardType: TextInputType.text,
                      validator: (value) {
                        if (value == null || value.trim().isEmpty) return 'UF';
                        if (value.length != 2) return '2 letras';
                        return null;
                      },
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),

              // Imagem URL
              BlocProvider(
                create: (context) => sl<ImagePickerCubit>(),
                child: ImagePickerWidget(
                  label: 'Foto do Pet',
                  onImageSelected:(file) {
                    setState(() => _selectedImageFile = file);
                  },
                ),
              ),

              const SizedBox(height: 24),

              // Checkboxes de Vacinado e Castrado
              CheckboxListTile(
                title: const Text('O animal já está vacinado?'),
                activeColor: AppColors.primary,
                value: _vacinado,
                onChanged: (val) {
                  if (val != null) setState(() => _vacinado = val);
                },
                controlAffinity: ListTileControlAffinity.leading,
                contentPadding: EdgeInsets.zero,
              ),
              CheckboxListTile(
                title: const Text('O animal já está castrado?'),
                activeColor: AppColors.primary,
                value: _castrado,
                onChanged: (val) {
                  if (val != null) setState(() => _castrado = val);
                },
                controlAffinity: ListTileControlAffinity.leading,
                contentPadding: EdgeInsets.zero,
              ),
              const SizedBox(height: 32),

              // Botão Salvar
              AppButton(
                text: _isLoading ? 'Salvando...' : 'Cadastrar Pet',
                onPressed: _isLoading ? () {} : _salvarPet,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
