import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_state.dart';
import 'package:mybuddy_app/features/marketplace/presentation/bloc/cart_cubit.dart';
import 'package:mybuddy_app/features/marketplace/presentation/bloc/products_cubit.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';
import 'package:mybuddy_app/shared/widgets/app_button.dart';
import 'package:mybuddy_app/shared/widgets/app_card.dart';

class CheckoutPage extends StatefulWidget {
  const CheckoutPage({super.key});

  @override
  State<CheckoutPage> createState() => _CheckoutPageState();
}

class _CheckoutPageState extends State<CheckoutPage> {
  String _paymentMethod = 'Pix';
  final TextEditingController _cupomController = TextEditingController();
  double _cupomDiscount = 0.0;
  bool _isCouponApplied = false;

  void _applyCoupon() {
    final coupon = _cupomController.text.trim().toUpperCase();
    if (coupon == 'MYBUDDY15') {
      setState(() {
        _cupomDiscount = 0.15; // 15% discount
        _isCouponApplied = true;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Cupom MYBUDDY15 aplicado com sucesso (15% OFF)!'), backgroundColor: AppColors.success),
      );
    } else if (coupon.isNotEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Cupom inválido.'), backgroundColor: AppColors.error),
      );
    }
  }

  @override
  void dispose() {
    _cupomController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    final authState = context.read<AuthBloc>().state;
    final loggedUser = authState is AuthAuthenticated ? authState.user : null;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Finalizar Compra', style: TextStyle(fontWeight: FontWeight.bold)),
      ),
      body: BlocBuilder<CartCubit, CartState>(
        builder: (context, cartState) {
          if (cartState.items.isEmpty) {
            return Center(
              child: Text(
                'Nenhum item no carrinho.',
                style: TextStyle(
                  color: isDark ? Colors.white70 : Colors.black87,
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
            );
          }

          final subtotal = cartState.totalPrice;
          final deliveryFee = subtotal >= 150 ? 0.0 : 5.90;
          final discountValue = subtotal * _cupomDiscount;
          final total = (subtotal + deliveryFee) - discountValue;

          return Center(
            child: ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 650),
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(20.0),
                child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                // Endereço de Entrega (iFood-like)
                Text(
                  'Endereço de Entrega',
                  style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 10),
                AppCard(
                  child: Row(
                    children: [
                      const Icon(Icons.location_on_rounded, color: AppColors.primary, size: 24),
                      const SizedBox(width: 12),
                      const Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              'Casa',
                              style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13),
                            ),
                            SizedBox(height: 2),
                            Text(
                              'Rua das Flores, 123 - Centro - São Paulo/SP',
                              style: TextStyle(color: Colors.grey, fontSize: 11),
                            ),
                          ],
                        ),
                      ),
                      TextButton(
                        onPressed: () {},
                        child: const Text('Alterar', style: TextStyle(color: AppColors.primary, fontSize: 12)),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 24),

                // Lista de Itens
                Text(
                  'Produtos Selecionados',
                  style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 10),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: cartState.items.map((item) {
                    return Padding(
                      padding: const EdgeInsets.symmetric(vertical: 6.0),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Expanded(
                            child: Row(
                              children: [
                                Text(
                                  '${item.quantidade}x ',
                                  style: const TextStyle(fontWeight: FontWeight.bold, color: AppColors.primary),
                                ),
                                Expanded(
                                  child: Text(
                                    item.produto.nome,
                                    maxLines: 1,
                                    overflow: TextOverflow.ellipsis,
                                    style: const TextStyle(fontSize: 13),
                                  ),
                                ),
                              ],
                            ),
                          ),
                          Text(
                            'R\$ ${(item.produto.preco * item.quantidade).toStringAsFixed(2).replaceAll('.', ',')}',
                            style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13),
                          ),
                        ],
                      ),
                    );
                  }).toList(),
                ),
                const SizedBox(height: 24),

                // Cupom de Desconto
                Text(
                  'Cupom de Desconto',
                  style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 10),
                Row(
                  children: [
                    Expanded(
                      child: TextField(
                        controller: _cupomController,
                        enabled: !_isCouponApplied,
                        decoration: InputDecoration(
                          hintText: 'Digite o cupom (ex: MYBUDDY15)',
                          hintStyle: const TextStyle(fontSize: 12),
                          border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                          contentPadding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
                        ),
                      ),
                    ),
                    const SizedBox(width: 12),
                    ElevatedButton(
                      style: ElevatedButton.styleFrom(
                        backgroundColor: _isCouponApplied ? AppColors.success : AppColors.primary,
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                        minimumSize: const Size(0, 48),
                      ),
                      onPressed: _isCouponApplied ? null : _applyCoupon,
                      child: Text(_isCouponApplied ? 'Aplicado' : 'Aplicar', style: const TextStyle(color: Colors.white)),
                    ),
                  ],
                ),
                const SizedBox(height: 24),

                // Formas de Pagamento
                Text(
                  'Formas de Pagamento',
                  style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 10),
                _buildPaymentOption('Pix', Icons.qr_code_rounded, 'Liberação imediata', isDark),
                _buildPaymentOption('Cartão de Crédito', Icons.credit_card_rounded, 'Até 6x sem juros', isDark),
                _buildPaymentOption('Boleto Bancário', Icons.description_outlined, 'Vencimento em 3 dias', isDark),
                const SizedBox(height: 24),

                // Resumo de Valores
                AppCard(
                  child: Column(
                    children: [
                      _buildSummaryRow('Subtotal', subtotal, isDark),
                      _buildSummaryRow(
                        'Taxa de Entrega',
                        deliveryFee,
                        isDark,
                        isFree: deliveryFee == 0.0,
                      ),
                      if (_cupomDiscount > 0)
                        _buildSummaryRow('Desconto Cupom', -discountValue, isDark, isDiscount: true),
                      const Divider(height: 24),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          const Text(
                            'Total',
                            style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                          ),
                          Text(
                            'R\$ ${total.toStringAsFixed(2).replaceAll('.', ',')}',
                            style: const TextStyle(
                              fontWeight: FontWeight.w900,
                              fontSize: 18,
                              color: AppColors.primary,
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 32),

                // Botão Confirmar
                AppButton(
                  text: 'Confirmar e Finalizar Pedido',
                  onPressed: () => _processOrder(context, cartState.items, total, loggedUser, isDark),
                ),
                const SizedBox(height: 20),
              ],
            ),
          ),
        ),
      );
        },
      ),
    );
  }

  Widget _buildPaymentOption(String method, IconData icon, String subtitle, bool isDark) {
    final isSelected = _paymentMethod == method;
    return GestureDetector(
      onTap: () {
        setState(() {
          _paymentMethod = method;
        });
      },
      child: Container(
        margin: const EdgeInsets.only(bottom: 10),
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
        decoration: BoxDecoration(
          color: isDark ? AppColors.darkSurface : Colors.white,
          borderRadius: BorderRadius.circular(14),
          border: Border.all(
            color: isSelected
                ? AppColors.primary
                : (isDark ? AppColors.darkBorder : AppColors.border),
            width: isSelected ? 1.8 : 1.0,
          ),
        ),
        child: Row(
          children: [
            Icon(icon, color: isSelected ? AppColors.primary : Colors.grey, size: 22),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    method,
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 13,
                      color: isSelected ? AppColors.primary : (isDark ? Colors.white : Colors.black87),
                    ),
                  ),
                  const SizedBox(height: 2),
                  Text(
                    subtitle,
                    style: const TextStyle(color: Colors.grey, fontSize: 10),
                  ),
                ],
              ),
            ),
            if (isSelected)
              const Icon(Icons.check_circle_rounded, color: AppColors.primary, size: 18),
          ],
        ),
      ),
    );
  }

  Widget _buildSummaryRow(String label, double val, bool isDark, {bool isFree = false, bool isDiscount = false}) {
    String valueText = 'R\$ ${val.toStringAsFixed(2).replaceAll('.', ',')}';
    if (isFree) valueText = 'Grátis';

    Color valueColor = isDark ? Colors.white : Colors.black87;
    if (isFree) valueColor = AppColors.success;
    if (isDiscount) valueColor = AppColors.error;

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: const TextStyle(color: Colors.grey, fontSize: 12.5)),
          Text(
            valueText,
            style: TextStyle(
              fontWeight: FontWeight.bold,
              fontSize: 12.5,
              color: valueColor,
            ),
          ),
        ],
      ),
    );
  }

  void _processOrder(
    BuildContext context,
    List<CartItem> items,
    double total,
    dynamic loggedUser,
    bool isDark,
  ) {
    final productsCubit = context.read<ProductsCubit>();
    final cartCubit = context.read<CartCubit>();

    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (dialogCtx) {
        return const Center(child: CircularProgressIndicator(color: AppColors.primary));
      },
    );

    Future.delayed(const Duration(milliseconds: 1500), () async {
      final clienteNome = loggedUser?.nome ?? 'Adotante MyBuddy';

      bool anyFailed = false;
      for (final item in items) {
        final success = await productsCubit.comprarProduto(
          clienteNome,
          item.produto.nome,
          item.produto.preco * item.quantidade,
          produtoId: item.produto.id,
          quantidade: item.quantidade,
        );
        if (!success) anyFailed = true;
      }

      if (context.mounted) {
        Navigator.of(context, rootNavigator: true).pop(); // Fecha o loading dialog
      }

      if (context.mounted) {
        showDialog(
          context: context,
          barrierDismissible: false,
          builder: (dialogContext) {
            return AlertDialog(
              backgroundColor: isDark ? AppColors.darkSurface : Colors.white,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
              title: const Row(
                children: [
                  Icon(Icons.check_circle_outline_rounded, color: AppColors.success),
                  SizedBox(width: 10),
                  Text('Pedido Concluído!'),
                ],
              ),
              content: Text(
                anyFailed
                    ? 'Seu pedido foi registrado parcialmente, mas foi finalizado com sucesso! Você pode acompanhar o envio em "Meu Perfil > Minhas Compras".'
                    : 'Parabéns! Seu pedido de R\$ ${total.toStringAsFixed(2).replaceAll('.', ',')} foi recebido pelos Petshops parceiros e está em preparação para entrega.',
              ),
              actions: [
                ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppColors.success,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                    minimumSize: const Size(0, 48),
                  ),
                  onPressed: () {
                    Navigator.pop(dialogContext); // Fecha dialog de sucesso usando o context do próprio dialog
                    if (context.mounted) {
                      cartCubit.clearCart(); // Limpa o carrinho
                      context.go('/marketplace'); // Volta ao marketplace usando o context do CheckoutPage
                    }
                  },
                  child: const Text('Ok', style: TextStyle(color: Colors.white)),
                ),
              ],
            );
          },
        );
      }
    });
  }
}
