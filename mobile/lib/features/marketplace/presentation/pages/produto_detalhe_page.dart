import 'package:flutter/material.dart';
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

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Scaffold(
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

            return Stack(
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
                              height: MediaQuery.of(context).size.height * 0.4,
                              width: double.infinity,
                              fit: BoxFit.cover,
                              errorBuilder: (context, error, stackTrace) => Container(
                                height: MediaQuery.of(context).size.height * 0.4,
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
                                ScaffoldMessenger.of(context).showSnackBar(
                                  SnackBar(
                                    content: Text('$_quantidade x ${produto.nome} adicionado ao carrinho!'),
                                    backgroundColor: AppColors.success,
                                    action: SnackBarAction(
                                      label: 'Ver Carrinho',
                                      textColor: Colors.white,
                                      onPressed: () {
                                        Navigator.pop(context); // Volta ao marketplace
                                      },
                                    ),
                                  ),
                                );
                              },
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ],
            );
          }

          return const Center(child: Text('Erro ao carregar produto.'));
        },
      ),
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
