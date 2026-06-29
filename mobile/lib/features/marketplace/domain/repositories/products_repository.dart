import 'package:dartz/dartz.dart';
import 'package:mybuddy_app/core/errors/failures.dart';
import 'package:mybuddy_app/features/marketplace/domain/entities/produto.dart';

abstract class ProductsRepository {
  Future<Either<Failure, List<Produto>>> getProdutos();
  Future<Either<Failure, Produto>> cadastrarProduto(Produto produto);
  Future<Either<Failure, Produto>> atualizarProduto(Produto produto);
  Future<Either<Failure, List<PedidoCompra>>> getPedidos();
  Future<Either<Failure, PedidoCompra>> criarPedido(PedidoCompra pedido);
  Future<Either<Failure, void>> atualizarStatusPedido(String pedidoId, String status);
}
