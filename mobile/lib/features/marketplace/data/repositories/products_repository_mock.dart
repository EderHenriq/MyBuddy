import 'package:dartz/dartz.dart';
import 'package:mybuddy_app/core/errors/failures.dart';
import 'package:mybuddy_app/features/marketplace/domain/entities/produto.dart';
import 'package:mybuddy_app/features/marketplace/domain/repositories/products_repository.dart';

class ProductsRepositoryMock implements ProductsRepository {
  final List<Produto> _produtos = [
    const Produto(
      id: '1',
      nome: 'Ração Golden Duo Cães Adultos - 15kg',
      preco: 159.90,
      descricao: 'Alimento Premium Especial formulado para cães adultos de médio e grande porte. Sem corantes e aromatizantes artificiais.',
      imagemUrl: 'https://images.unsplash.com/photo-1589924691126-330b1353e568?q=80&w=400&auto=format&fit=crop',
      categoria: 'Alimentação',
    ),
    const Produto(
      id: '2',
      nome: 'Arranhador Torre com Brinquedo para Gatos',
      preco: 89.90,
      descricao: 'Arranhador de sisal de alta qualidade com base revestida de pelúcia macia. Ideal para o entretenimento e saúde das garras do seu gato.',
      imagemUrl: 'https://images.unsplash.com/photo-1545249390-6bdfa286032f?q=80&w=400&auto=format&fit=crop',
      categoria: 'Brinquedos',
    ),
    const Produto(
      id: '3',
      nome: 'Guia Retrátil para Cães - 5 Metros',
      preco: 45.00,
      descricao: 'Guia retrátil resistente com trava de segurança de acionamento rápido. Suporta cães de até 20kg. Empunhadura ergonômica.',
      imagemUrl: 'https://images.unsplash.com/photo-1576201836106-db1758fd1c97?q=80&w=400&auto=format&fit=crop',
      categoria: 'Acessórios',
    ),
    const Produto(
      id: '4',
      nome: 'Shampoo Neutro Pet Care - 500ml',
      preco: 22.50,
      descricao: 'Shampoo neutro hipoalergênico com extrato de aveia e mel. Deixa os pelos macios, brilhantes e fáceis de pentear. Indicado para cães e gatos.',
      imagemUrl: 'https://images.unsplash.com/photo-1516733725897-1aa73b87c8e8?q=80&w=400&auto=format&fit=crop',
      categoria: 'Higiene',
    ),
  ];

  final List<PedidoCompra> _pedidos = [
    PedidoCompra(
      id: '1',
      clienteNome: 'Adotante MyBuddy',
      produtoNome: 'Guia Retrátil para Cães - 5 Metros',
      preco: 45.00,
      data: '10/06/2026',
      status: 'Entregue',
    ),
  ];

  @override
  Future<Either<Failure, List<Produto>>> getProdutos() async {
    await Future.delayed(const Duration(milliseconds: 300));
    return Right(List.from(_produtos));
  }

  @override
  Future<Either<Failure, Produto>> cadastrarProduto(Produto produto) async {
    await Future.delayed(const Duration(milliseconds: 500));
    final newProduct = Produto(
      id: (DateTime.now().millisecondsSinceEpoch).toString(),
      nome: produto.nome,
      preco: produto.preco,
      descricao: produto.descricao,
      imagemUrl: produto.imagemUrl.isEmpty 
          ? 'https://images.unsplash.com/photo-1535268647977-a403b69fc756?q=80&w=400&auto=format&fit=crop'
          : produto.imagemUrl,
      categoria: produto.categoria,
    );
    _produtos.add(newProduct);
    return Right(newProduct);
  }

  @override
  Future<Either<Failure, List<PedidoCompra>>> getPedidos() async {
    await Future.delayed(const Duration(milliseconds: 300));
    return Right(List.from(_pedidos));
  }

  @override
  Future<Either<Failure, PedidoCompra>> criarPedido(PedidoCompra pedido) async {
    await Future.delayed(const Duration(milliseconds: 400));
    final newPedido = PedidoCompra(
      id: (DateTime.now().millisecondsSinceEpoch).toString(),
      clienteNome: pedido.clienteNome,
      produtoNome: pedido.produtoNome,
      preco: pedido.preco,
      data: pedido.data,
      status: pedido.status,
    );
    _pedidos.add(newPedido);
    return Right(newPedido);
  }

  @override
  Future<Either<Failure, void>> atualizarStatusPedido(String pedidoId, String status) async {
    await Future.delayed(const Duration(milliseconds: 200));
    final pedido = _pedidos.firstWhere((p) => p.id == pedidoId);
    pedido.status = status;
    return const Right(null);
  }
}
