import 'package:equatable/equatable.dart';
import 'package:mybuddy_app/features/marketplace/domain/entities/produto.dart';

abstract class ProductsState extends Equatable {
  const ProductsState();
  @override
  List<Object?> get props => [];
}

class ProductsInitial extends ProductsState {}
class ProductsLoading extends ProductsState {}
class ProductsLoaded extends ProductsState {
  final List<Produto> produtos;
  final List<PedidoCompra> pedidos;
  const ProductsLoaded({required this.produtos, required this.pedidos});
  
  @override
  List<Object?> get props => [produtos, pedidos];
}
class ProductsError extends ProductsState {
  final String message;
  const ProductsError(this.message);
  @override
  List<Object?> get props => [message];
}
