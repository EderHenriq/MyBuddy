import 'package:dartz/dartz.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:mybuddy_app/features/marketplace/domain/entities/produto.dart';
import 'package:mybuddy_app/features/marketplace/domain/repositories/products_repository.dart';
import 'package:mybuddy_app/features/marketplace/presentation/bloc/products_cubit.dart';
import 'package:mybuddy_app/core/errors/failures.dart';

class MockProductsRepository extends Mock implements ProductsRepository {}

void main() {
  late ProductsCubit productsCubit;
  late MockProductsRepository mockProductsRepository;

  const testProduct = Produto(
    id: '1',
    nome: 'Ração Golden',
    preco: 120.0,
    descricao: 'Ração especial',
    imagemUrl: '',
    categoria: 'Rações',
  );

  const testOrder = PedidoCompra(
    id: '10',
    clienteNome: 'Eder',
    produtoNome: 'Ração Golden',
    preco: 120.0,
    data: '18/06/2026',
    status: 'Em preparação',
  );

  setUpAll(() {
    registerFallbackValue(const Produto(
      id: '',
      nome: '',
      preco: 0.0,
      descricao: '',
      imagemUrl: '',
      categoria: '',
    ));
    registerFallbackValue(const PedidoCompra(
      id: '',
      clienteNome: '',
      produtoNome: '',
      preco: 0.0,
      data: '',
      status: '',
    ));
  });

  setUp(() {
    mockProductsRepository = MockProductsRepository();
    productsCubit = ProductsCubit(productsRepository: mockProductsRepository);
  });

  tearDown(() {
    productsCubit.close();
  });

  group('ProductsCubit', () {
    test('initial state should be ProductsInitial', () {
      expect(productsCubit.state, isA<ProductsInitial>());
    });

    group('loadProducts', () {
      test('should emit [ProductsLoading, ProductsLoaded] when repository returns data', () async {
        // Arrange
        when(() => mockProductsRepository.getProdutos()).thenAnswer((_) async => const Right([testProduct]));
        when(() => mockProductsRepository.getPedidos()).thenAnswer((_) async => const Right([testOrder]));

        // Assert later
        final expectation = expectLater(
          productsCubit.stream,
          emitsInOrder([
            isA<ProductsLoading>(),
            isA<ProductsLoaded>(),
          ]),
        );

        // Act
        productsCubit.loadProducts();

        await expectation;
      });

      test('should emit [ProductsLoading, ProductsError] when getProdutos returns failure', () async {
        // Arrange
        when(() => mockProductsRepository.getProdutos()).thenAnswer((_) async => const Left(ServerFailure('Error')));
        when(() => mockProductsRepository.getPedidos()).thenAnswer((_) async => const Right([testOrder]));

        // Assert later
        final expectation = expectLater(
          productsCubit.stream,
          emitsInOrder([
            isA<ProductsLoading>(),
            isA<ProductsError>(),
          ]),
        );

        // Act
        productsCubit.loadProducts();

        await expectation;
      });
    });

    group('cadastrarProduto', () {
      test('should return true and reload when registration is successful', () async {
        // Arrange
        when(() => mockProductsRepository.cadastrarProduto(any())).thenAnswer((_) async => const Right(testProduct));
        when(() => mockProductsRepository.getProdutos()).thenAnswer((_) async => const Right([testProduct]));
        when(() => mockProductsRepository.getPedidos()).thenAnswer((_) async => const Right([testOrder]));

        // Act
        final result = await productsCubit.cadastrarProduto(testProduct);

        // Assert
        expect(result, true);
        verify(() => mockProductsRepository.cadastrarProduto(any())).called(1);

        // Let background loadProducts finish
        await Future.delayed(const Duration(milliseconds: 5));
      });
    });

    group('comprarProduto', () {
      test('should return true and reload when order creation is successful', () async {
        // Arrange
        when(() => mockProductsRepository.criarPedido(any())).thenAnswer((_) async => const Right(testOrder));
        when(() => mockProductsRepository.getProdutos()).thenAnswer((_) async => const Right([testProduct]));
        when(() => mockProductsRepository.getPedidos()).thenAnswer((_) async => const Right([testOrder]));

        // Act
        final result = await productsCubit.comprarProduto('Eder', 'Ração Golden', 120.0);

        // Assert
        expect(result, true);
        verify(() => mockProductsRepository.criarPedido(any())).called(1);

        // Let background loadProducts finish
        await Future.delayed(const Duration(milliseconds: 5));
      });
    });
  });
}
