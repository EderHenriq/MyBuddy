import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:mybuddy_app/features/marketplace/domain/entities/produto.dart';
import 'package:mybuddy_app/features/marketplace/presentation/bloc/products_cubit.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';
import 'package:mybuddy_app/shared/widgets/app_button.dart';
import 'package:mybuddy_app/shared/widgets/app_input.dart';

class CadastrarProdutoPage extends StatefulWidget {
  const CadastrarProdutoPage({super.key});

  @override
  State<CadastrarProdutoPage> createState() => _CadastrarProdutoPageState();
}

class _CadastrarProdutoPageState extends State<CadastrarProdutoPage> {
  final _formKey = GlobalKey<FormState>();
  final _nomeController = TextEditingController();
  final _precoController = TextEditingController();
  final _descricaoController = TextEditingController();
  final _imagemUrlController = TextEditingController();

  String _categoria = 'Rações';
  bool _isLoading = false;

  @override
  void dispose() {
    _nomeController.dispose();
    _precoController.dispose();
    _descricaoController.dispose();
    _imagemUrlController.dispose();
    super.dispose();
  }

  void _salvarProduto() async {
    if (_formKey.currentState?.validate() ?? false) {
      setState(() => _isLoading = true);

      final produto = Produto(
        id: '',
        nome: _nomeController.text.trim(),
        preco: double.parse(_precoController.text.trim().replaceAll(',', '.')),
        descricao: _descricaoController.text.trim(),
        imagemUrl: _imagemUrlController.text.trim(),
        categoria: _categoria,
      );

      final success = await context.read<ProductsCubit>().cadastrarProduto(produto);

      setState(() => _isLoading = false);

      if (mounted) {
        if (success) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('Produto cadastrado com sucesso!'),
              backgroundColor: AppColors.success,
            ),
          );
          context.pop();
        } else {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('Erro ao cadastrar produto. Tente novamente.'),
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
        title: const Text('Cadastrar Produto'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Text(
                'Cadastre itens para vender no Marketplace',
                style: theme.textTheme.titleMedium?.copyWith(
                  color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                ),
              ),
              const SizedBox(height: 24),

              // Nome
              AppInput(
                controller: _nomeController,
                labelText: 'Nome do Produto',
                prefixIcon: Icons.shopping_bag_outlined,
                validator: (value) {
                  if (value == null || value.trim().isEmpty) return 'Informe o nome';
                  return null;
                },
              ),
              const SizedBox(height: 16),

              // Preço
              AppInput(
                controller: _precoController,
                labelText: 'Preço (R\$)',
                prefixIcon: Icons.attach_money_rounded,
                keyboardType: const TextInputType.numberWithOptions(decimal: true),
                validator: (value) {
                  if (value == null || value.trim().isEmpty) return 'Informe o preço';
                  final cleanVal = value.trim().replaceAll(',', '.');
                  if (double.tryParse(cleanVal) == null) return 'Digite um preço válido';
                  return null;
                },
              ),
              const SizedBox(height: 16),

              // Categoria (Dropdown)
              DropdownButtonFormField<String>(
                initialValue: _categoria,
                decoration: InputDecoration(
                  labelText: 'Categoria',
                  prefixIcon: const Icon(Icons.tag_rounded, color: AppColors.primary),
                  border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                ),
                items: const [
                  DropdownMenuItem(value: 'Rações', child: Text('Rações')),
                  DropdownMenuItem(value: 'Petiscos', child: Text('Petiscos')),
                  DropdownMenuItem(value: 'Brinquedos', child: Text('Brinquedos')),
                  DropdownMenuItem(value: 'Farmácia', child: Text('Farmácia')),
                  DropdownMenuItem(value: 'Higiene', child: Text('Higiene')),
                  DropdownMenuItem(value: 'Camas', child: Text('Camas')),
                ],
                onChanged: (val) {
                  if (val != null) setState(() => _categoria = val);
                },
              ),
              const SizedBox(height: 16),

              // Descrição
              AppInput(
                controller: _descricaoController,
                labelText: 'Descrição',
                prefixIcon: Icons.description_outlined,
                maxLines: 3,
                validator: (value) {
                  if (value == null || value.trim().isEmpty) return 'Informe a descrição do produto';
                  return null;
                },
              ),
              const SizedBox(height: 16),

              // Imagem URL
              AppInput(
                controller: _imagemUrlController,
                labelText: 'URL da Imagem (opcional)',
                prefixIcon: Icons.image_outlined,
                hintText: 'Link da foto na internet...',
              ),
              const SizedBox(height: 32),

              // Botão Salvar
              AppButton(
                text: _isLoading ? 'Salvando...' : 'Cadastrar Produto',
                onPressed: _isLoading ? () {} : _salvarProduto,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
