import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mybuddy_app/features/marketplace/domain/entities/produto.dart';
import 'package:mybuddy_app/features/marketplace/domain/repositories/products_repository.dart';
import 'package:mybuddy_app/features/marketplace/presentation/bloc/products_state.dart';
export 'products_state.dart';

class ProductsCubit extends Cubit<ProductsState> {
  final ProductsRepository productsRepository;

  ProductsCubit({required this.productsRepository}) : super(ProductsInitial());

  Future<void> loadProducts() async {
    emit(ProductsLoading());
    final productsResult = await productsRepository.getProdutos();
    final ordersResult = await productsRepository.getPedidos();
    
    productsResult.fold(
      (failure) => emit(const ProductsError('Erro ao buscar produtos')),
      (produtos) {
        ordersResult.fold(
          (failure) => emit(const ProductsError('Erro ao buscar pedidos')),
          (pedidos) => emit(ProductsLoaded(produtos: produtos, pedidos: pedidos)),
        );
      },
    );
  }

  Future<bool> cadastrarProduto(Produto produto) async {
    final result = await productsRepository.cadastrarProduto(produto);
    return result.fold(
      (failure) => false,
      (newProduct) {
        loadProducts();
        return true;
      },
    );
  }

  Future<bool> atualizarProduto(Produto produto) async {
    final result = await productsRepository.atualizarProduto(produto);
    return result.fold(
      (failure) => false,
      (updatedProduct) {
        loadProducts();
        return true;
      },
    );
  }

  Future<bool> comprarProduto(
    String clienteNome,
    String produtoNome,
    double preco, {
    String? produtoId,
    int? quantidade,
  }) async {
    final now = DateTime.now();
    final dataStr = '${now.day.toString().padLeft(2, '0')}/${now.month.toString().padLeft(2, '0')}/${now.year}';
    final result = await productsRepository.criarPedido(
      PedidoCompra(
        id: '',
        clienteNome: clienteNome,
        produtoNome: produtoNome,
        preco: preco,
        data: dataStr,
        status: 'Em preparação',
        produtoId: produtoId,
        quantidade: quantidade,
      ),
    );
    return result.fold(
      (failure) => false,
      (newOrder) {
        return true;
      },
    );
  }

  Future<bool> atualizarStatusPedido(String pedidoId, String status) async {
    final result = await productsRepository.atualizarStatusPedido(pedidoId, status);
    return result.fold(
      (failure) => false,
      (_) {
        loadProducts();
        return true;
      },
    );
  }
}
