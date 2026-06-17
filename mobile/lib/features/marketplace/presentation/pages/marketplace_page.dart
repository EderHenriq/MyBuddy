import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_state.dart';
import 'package:mybuddy_app/features/marketplace/domain/entities/produto.dart';
import 'package:mybuddy_app/features/marketplace/presentation/bloc/products_cubit.dart';
import 'package:mybuddy_app/features/marketplace/presentation/bloc/cart_cubit.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';
import 'package:mybuddy_app/shared/widgets/app_card.dart';
import 'package:mybuddy_app/shared/widgets/app_button.dart';

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
    'Rações',
    'Petiscos',
    'Brinquedos',
    'Farmácia',
    'Higiene',
    'Camas',
  ];

  final Map<String, IconData> _categoryIcons = {
    'Todos': Icons.grid_view_rounded,
    'Rações': Icons.pets_rounded,
    'Petiscos': Icons.cookie_rounded,
    'Brinquedos': Icons.sports_soccer_rounded,
    'Farmácia': Icons.medical_services_rounded,
    'Higiene': Icons.bathtub_rounded,
    'Camas': Icons.bed_rounded,
  };

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

  void _showCartBottomSheet(BuildContext context, CartState cartState) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.transparent,
      isScrollControlled: true,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setModalState) {
            return BlocBuilder<CartCubit, CartState>(
              builder: (context, state) {
                if (state.items.isEmpty) {
                  return Container(
                    height: 250,
                    decoration: BoxDecoration(
                      color: isDark ? AppColors.darkSurface : Colors.white,
                      borderRadius: const BorderRadius.vertical(top: Radius.circular(24)),
                    ),
                    padding: const EdgeInsets.all(24),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.stretch,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Row(
                              children: [
                                const Icon(Icons.shopping_cart_rounded, color: AppColors.primary),
                                const SizedBox(width: 10),
                                Text(
                                  'Seu Carrinho',
                                  style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                                ),
                              ],
                            ),
                            IconButton(
                              icon: const Icon(Icons.close_rounded),
                              onPressed: () => Navigator.pop(context),
                            ),
                          ],
                        ),
                        const Divider(),
                        const Expanded(
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Icon(Icons.remove_shopping_cart_outlined, size: 48, color: Colors.grey),
                              SizedBox(height: 12),
                              Text(
                                'Seu carrinho está vazio.',
                                style: TextStyle(color: Colors.grey, fontSize: 14, fontWeight: FontWeight.bold),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  );
                }

                return Container(
                  height: MediaQuery.of(context).size.height * 0.65,
                  decoration: BoxDecoration(
                    color: isDark ? AppColors.darkSurface : Colors.white,
                    borderRadius: const BorderRadius.vertical(top: Radius.circular(24)),
                  ),
                  padding: const EdgeInsets.all(24),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Row(
                            children: [
                              const Icon(Icons.shopping_cart_rounded, color: AppColors.primary),
                              const SizedBox(width: 10),
                              Text(
                                'Seu Carrinho',
                                style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                              ),
                            ],
                          ),
                          IconButton(
                            icon: const Icon(Icons.close_rounded),
                            onPressed: () => Navigator.pop(context),
                          ),
                        ],
                      ),
                      const Divider(),
                      Expanded(
                        child: ListView.builder(
                          itemCount: state.items.length,
                          itemBuilder: (context, index) {
                            final item = state.items[index];
                            return Padding(
                              padding: const EdgeInsets.symmetric(vertical: 10.0),
                              child: Row(
                                children: [
                                  ClipRRect(
                                    borderRadius: BorderRadius.circular(8),
                                    child: Image.network(
                                      item.produto.imagemUrl,
                                      width: 50,
                                      height: 50,
                                      fit: BoxFit.cover,
                                      errorBuilder: (context, error, stackTrace) => Container(
                                        width: 50,
                                        height: 50,
                                        color: Colors.grey[200],
                                        child: const Icon(Icons.shopping_bag_outlined),
                                      ),
                                    ),
                                  ),
                                  const SizedBox(width: 12),
                                  Expanded(
                                    child: Column(
                                      crossAxisAlignment: CrossAxisAlignment.start,
                                      children: [
                                        Text(
                                          item.produto.nome,
                                          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13),
                                          maxLines: 1,
                                          overflow: TextOverflow.ellipsis,
                                        ),
                                        const SizedBox(height: 2),
                                        Text(
                                          'R\$ ${item.produto.preco.toStringAsFixed(2).replaceAll('.', ',')}',
                                          style: const TextStyle(color: AppColors.primary, fontSize: 12, fontWeight: FontWeight.bold),
                                        ),
                                      ],
                                    ),
                                  ),
                                  Row(
                                    children: [
                                      IconButton(
                                        icon: const Icon(Icons.remove_circle_outline_rounded, color: Colors.grey, size: 20),
                                        onPressed: () {
                                          context.read<CartCubit>().updateQuantity(item.produto.id, item.quantidade - 1);
                                        },
                                      ),
                                      Text(
                                        '${item.quantidade}',
                                        style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14),
                                      ),
                                      IconButton(
                                        icon: const Icon(Icons.add_circle_outline_rounded, color: AppColors.primary, size: 20),
                                        onPressed: () {
                                          context.read<CartCubit>().updateQuantity(item.produto.id, item.quantidade + 1);
                                        },
                                      ),
                                    ],
                                  ),
                                ],
                              ),
                            );
                          },
                        ),
                      ),
                      const Divider(),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          const Text('Total:', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                          Text(
                            'R\$ ${state.totalPrice.toStringAsFixed(2).replaceAll('.', ',')}',
                            style: const TextStyle(color: AppColors.primary, fontWeight: FontWeight.w900, fontSize: 18),
                          ),
                        ],
                      ),
                      const SizedBox(height: 16),
                      AppButton(
                        text: 'Ir para o Checkout',
                        onPressed: () {
                          Navigator.pop(context);
                          context.push('/checkout');
                        },
                      ),
                    ],
                  ),
                );
              },
            );
          },
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    final authState = context.read<AuthBloc>().state;
    final loggedUser = authState is AuthAuthenticated ? authState.user : null;

    final isSearchingMode = _searchQuery.isNotEmpty || _selectedCategory != 'Todos';

    return BlocBuilder<CartCubit, CartState>(
      builder: (context, cartState) {
        return Scaffold(
          appBar: AppBar(
            title: const Text(
              'Marketplace',
              style: TextStyle(fontWeight: FontWeight.w800, letterSpacing: -0.5),
            ),
            elevation: 0,
          ),
          floatingActionButton: cartState.items.isEmpty
              ? null
              : FloatingActionButton.extended(
                  onPressed: () => _showCartBottomSheet(context, cartState),
                  backgroundColor: AppColors.primary,
                  icon: Badge(
                    label: Text('${cartState.totalItems}'),
                    child: const Icon(Icons.shopping_cart_rounded, color: Colors.white),
                  ),
                  label: Text(
                    'R\$ ${cartState.totalPrice.toStringAsFixed(2).replaceAll('.', ',')}',
                    style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.white),
                  ),
                ),
          body: SafeArea(
            child: RefreshIndicator(
              onRefresh: () => context.read<ProductsCubit>().loadProducts(),
              color: AppColors.primary,
              child: SingleChildScrollView(
                physics: const AlwaysScrollableScrollPhysics(),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    _buildLocationHeader(isDark),
                    _buildSearchBar(isDark),

                    if (!isSearchingMode) ...[
                      const SizedBox(height: 8),
                      _buildCircularCategories(isDark),
                      const SizedBox(height: 12),
                      _buildPromoBanners(isDark),
                      const SizedBox(height: 12),
                      _buildNearbyShops(isDark),
                      const SizedBox(height: 12),
                      _buildFeaturedBrands(isDark),
                      const SizedBox(height: 20),
                      Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 8.0),
                        child: Text(
                          'Produtos em Destaque',
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                            color: isDark ? AppColors.darkTextPrimary : AppColors.textPrimary,
                          ),
                        ),
                      ),
                    ] else ...[
                      _buildCircularCategories(isDark),
                      Padding(
                        padding: const EdgeInsets.fromLTRB(20.0, 16.0, 20.0, 4.0),
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Text(
                              _selectedCategory != 'Todos'
                                  ? 'Filtrado por: $_selectedCategory'
                                  : 'Resultados da busca',
                              style: TextStyle(
                                fontSize: 14,
                                fontWeight: FontWeight.bold,
                                color: isDark ? AppColors.darkTextPrimary : AppColors.textPrimary,
                              ),
                            ),
                            TextButton(
                              onPressed: () {
                                setState(() {
                                  _selectedCategory = 'Todos';
                                  _searchController.clear();
                                  _searchQuery = '';
                                });
                              },
                              child: const Text('Limpar Filtros', style: TextStyle(color: AppColors.primary, fontSize: 12)),
                            ),
                          ],
                        ),
                      ),
                    ],

                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 14.0),
                      child: BlocBuilder<ProductsCubit, ProductsState>(
                        builder: (context, state) {
                          if (state is ProductsLoading) {
                            return const Padding(
                              padding: EdgeInsets.symmetric(vertical: 40.0),
                              child: Center(child: CircularProgressIndicator(color: AppColors.primary)),
                            );
                          }

                          if (state is ProductsError) {
                            return Padding(
                              padding: const EdgeInsets.symmetric(vertical: 40.0),
                              child: Center(
                                child: Column(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    const Icon(Icons.error_outline_rounded, size: 56, color: AppColors.error),
                                    const SizedBox(height: 12),
                                    Text(state.message, style: const TextStyle(fontWeight: FontWeight.bold)),
                                    const SizedBox(height: 12),
                                    ElevatedButton(
                                      style: ElevatedButton.styleFrom(backgroundColor: AppColors.primary),
                                      onPressed: () => context.read<ProductsCubit>().loadProducts(),
                                      child: const Text('Tentar Novamente', style: TextStyle(color: Colors.white)),
                                    ),
                                  ],
                                ),
                              ),
                            );
                          }

                          if (state is ProductsLoaded) {
                            final filteredProducts = state.produtos.where((p) {
                              final matchesCategory = _selectedCategory == 'Todos' ||
                                  p.categoria.toLowerCase() == _selectedCategory.toLowerCase();
                              final matchesSearch = p.nome.toLowerCase().contains(_searchQuery.toLowerCase()) ||
                                  p.descricao.toLowerCase().contains(_searchQuery.toLowerCase());
                              return matchesCategory && matchesSearch;
                            }).toList();

                            if (filteredProducts.isEmpty) {
                              return Padding(
                                padding: const EdgeInsets.symmetric(vertical: 60.0),
                                child: Center(
                                  child: Column(
                                    mainAxisAlignment: MainAxisAlignment.center,
                                    children: [
                                      Icon(
                                        Icons.storefront_outlined,
                                        size: 64,
                                        color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                                      ),
                                      const SizedBox(height: 12),
                                      Text(
                                        'Nenhum produto encontrado.',
                                        style: theme.textTheme.titleMedium?.copyWith(
                                          color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                              );
                            }

                            return GridView.builder(
                              shrinkWrap: true,
                              physics: const NeverScrollableScrollPhysics(),
                              padding: const EdgeInsets.symmetric(vertical: 10.0),
                              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                                crossAxisCount: 2,
                                mainAxisSpacing: 14,
                                crossAxisSpacing: 14,
                                childAspectRatio: 0.64,
                              ),
                              itemCount: filteredProducts.length,
                              itemBuilder: (context, index) {
                                final produto = filteredProducts[index];
                                final cartItemIndex = cartState.items.indexWhere((item) => item.produto.id == produto.id);
                                final quantityInCart = cartItemIndex != -1 ? cartState.items[cartItemIndex].quantidade : 0;
                                return _buildProductCard(context, produto, loggedUser, isDark, quantityInCart);
                              },
                            );
                          }

                          return const Padding(
                            padding: EdgeInsets.symmetric(vertical: 40.0),
                            child: Center(child: Text('Carregando produtos...')),
                          );
                        },
                      ),
                    ),
                    const SizedBox(height: 40),
                  ],
                ),
              ),
            ),
          ),
        );
      },
    );
  }

  Widget _buildLocationHeader(bool isDark) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(20.0, 12.0, 20.0, 4.0),
      child: Row(
        children: [
          const Icon(Icons.location_on_rounded, color: AppColors.primary, size: 22),
          const SizedBox(width: 8),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Entregar em',
                  style: TextStyle(
                    fontSize: 10,
                    fontWeight: FontWeight.w500,
                    color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                  ),
                ),
                Row(
                  children: [
                    Text(
                      'Rua das Flores, 123 - Centro',
                      style: TextStyle(
                        fontSize: 13,
                        fontWeight: FontWeight.bold,
                        color: isDark ? AppColors.darkTextPrimary : AppColors.textPrimary,
                      ),
                    ),
                    const Icon(Icons.keyboard_arrow_down_rounded, color: AppColors.primary, size: 18),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSearchBar(bool isDark) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 10.0),
      child: Row(
        children: [
          Expanded(
            child: Container(
              decoration: BoxDecoration(
                color: isDark ? AppColors.darkSurface : Colors.white,
                borderRadius: BorderRadius.circular(28),
                border: Border.all(
                  color: isDark ? AppColors.darkBorder : AppColors.border,
                  width: 1.0,
                ),
                boxShadow: [
                  BoxShadow(
                    color: isDark ? Colors.black26 : Colors.black12,
                    blurRadius: 4,
                    offset: const Offset(0, 2),
                  ),
                ],
              ),
              child: TextField(
                controller: _searchController,
                onChanged: _onSearchChanged,
                decoration: InputDecoration(
                  hintText: 'Buscar ração, brinquedo, petshop...',
                  hintStyle: TextStyle(
                    color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                    fontSize: 13,
                  ),
                  prefixIcon: const Icon(Icons.search_rounded, color: AppColors.primary, size: 20),
                  suffixIcon: _searchQuery.isNotEmpty
                      ? IconButton(
                          icon: const Icon(Icons.clear_rounded, color: AppColors.primary, size: 20),
                          onPressed: () {
                            _searchController.clear();
                            _onSearchChanged('');
                          },
                        )
                      : null,
                  border: InputBorder.none,
                  contentPadding: const EdgeInsets.symmetric(vertical: 12.0),
                ),
              ),
            ),
          ),
          const SizedBox(width: 12),
          Container(
            decoration: BoxDecoration(
              color: isDark ? AppColors.darkSurface : Colors.white,
              shape: BoxShape.circle,
              border: Border.all(
                color: isDark ? AppColors.darkBorder : AppColors.border,
                width: 1.0,
              ),
            ),
            child: IconButton(
              icon: const Icon(Icons.tune_rounded, color: AppColors.primary, size: 20),
              onPressed: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content: Text('Ordenação e filtros do marketplace em breve!'),
                    duration: Duration(seconds: 1),
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildCircularCategories(bool isDark) {
    return SizedBox(
      height: 95,
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.symmetric(horizontal: 16.0),
        itemCount: _categories.length,
        itemBuilder: (context, index) {
          final category = _categories[index];
          final isSelected = _selectedCategory == category;
          final icon = _categoryIcons[category] ?? Icons.shopping_bag_rounded;

          return Padding(
            padding: const EdgeInsets.symmetric(horizontal: 8.0),
            child: GestureDetector(
              onTap: () {
                setState(() {
                  _selectedCategory = category;
                });
              },
              child: Column(
                children: [
                  AnimatedContainer(
                    duration: const Duration(milliseconds: 200),
                    width: 56,
                    height: 56,
                    decoration: BoxDecoration(
                       shape: BoxShape.circle,
                       color: isSelected
                           ? AppColors.primary
                           : (isDark ? AppColors.darkSurface : Colors.white),
                       border: Border.all(
                         color: isSelected
                             ? Colors.transparent
                             : (isDark ? AppColors.darkBorder : AppColors.border),
                         width: 1.2,
                       ),
                       boxShadow: [
                         BoxShadow(
                           color: isSelected
                               ? AppColors.primary.withValues(alpha: 0.3)
                               : Colors.transparent,
                           blurRadius: 6,
                           offset: const Offset(0, 3),
                         ),
                       ],
                    ),
                    child: Icon(
                      icon,
                      color: isSelected ? Colors.white : AppColors.primary,
                      size: 24,
                    ),
                  ),
                  const SizedBox(height: 6),
                  Text(
                    category,
                    style: TextStyle(
                      fontSize: 10.5,
                      fontWeight: isSelected ? FontWeight.bold : FontWeight.w500,
                      color: isSelected
                          ? AppColors.primary
                          : (isDark ? AppColors.darkTextPrimary : AppColors.textPrimary),
                    ),
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }

  Widget _buildPromoBanners(bool isDark) {
    final List<Map<String, dynamic>> banners = [
      {
        'title': 'Frete Grátis',
        'subtitle': 'Nas compras acima de R\$150',
        'tag': 'PROMOÇÃO',
        'colors': [const Color(0xFFFF9E00), const Color(0xFFFF6D00)],
        'actionText': 'Aproveitar',
      },
      {
        'title': 'Mês do Cachorro Louco',
        'subtitle': 'Até 30% OFF em rações',
        'tag': 'OFERTA DO MÊS',
        'colors': [const Color(0xFF5D831C), const Color(0xFF7CB342)],
        'actionText': 'Ver Rações',
        'category': 'Rações',
      },
      {
        'title': 'Primeira Compra',
        'subtitle': '15% OFF usando MYBUDDY15',
        'tag': 'CUPOM',
        'colors': [const Color(0xFF1E88E5), const Color(0xFF1565C0)],
        'actionText': 'Comprar',
      },
    ];

    return SizedBox(
      height: 120,
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.symmetric(horizontal: 16.0),
        itemCount: banners.length,
        itemBuilder: (context, index) {
          final b = banners[index];
          return Container(
            width: MediaQuery.of(context).size.width * 0.78,
            margin: const EdgeInsets.symmetric(horizontal: 6.0, vertical: 4.0),
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(16),
              gradient: LinearGradient(
                colors: b['colors'] as List<Color>,
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              boxShadow: [
                BoxShadow(
                  color: (b['colors'] as List<Color>)[1].withValues(alpha: 0.3),
                   blurRadius: 6,
                   offset: const Offset(0, 3),
                ),
              ],
            ),
            child: Stack(
              children: [
                Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                        decoration: BoxDecoration(
                          color: Colors.white24,
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Text(
                          b['tag'] as String,
                          style: const TextStyle(
                            color: Colors.white,
                            fontSize: 9,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                      const SizedBox(height: 6),
                      Text(
                        b['title'] as String,
                        style: const TextStyle(
                          color: Colors.white,
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      Text(
                        b['subtitle'] as String,
                        style: const TextStyle(
                          color: Colors.white70,
                          fontSize: 11,
                        ),
                      ),
                    ],
                  ),
                ),
                Positioned(
                  right: 12,
                  bottom: 12,
                  child: ElevatedButton(
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.white,
                      foregroundColor: (b['colors'] as List<Color>)[1],
                      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                      minimumSize: Size.zero,
                      tapTargetSize: MaterialTapTargetSize.shrinkWrap,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                    ),
                    onPressed: () {
                      if (b['category'] != null) {
                        setState(() {
                          _selectedCategory = b['category'] as String;
                        });
                      }
                    },
                    child: Text(
                      b['actionText'] as String,
                      style: const TextStyle(fontSize: 10, fontWeight: FontWeight.bold),
                    ),
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _buildNearbyShops(bool isDark) {
    final List<Map<String, dynamic>> shops = [
      {'name': 'Petz', 'rating': 4.8, 'time': '30-45 min', 'fee': 'R\$ 5,90', 'icon': Icons.pets},
      {'name': 'Cobasi', 'rating': 4.9, 'time': '20-30 min', 'fee': 'Grátis', 'icon': Icons.storefront},
      {'name': 'Petlove', 'rating': 4.7, 'time': '15-25 min', 'fee': 'R\$ 7,50', 'icon': Icons.shopping_basket},
      {'name': 'Bicho Chic', 'rating': 4.6, 'time': '40-55 min', 'fee': 'R\$ 4,00', 'icon': Icons.home},
    ];

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 12.0),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'Lojas Próximas',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                  color: isDark ? AppColors.darkTextPrimary : AppColors.textPrimary,
                ),
              ),
              Text(
                'Ver todas',
                style: TextStyle(
                  fontSize: 12,
                  color: AppColors.primary,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
        ),
        SizedBox(
          height: 75,
          child: ListView.builder(
            scrollDirection: Axis.horizontal,
            padding: const EdgeInsets.symmetric(horizontal: 16.0),
            itemCount: shops.length,
            itemBuilder: (context, index) {
              final s = shops[index];
              return Container(
                width: 170,
                margin: const EdgeInsets.symmetric(horizontal: 6.0),
                padding: const EdgeInsets.all(10.0),
                decoration: BoxDecoration(
                  color: isDark ? AppColors.darkSurface : Colors.white,
                  borderRadius: BorderRadius.circular(14),
                  border: Border.all(
                    color: isDark ? AppColors.darkBorder : AppColors.border,
                    width: 1.0,
                  ),
                ),
                child: Row(
                  children: [
                    CircleAvatar(
                      radius: 18,
                      backgroundColor: AppColors.primary.withValues(alpha: 0.1),
                      child: Icon(s['icon'] as IconData, color: AppColors.primary, size: 18),
                    ),
                    const SizedBox(width: 10),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Text(
                            s['name'] as String,
                            style: TextStyle(
                              fontSize: 12,
                              fontWeight: FontWeight.bold,
                              color: isDark ? AppColors.darkTextPrimary : AppColors.textPrimary,
                            ),
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                          ),
                          const SizedBox(height: 2),
                          Row(
                            children: [
                              const Icon(Icons.star_rounded, color: Colors.amber, size: 12),
                              const SizedBox(width: 2),
                              Text(
                                '${s['rating']}',
                                style: const TextStyle(fontSize: 10, fontWeight: FontWeight.bold),
                              ),
                              const SizedBox(width: 4),
                              Text(
                                '• ${s['time']}',
                                style: TextStyle(
                                  fontSize: 9,
                                  color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                                ),
                                maxLines: 1,
                                overflow: TextOverflow.ellipsis,
                              ),
                            ],
                          ),
                          const SizedBox(height: 2),
                          Text(
                            'Entrega: ${s['fee']}',
                            style: TextStyle(
                              fontSize: 9,
                              fontWeight: FontWeight.w500,
                              color: s['fee'] == 'Grátis' ? AppColors.success : (isDark ? AppColors.darkTextSecondary : AppColors.textLight),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              );
            },
          ),
        ),
      ],
    );
  }

  Widget _buildFeaturedBrands(bool isDark) {
    final brands = ['Royal Canin', 'Premier', 'Zee.Dog', 'Bravecto', 'Golden', 'Pedigree'];
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 12.0),
          child: Text(
            'Marcas em Destaque',
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.bold,
              color: isDark ? AppColors.darkTextPrimary : AppColors.textPrimary,
            ),
          ),
        ),
        SizedBox(
          height: 38,
          child: ListView.builder(
            scrollDirection: Axis.horizontal,
            padding: const EdgeInsets.symmetric(horizontal: 16.0),
            itemCount: brands.length,
            itemBuilder: (context, index) {
              return Container(
                margin: const EdgeInsets.symmetric(horizontal: 6.0),
                padding: const EdgeInsets.symmetric(horizontal: 14.0),
                alignment: Alignment.center,
                decoration: BoxDecoration(
                  color: isDark ? AppColors.darkSurface : Colors.white,
                  borderRadius: BorderRadius.circular(20),
                  border: Border.all(
                    color: isDark ? AppColors.darkBorder : AppColors.border,
                    width: 1.0,
                  ),
                ),
                child: Text(
                  brands[index],
                  style: TextStyle(
                    fontSize: 11,
                    fontWeight: FontWeight.bold,
                    color: isDark ? AppColors.darkTextSecondary : AppColors.textSecondary,
                  ),
                ),
              );
            },
          ),
        ),
      ],
    );
  }

  Widget _buildProductCard(
    BuildContext context,
    Produto produto,
    dynamic loggedUser,
    bool isDark,
    int quantityInCart,
  ) {
    final theme = Theme.of(context);
    final hasDiscount = produto.precoAntigo != null && produto.precoAntigo! > produto.preco;
    final percentOff = hasDiscount ? (((produto.precoAntigo! - produto.preco) / produto.precoAntigo!) * 100).round() : 0;
    
    final rating = produto.avaliacaoMedia ?? 4.7;
    final storeName = produto.nomeLoja ?? 'Petshop Parceiro';

    return AppCard(
      padding: EdgeInsets.zero,
      borderRadius: 16,
      onTap: () => context.push('/produto-detalhe/${produto.id}'),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
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
                      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 3),
                      decoration: BoxDecoration(
                        color: AppColors.secondary.withValues(alpha: 0.95),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Text(
                        produto.categoria,
                        style: const TextStyle(
                          color: Colors.white,
                          fontSize: 9,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ),

                  if (hasDiscount || produto.preco >= 150)
                    Positioned(
                      top: 8,
                      right: 8,
                      child: Container(
                        padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 3),
                        decoration: BoxDecoration(
                          color: hasDiscount ? AppColors.error : AppColors.success,
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Text(
                          hasDiscount ? '$percentOff% OFF' : 'Frete Grátis',
                          style: const TextStyle(
                            color: Colors.white,
                            fontSize: 9,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                    ),

                  Positioned(
                    bottom: 6,
                    right: 6,
                    child: Container(
                      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                      decoration: BoxDecoration(
                        color: Colors.black87,
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          const Icon(Icons.star_rounded, color: Colors.amber, size: 10),
                          const SizedBox(width: 2),
                          Text(
                            rating.toStringAsFixed(1),
                            style: const TextStyle(
                              color: Colors.white,
                              fontSize: 9,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),

          Padding(
            padding: const EdgeInsets.fromLTRB(10.0, 8.0, 10.0, 10.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  storeName,
                  style: TextStyle(
                    color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                    fontSize: 9.5,
                    fontWeight: FontWeight.w600,
                  ),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
                const SizedBox(height: 3),
                
                Text(
                  produto.nome,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                  style: theme.textTheme.bodyMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                    height: 1.15,
                    fontSize: 12,
                  ),
                ),
                const SizedBox(height: 3),

                Text(
                  produto.descricao,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: theme.textTheme.bodySmall?.copyWith(
                    color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                    fontSize: 10,
                  ),
                ),
                const SizedBox(height: 6),

                Row(
                  children: [
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          if (hasDiscount)
                            Text(
                              'R\$ ${produto.precoAntigo!.toStringAsFixed(2).replaceAll('.', ',')}',
                              style: TextStyle(
                                color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                                fontSize: 10,
                                decoration: TextDecoration.lineThrough,
                              ),
                            ),
                          Text(
                            'R\$ ${produto.preco.toStringAsFixed(2).replaceAll('.', ',')}',
                            style: TextStyle(
                              color: AppColors.primary,
                              fontWeight: FontWeight.w900,
                              fontSize: hasDiscount ? 13.5 : 14.5,
                            ),
                          ),
                        ],
                      ),
                    ),
                    
                    if (quantityInCart > 0)
                      Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          GestureDetector(
                            onTap: () {
                              context.read<CartCubit>().updateQuantity(produto.id, quantityInCart - 1);
                            },
                            child: Container(
                              padding: const EdgeInsets.all(4),
                              decoration: BoxDecoration(
                                color: AppColors.primary.withValues(alpha: 0.1),
                                shape: BoxShape.circle,
                              ),
                              child: const Icon(
                                Icons.remove_rounded,
                                color: AppColors.primary,
                                size: 16,
                              ),
                            ),
                          ),
                          const SizedBox(width: 8),
                          Text(
                            '$quantityInCart',
                            style: const TextStyle(
                              fontWeight: FontWeight.bold,
                              fontSize: 13,
                              color: AppColors.primary,
                            ),
                          ),
                          const SizedBox(width: 8),
                          GestureDetector(
                            onTap: () {
                              context.read<CartCubit>().updateQuantity(produto.id, quantityInCart + 1);
                            },
                            child: Container(
                              padding: const EdgeInsets.all(4),
                              decoration: BoxDecoration(
                                color: AppColors.primary,
                                shape: BoxShape.circle,
                              ),
                              child: const Icon(
                                Icons.add_rounded,
                                color: Colors.white,
                                size: 16,
                              ),
                            ),
                          ),
                        ],
                      )
                    else
                      GestureDetector(
                        onTap: () {
                          context.read<CartCubit>().addToCart(produto, quantidade: 1);
                        },
                        child: Container(
                          padding: const EdgeInsets.all(5),
                          decoration: BoxDecoration(
                            color: AppColors.primary.withValues(alpha: 0.1),
                            shape: BoxShape.circle,
                          ),
                          child: const Icon(
                            Icons.add_rounded,
                            color: AppColors.primary,
                            size: 20,
                          ),
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
