import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';
import 'package:mybuddy_app/features/marketplace/domain/entities/produto.dart';

class CartItem extends Equatable {
  final Produto produto;
  final int quantidade;

  const CartItem({
    required this.produto,
    required this.quantidade,
  });

  CartItem copyWith({Produto? produto, int? quantidade}) {
    return CartItem(
      produto: produto ?? this.produto,
      quantidade: quantidade ?? this.quantidade,
    );
  }

  @override
  List<Object?> get props => [produto, quantidade];
}

class CartState extends Equatable {
  final List<CartItem> items;

  const CartState({this.items = const []});

  double get totalPrice => items.fold(0.0, (sum, item) => sum + (item.produto.preco * item.quantidade));

  int get totalItems => items.fold(0, (sum, item) => sum + item.quantidade);

  @override
  List<Object?> get props => [items];
}

class CartCubit extends Cubit<CartState> {
  CartCubit() : super(const CartState());

  void addToCart(Produto produto, {int quantidade = 1}) {
    final currentItems = List<CartItem>.from(state.items);
    final index = currentItems.indexWhere((item) => item.produto.id == produto.id);

    if (index >= 0) {
      final updatedQty = currentItems[index].quantidade + quantidade;
      currentItems[index] = currentItems[index].copyWith(quantidade: updatedQty);
    } else {
      currentItems.add(CartItem(produto: produto, quantidade: quantidade));
    }

    emit(CartState(items: currentItems));
  }

  void updateQuantity(String produtoId, int quantidade) {
    if (quantidade <= 0) {
      removeFromCart(produtoId);
      return;
    }

    final currentItems = List<CartItem>.from(state.items);
    final index = currentItems.indexWhere((item) => item.produto.id == produtoId);

    if (index >= 0) {
      currentItems[index] = currentItems[index].copyWith(quantidade: quantidade);
      emit(CartState(items: currentItems));
    }
  }

  void removeFromCart(String produtoId) {
    final currentItems = List<CartItem>.from(state.items);
    currentItems.removeWhere((item) => item.produto.id == produtoId);
    emit(CartState(items: currentItems));
  }

  void clearCart() {
    emit(const CartState(items: []));
  }
}
