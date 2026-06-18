import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mybuddy_app/features/marketplace/domain/entities/produto.dart';
import 'package:mybuddy_app/features/marketplace/presentation/bloc/cart_cubit.dart';
import 'package:mybuddy_app/features/marketplace/presentation/bloc/products_cubit.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';
import 'package:mybuddy_app/shared/widgets/app_button.dart';

class ProdutoDetalhePage extends StatefulWidget {
  final String productId;
  const ProdutoDetalhePage({super.key, required this.productId});

  @override
  State<ProdutoDetalhePage> createState() => _ProdutoDetalhePageState();
}

class _ProdutoDetalhePageState extends State<ProdutoDetalhePage> {
  int _quantidade = 1;
  bool _isFavorited = false;

  void _incrementQuantity() {
    setState(() {
      _quantidade++;
    });
  }

  void _decrementQuantity() {
    if (_quantidade > 1) {
      setState(() {
        _quantidade--;
      });
    }
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
                        text: 'Finalizar Compra',
                        onPressed: () {
                          final goRouter = GoRouter.of(context);
                          Navigator.pop(context);
                          goRouter.push('/checkout');
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

    return BlocBuilder<CartCubit, CartState>(
      builder: (context, cartState) {
        return Scaffold(
          floatingActionButtonLocation: const _CustomFloatingActionButtonLocation(),
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
          body: BlocBuilder<ProductsCubit, ProductsState>(
            builder: (context, state) {
              if (state is ProductsLoading) {
                return const Center(child: CircularProgressIndicator(color: AppColors.primary));
              }

          if (state is ProductsLoaded) {
            final produto = state.produtos.firstWhere(
              (p) => p.id == widget.productId,
              orElse: () => const Produto(
                id: '',
                nome: 'Produto não encontrado',
                preco: 0.0,
                descricao: '',
                imagemUrl: '',
                categoria: '',
              ),
            );

            if (produto.id.isEmpty) {
              return Scaffold(
                appBar: AppBar(title: const Text('Erro')),
                body: const Center(child: Text('Produto não encontrado.')),
              );
            }

            final hasDiscount = produto.precoAntigo != null && produto.precoAntigo! > produto.preco;
            final percentOff = hasDiscount ? (((produto.precoAntigo! - produto.preco) / produto.precoAntigo!) * 100).round() : 0;
            final rating = produto.avaliacaoMedia ?? 4.7;
            final storeName = produto.nomeLoja ?? 'Petshop Parceiro';

            return Center(
              child: ConstrainedBox(
                constraints: const BoxConstraints(maxWidth: 650),
                child: Stack(
                  children: [
                // Conteúdo Principal Rolável
                Positioned.fill(
                  child: SingleChildScrollView(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.stretch,
                      children: [
                        // Imagem com Gradiente de sombra no topo
                        Stack(
                          children: [
                            Image.network(
                              produto.imagemUrl,
                              height: MediaQuery.of(context).size.height * 0.4 > 350 ? 350.0 : MediaQuery.of(context).size.height * 0.4,
                              width: double.infinity,
                              fit: BoxFit.cover,
                              errorBuilder: (context, error, stackTrace) => Container(
                                height: MediaQuery.of(context).size.height * 0.4 > 350 ? 350.0 : MediaQuery.of(context).size.height * 0.4,
                                color: isDark ? AppColors.darkBorder : AppColors.background,
                                child: const Icon(Icons.shopping_bag_outlined, size: 80, color: AppColors.primary),
                              ),
                            ),
                            Positioned.fill(
                              child: Container(
                                decoration: const BoxDecoration(
                                  gradient: LinearGradient(
                                    colors: [Colors.black54, Colors.transparent, Colors.transparent],
                                    begin: Alignment.topCenter,
                                    end: Alignment.bottomCenter,
                                  ),
                                ),
                              ),
                            ),
                          ],
                        ),

                        // Painel de Informações do Produto
                        Padding(
                          padding: const EdgeInsets.all(24.0),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              // Categoria & Avaliação
                              Row(
                                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                children: [
                                  Container(
                                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                                    decoration: BoxDecoration(
                                      color: AppColors.secondary.withValues(alpha: 0.15),
                                      borderRadius: BorderRadius.circular(12),
                                    ),
                                    child: Text(
                                      produto.categoria,
                                      style: const TextStyle(
                                        color: AppColors.secondary,
                                        fontWeight: FontWeight.bold,
                                        fontSize: 11,
                                      ),
                                    ),
                                  ),
                                  Container(
                                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                                    decoration: BoxDecoration(
                                      color: Colors.amber.withValues(alpha: 0.15),
                                      borderRadius: BorderRadius.circular(12),
                                    ),
                                    child: Row(
                                      children: [
                                        const Icon(Icons.star_rounded, color: Colors.amber, size: 16),
                                        const SizedBox(width: 4),
                                        Text(
                                          rating.toStringAsFixed(1),
                                          style: const TextStyle(
                                            color: Colors.orange,
                                            fontWeight: FontWeight.bold,
                                            fontSize: 11,
                                          ),
                                        ),
                                        const SizedBox(width: 2),
                                        const Text(
                                          '(48 avaliações)',
                                          style: TextStyle(color: Colors.grey, fontSize: 9),
                                        ),
                                      ],
                                    ),
                                  ),
                                ],
                              ),
                              const SizedBox(height: 16),

                              // Nome do Produto
                              Text(
                                produto.nome,
                                style: theme.textTheme.headlineSmall?.copyWith(
                                  fontWeight: FontWeight.w800,
                                  letterSpacing: -0.5,
                                  height: 1.2,
                                ),
                              ),
                              const SizedBox(height: 8),

                              // Vendido por
                              Row(
                                children: [
                                  Icon(Icons.storefront_rounded, size: 16, color: isDark ? AppColors.darkTextSecondary : AppColors.textLight),
                                  const SizedBox(width: 6),
                                  Text(
                                    'Vendido e entregue por: ',
                                    style: TextStyle(
                                      color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                                      fontSize: 12,
                                    ),
                                  ),
                                  Text(
                                    storeName,
                                    style: const TextStyle(
                                      fontWeight: FontWeight.bold,
                                      color: AppColors.primary,
                                      fontSize: 12,
                                    ),
                                  ),
                                ],
                              ),
                              const SizedBox(height: 20),

                              // Preços
                              Row(
                                crossAxisAlignment: CrossAxisAlignment.center,
                                children: [
                                  Column(
                                    crossAxisAlignment: CrossAxisAlignment.start,
                                    children: [
                                      if (hasDiscount) ...[
                                        Text(
                                          'R\$ ${produto.precoAntigo!.toStringAsFixed(2).replaceAll('.', ',')}',
                                          style: TextStyle(
                                            color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                                            fontSize: 13,
                                            decoration: TextDecoration.lineThrough,
                                          ),
                                        ),
                                        const SizedBox(height: 2),
                                      ],
                                      Text(
                                        'R\$ ${produto.preco.toStringAsFixed(2).replaceAll('.', ',')}',
                                        style: TextStyle(
                                          color: AppColors.primary,
                                          fontWeight: FontWeight.w900,
                                          fontSize: hasDiscount ? 22 : 24,
                                        ),
                                      ),
                                    ],
                                  ),
                                  const SizedBox(width: 16),
                                  if (hasDiscount)
                                    Container(
                                      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                                      decoration: BoxDecoration(
                                        color: AppColors.error,
                                        borderRadius: BorderRadius.circular(10),
                                      ),
                                      child: Text(
                                        '$percentOff% OFF',
                                        style: const TextStyle(
                                          color: Colors.white,
                                          fontWeight: FontWeight.bold,
                                          fontSize: 11,
                                        ),
                                      ),
                                    )
                                  else if (produto.preco >= 150)
                                    Container(
                                      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                                      decoration: BoxDecoration(
                                        color: AppColors.success,
                                        borderRadius: BorderRadius.circular(10),
                                      ),
                                      child: const Text(
                                        'Frete Grátis',
                                        style: TextStyle(
                                          color: Colors.white,
                                          fontWeight: FontWeight.bold,
                                          fontSize: 11,
                                        ),
                                      ),
                                    ),
                                ],
                              ),

                              const Divider(height: 32),

                              // Descrição
                              Text(
                                'Descrição',
                                style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                              ),
                              const SizedBox(height: 8),
                              Text(
                                produto.descricao.isEmpty
                                    ? 'Este produto é excelente para a saúde, bem-estar e diversão do seu pet, produzido pelas melhores marcas parceiras do mercado.'
                                    : produto.descricao,
                                style: TextStyle(
                                  color: isDark ? AppColors.darkTextSecondary : AppColors.textSecondary,
                                  fontSize: 13,
                                  height: 1.5,
                                ),
                              ),

                              const Divider(height: 32),

                              // Ficha Técnica (iFood / Angular style)
                              Text(
                                'Especificações',
                                style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                              ),
                              const SizedBox(height: 12),
                              _buildSpecRow('Marca', produto.nome.contains('Premier') ? 'Premier' : (produto.nome.contains('Golden') ? 'Golden' : 'MyBuddy Brand'), isDark),
                              _buildSpecRow('Indicação', produto.categoria == 'Rações' ? 'Cães / Gatos' : 'Todos os pets', isDark),
                              _buildSpecRow('Origem', 'Nacional', isDark),
                              _buildSpecRow('Peso / Conteúdo', produto.nome.contains('15kg') ? '15 kg' : (produto.nome.contains('500ml') ? '500 ml' : '500g'), isDark),
                              const SizedBox(height: 100), // Espaço para barra inferior
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                ),

                // Header com Botões de Ação
                Positioned(
                  top: MediaQuery.of(context).padding.top + 10,
                  left: 16,
                  right: 16,
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      // Botão Voltar
                      Container(
                        decoration: const BoxDecoration(
                          color: Colors.black38,
                          shape: BoxShape.circle,
                        ),
                        child: IconButton(
                          icon: const Icon(Icons.arrow_back_rounded, color: Colors.white),
                          onPressed: () => Navigator.pop(context),
                        ),
                      ),

                      // Botão Favorito
                      Container(
                        decoration: const BoxDecoration(
                          color: Colors.black38,
                          shape: BoxShape.circle,
                        ),
                        child: IconButton(
                          icon: Icon(
                            _isFavorited ? Icons.favorite_rounded : Icons.favorite_border_rounded,
                            color: _isFavorited ? Colors.red : Colors.white,
                          ),
                          onPressed: () {
                            setState(() {
                              _isFavorited = !_isFavorited;
                            });
                            ScaffoldMessenger.of(context).showSnackBar(
                              SnackBar(
                                content: Text(
                                  _isFavorited
                                      ? 'Produto adicionado aos favoritos!'
                                      : 'Produto removido dos favoritos.',
                                ),
                                duration: const Duration(seconds: 1),
                              ),
                            );
                          },
                        ),
                      ),
                    ],
                  ),
                ),

                // Barra Inferior de Adicionar ao Carrinho (iFood-like)
                Positioned(
                  left: 0,
                  right: 0,
                  bottom: 0,
                  child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
                    decoration: BoxDecoration(
                      color: isDark ? AppColors.darkSurface : Colors.white,
                      border: Border(
                        top: BorderSide(
                          color: isDark ? AppColors.darkBorder : AppColors.border,
                          width: 1.0,
                        ),
                      ),
                      boxShadow: const [
                        BoxShadow(color: Colors.black12, blurRadius: 10, offset: Offset(0, -2)),
                      ],
                    ),
                    child: SafeArea(
                      top: false,
                      child: Row(
                        children: [
                          // Seletor de Quantidade
                          Container(
                            decoration: BoxDecoration(
                              color: isDark ? AppColors.darkBackground : Colors.grey[100],
                              borderRadius: BorderRadius.circular(16),
                            ),
                            child: Row(
                              children: [
                                IconButton(
                                  icon: const Icon(Icons.remove_rounded, color: AppColors.primary, size: 20),
                                  onPressed: _decrementQuantity,
                                ),
                                Text(
                                  '$_quantidade',
                                  style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                                ),
                                IconButton(
                                  icon: const Icon(Icons.add_rounded, color: AppColors.primary, size: 20),
                                  onPressed: _incrementQuantity,
                                ),
                              ],
                            ),
                          ),
                          const SizedBox(width: 16),

                          // Botão Principal
                          Expanded(
                            child: AppButton(
                              text: 'Adicionar ao Carrinho',
                              onPressed: () {
                                context.read<CartCubit>().addToCart(produto, quantidade: _quantidade);
                              },
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
                  ],
                ),
              ),
            );
          }

          return const Center(child: Text('Erro ao carregar produto.'));
        },
      ),
    );
  },
);
  }

  Widget _buildSpecRow(String label, String value, bool isDark) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            label,
            style: TextStyle(
              fontSize: 12,
              color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
            ),
          ),
          Text(
            value,
            style: TextStyle(
              fontSize: 12,
              fontWeight: FontWeight.bold,
              color: isDark ? AppColors.darkTextPrimary : AppColors.textPrimary,
            ),
          ),
        ],
      ),
    );
  }
}

class _CustomFloatingActionButtonLocation extends FloatingActionButtonLocation {
  const _CustomFloatingActionButtonLocation();

  @override
  Offset getOffset(ScaffoldPrelayoutGeometry geometry) {
    double x = geometry.scaffoldSize.width - geometry.floatingActionButtonSize.width - 16;
    if (geometry.scaffoldSize.width > 650) {
      final double centerOffset = (geometry.scaffoldSize.width - 650) / 2;
      x = geometry.scaffoldSize.width - centerOffset - geometry.floatingActionButtonSize.width - 16;
    }
    final double bottomBarHeight = 85.0 + geometry.minInsets.bottom;
    final double y = geometry.scaffoldSize.height - geometry.floatingActionButtonSize.height - bottomBarHeight - 16;
    return Offset(x, y);
  }
}
