import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_state.dart';
import 'package:mybuddy_app/features/marketplace/domain/entities/produto.dart';
import 'package:mybuddy_app/features/marketplace/presentation/bloc/products_cubit.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';
import 'package:mybuddy_app/shared/widgets/app_button.dart';
import 'package:mybuddy_app/shared/widgets/app_card.dart';

class MarketplacePage extends StatefulWidget {
  const MarketplacePage({super.key});

  @override
  State<MarketplacePage> createState() => _MarketplacePageState();
}

class _MarketplacePageState extends State<MarketplacePage> {
  final TextEditingController _searchController = TextEditingController();
  String _searchQuery = '';
  String _selectedCategory = 'Todos';

  final List<String> _categories = [
    'Todos',
    'Alimentação',
    'Brinquedos',
    'Acessórios',
    'Higiene',
  ];

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  void _onSearchChanged(String query) {
    setState(() {
      _searchQuery = query;
    });
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    final authState = context.read<AuthBloc>().state;
    final loggedUser = authState is AuthAuthenticated ? authState.user : null;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Marketplace'),
        elevation: 0,
      ),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          // Campo de busca
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 12.0),
            child: Container(
              decoration: BoxDecoration(
                color: isDark ? AppColors.darkSurface : Colors.white,
                borderRadius: BorderRadius.circular(16),
                border: Border.all(
                  color: isDark ? AppColors.darkBorder : AppColors.border,
                  width: 1.2,
                ),
                boxShadow: [
                  BoxShadow(
                    color: isDark ? Colors.black26 : Colors.black12,
                    blurRadius: 6,
                    offset: const Offset(0, 3),
                  ),
                ],
              ),
              child: TextField(
                controller: _searchController,
                onChanged: _onSearchChanged,
                decoration: InputDecoration(
                  hintText: 'O que seu pet precisa hoje?',
                  hintStyle: TextStyle(
                    color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                  ),
                  prefixIcon: const Icon(Icons.search_rounded, color: AppColors.primary),
                  suffixIcon: _searchQuery.isNotEmpty
                      ? IconButton(
                          icon: const Icon(Icons.clear_rounded, color: AppColors.primary),
                          onPressed: () {
                            _searchController.clear();
                            _onSearchChanged('');
                          },
                        )
                      : null,
                  border: InputBorder.none,
                  contentPadding: const EdgeInsets.symmetric(vertical: 14.0),
                ),
              ),
            ),
          ),

          // Seletor de Categorias Horizontal
          SizedBox(
            height: 48,
            child: ListView.builder(
              scrollDirection: Axis.horizontal,
              padding: const EdgeInsets.symmetric(horizontal: 16.0),
              itemCount: _categories.length,
              itemBuilder: (context, index) {
                final category = _categories[index];
                final isSelected = _selectedCategory == category;
                return Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 6.0),
                  child: ChoiceChip(
                    label: Text(category),
                    selected: isSelected,
                    onSelected: (selected) {
                      if (selected) {
                        setState(() {
                          _selectedCategory = category;
                        });
                      }
                    },
                    selectedColor: AppColors.primary,
                    disabledColor: Colors.transparent,
                    backgroundColor: isDark ? AppColors.darkSurface : Colors.white,
                    labelStyle: TextStyle(
                      color: isSelected
                          ? Colors.white
                          : (isDark ? AppColors.darkTextPrimary : AppColors.textPrimary),
                      fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                    ),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(20),
                      side: BorderSide(
                        color: isSelected
                            ? Colors.transparent
                            : (isDark ? AppColors.darkBorder : AppColors.border),
                      ),
                    ),
                  ),
                );
              },
            ),
          ),

          const SizedBox(height: 12),

          // Grid de Produtos Reativo com ProductsCubit
          Expanded(
            child: BlocBuilder<ProductsCubit, ProductsState>(
              builder: (context, state) {
                if (state is ProductsLoading) {
                  return const Center(child: CircularProgressIndicator());
                }

                if (state is ProductsError) {
                  return Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        const Icon(Icons.error_outline_rounded, size: 64, color: AppColors.error),
                        const SizedBox(height: 16),
                        Text(state.message, style: const TextStyle(fontWeight: FontWeight.bold)),
                        const SizedBox(height: 8),
                        ElevatedButton(
                          onPressed: () => context.read<ProductsCubit>().loadProducts(),
                          child: const Text('Tentar Novamente'),
                        ),
                      ],
                    ),
                  );
                }

                if (state is ProductsLoaded) {
                  // Filtros
                  final filteredProducts = state.produtos.where((p) {
                    final matchesCategory = _selectedCategory == 'Todos' ||
                        p.categoria.toLowerCase() == _selectedCategory.toLowerCase();
                    final matchesSearch = p.nome.toLowerCase().contains(_searchQuery.toLowerCase()) ||
                        p.descricao.toLowerCase().contains(_searchQuery.toLowerCase());
                    return matchesCategory && matchesSearch;
                  }).toList();

                  if (filteredProducts.isEmpty) {
                    return Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(
                            Icons.storefront_outlined,
                            size: 72,
                            color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                          ),
                          const SizedBox(height: 16),
                          Text(
                            'Nenhum produto encontrado.',
                            style: theme.textTheme.titleMedium?.copyWith(
                              color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                            ),
                          ),
                        ],
                      ),
                    );
                  }

                  return GridView.builder(
                    padding: const EdgeInsets.all(20.0),
                    gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 2,
                      mainAxisSpacing: 16,
                      crossAxisSpacing: 16,
                      childAspectRatio: 0.65,
                    ),
                    itemCount: filteredProducts.length,
                    itemBuilder: (context, index) {
                      final produto = filteredProducts[index];
                      return _buildProductCard(context, produto, loggedUser, isDark);
                    },
                  );
                }

                return const Center(child: Text('Carregando produtos...'));
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildProductCard(
    BuildContext context,
    Produto produto,
    dynamic loggedUser,
    bool isDark,
  ) {
    final theme = Theme.of(context);
    return AppCard(
      padding: EdgeInsets.zero,
      borderRadius: 16,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          // Imagem do produto
          Expanded(
            child: ClipRRect(
              borderRadius: const BorderRadius.vertical(top: Radius.circular(16)),
              child: Stack(
                fit: StackFit.expand,
                children: [
                  Image.network(
                    produto.imagemUrl,
                    fit: BoxFit.cover,
                    errorBuilder: (context, error, stackTrace) {
                      return Container(
                        color: isDark ? AppColors.darkBorder : AppColors.background,
                        child: const Icon(
                          Icons.shopping_bag_outlined,
                          size: 40,
                          color: AppColors.primary,
                        ),
                      );
                    },
                  ),
                  Positioned(
                    top: 8,
                    left: 8,
                    child: Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(
                        color: AppColors.secondary.withOpacity(0.9),
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: Text(
                        produto.categoria,
                        style: const TextStyle(
                          color: Colors.white,
                          fontSize: 10,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),

          // Informações do produto
          Padding(
            padding: const EdgeInsets.all(12.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  produto.nome,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                  style: theme.textTheme.bodyMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                    height: 1.2,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  produto.descricao,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                  style: theme.textTheme.bodySmall?.copyWith(
                    color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                    fontSize: 11,
                  ),
                ),
                const SizedBox(height: 8),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Expanded(
                      child: Text(
                        'R\$ ${produto.preco.toStringAsFixed(2).replaceAll('.', ',')}',
                        style: const TextStyle(
                          color: AppColors.primary,
                          fontWeight: FontWeight.w800,
                          fontSize: 15,
                        ),
                      ),
                    ),
                    IconButton(
                      icon: const Icon(
                        Icons.add_shopping_cart_rounded,
                        color: AppColors.primary,
                        size: 22,
                      ),
                      onPressed: () => _confirmarCompra(context, produto, loggedUser),
                      padding: EdgeInsets.zero,
                      constraints: const BoxConstraints(),
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

  void _confirmarCompra(BuildContext context, Produto produto, dynamic loggedUser) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          backgroundColor: isDark ? AppColors.darkSurface : Colors.white,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
          title: const Row(
            children: [
              Icon(Icons.shopping_cart_checkout_rounded, color: AppColors.primary),
              SizedBox(width: 10),
              Text('Confirmar Compra'),
            ],
          ),
          content: Text(
            'Deseja comprar o produto "${produto.nome}" por R\$ ${produto.preco.toStringAsFixed(2).replaceAll('.', ',')}?',
            style: TextStyle(
              color: isDark ? AppColors.darkTextPrimary : AppColors.textPrimary,
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Cancelar', style: TextStyle(color: Colors.grey)),
            ),
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.primary,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              ),
              onPressed: () async {
                Navigator.pop(context); // Fecha dialog de confirmação

                // Mostrar Loading
                showDialog(
                  context: context,
                  barrierDismissible: false,
                  builder: (context) => const Center(child: CircularProgressIndicator()),
                );

                final productsCubit = context.read<ProductsCubit>();
                final clienteNome = loggedUser?.nome ?? 'Adotante MyBuddy';

                final success = await productsCubit.comprarProduto(
                  clienteNome,
                  produto.nome,
                  produto.preco,
                );

                if (context.mounted) {
                  Navigator.pop(context); // Fecha loading

                  if (success) {
                    showDialog(
                      context: context,
                      builder: (context) => AlertDialog(
                        backgroundColor: isDark ? AppColors.darkSurface : Colors.white,
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                        title: const Row(
                          children: [
                            Icon(Icons.check_circle_outline_rounded, color: AppColors.success),
                            SizedBox(width: 10),
                            Text('Sucesso!'),
                          ],
                        ),
                        content: const Text(
                          'Sua compra foi realizada com sucesso! O pedido foi enviado para o Petshop parceiro e você pode acompanhar o status em "Meu Perfil > Minhas Compras".',
                        ),
                        actions: [
                          ElevatedButton(
                            style: ElevatedButton.styleFrom(
                              backgroundColor: AppColors.success,
                              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                            ),
                            onPressed: () => Navigator.pop(context),
                            child: const Text('Ok', style: TextStyle(color: Colors.white)),
                          ),
                        ],
                      ),
                    );
                  } else {
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(
                        content: Text('Erro ao realizar a compra. Tente novamente.'),
                        backgroundColor: AppColors.error,
                      ),
                    );
                  }
                }
              },
              child: const Text('Confirmar', style: TextStyle(color: Colors.white)),
            ),
          ],
        );
      },
    );
  }
}
