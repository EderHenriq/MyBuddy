import 'package:flutter_test/flutter_test.dart';
import 'package:mybuddy_app/features/marketplace/domain/entities/produto.dart';
import 'package:mybuddy_app/features/marketplace/presentation/bloc/cart_cubit.dart';

void main() {
  late CartCubit cartCubit;

  const testProduct = Produto(
    id: '1',
    nome: 'Ração Golden',
    preco: 120.0,
    descricao: 'Ração especial',
    imagemUrl: '',
    categoria: 'Rações',
  );

  const testProduct2 = Produto(
    id: '2',
    nome: 'Brinquedo Bola',
    preco: 25.0,
    descricao: 'Bola de borracha',
    imagemUrl: '',
    categoria: 'Brinquedos',
  );

  setUp(() {
    cartCubit = CartCubit();
  });

  tearDown(() {
    cartCubit.close();
  });

  group('CartCubit', () {
    test('initial state should be empty CartState', () {
      expect(cartCubit.state.items, isEmpty);
      expect(cartCubit.state.totalPrice, 0.0);
      expect(cartCubit.state.totalItems, 0);
    });

    group('addToCart', () {
      test('should add new product to cart', () {
        cartCubit.addToCart(testProduct, quantidade: 2);

        expect(cartCubit.state.items.length, 1);
        expect(cartCubit.state.items.first.produto, testProduct);
        expect(cartCubit.state.items.first.quantidade, 2);
        expect(cartCubit.state.totalPrice, 240.0);
        expect(cartCubit.state.totalItems, 2);
      });

      test('should increment quantity if product already exists in cart', () {
        cartCubit.addToCart(testProduct, quantidade: 1);
        cartCubit.addToCart(testProduct, quantidade: 2);

        expect(cartCubit.state.items.length, 1);
        expect(cartCubit.state.items.first.quantidade, 3);
        expect(cartCubit.state.totalPrice, 360.0);
        expect(cartCubit.state.totalItems, 3);
      });
    });

    group('updateQuantity', () {
      test('should update quantity of existing product', () {
        cartCubit.addToCart(testProduct, quantidade: 1);
        cartCubit.updateQuantity('1', 5);

        expect(cartCubit.state.items.first.quantidade, 5);
        expect(cartCubit.state.totalPrice, 600.0);
      });

      test('should remove product if quantity is updated to <= 0', () {
        cartCubit.addToCart(testProduct, quantidade: 2);
        cartCubit.updateQuantity('1', 0);

        expect(cartCubit.state.items, isEmpty);
      });
    });

    group('removeFromCart', () {
      test('should remove product from cart', () {
        cartCubit.addToCart(testProduct, quantidade: 1);
        cartCubit.addToCart(testProduct2, quantidade: 1);
        cartCubit.removeFromCart('1');

        expect(cartCubit.state.items.length, 1);
        expect(cartCubit.state.items.first.produto, testProduct2);
      });
    });

    group('clearCart', () {
      test('should empty the cart', () {
        cartCubit.addToCart(testProduct, quantidade: 3);
        cartCubit.clearCart();

        expect(cartCubit.state.items, isEmpty);
        expect(cartCubit.state.totalPrice, 0.0);
      });
    });
  });
}
