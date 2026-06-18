import 'package:dartz/dartz.dart';
import 'package:dio/dio.dart';
import 'package:mybuddy_app/core/di/injection_container.dart';
import 'package:mybuddy_app/core/errors/failures.dart';
import 'package:mybuddy_app/core/mock/mock_data.dart';
import 'package:mybuddy_app/features/marketplace/domain/entities/produto.dart';
import 'package:mybuddy_app/features/marketplace/domain/repositories/products_repository.dart';

class ProductsRepositoryMock implements ProductsRepository {
  final Dio _dio;

  ProductsRepositoryMock({Dio? dio}) : _dio = dio ?? sl<Dio>();

  final List<Produto> _produtos = List.from(MockData.produtos);

  final List<PedidoCompra> _pedidos = List.from(MockData.pedidos);

  int _getCategoryId(String categoryName) {
    switch (categoryName) {
      case 'Rações': return 1;
      case 'Petiscos': return 2;
      case 'Brinquedos': return 3;
      case 'Farmácia': return 4;
      case 'Higiene': return 5;
      case 'Camas': return 6;
      default: return 1;
    }
  }

  Produto _mapJsonToProduto(Map<String, dynamic> json) {
    String imgUrl = '';
    if (json['imagens'] != null && (json['imagens'] as List).isNotEmpty) {
      final firstImg = json['imagens'][0].toString();
      if (firstImg.startsWith('http') || firstImg.startsWith('/assets')) {
        imgUrl = firstImg;
      } else {
        imgUrl = 'http://localhost:8081/uploads/$firstImg';
      }
    } else {
      imgUrl = 'https://images.unsplash.com/photo-1589924691126-330b1353e568?q=80&w=400&auto=format&fit=crop';
    }

    return Produto(
      id: json['id'].toString(),
      nome: json['nome'] ?? '',
      preco: (json['preco'] as num?)?.toDouble() ?? 0.0,
      precoAntigo: (json['precoAntigo'] as num?)?.toDouble(),
      avaliacaoMedia: (json['notaMedia'] as num?)?.toDouble() ?? 4.7,
      nomeLoja: json['petshopNome'] ?? 'Petshop Parceiro',
      descricao: json['descricao'] ?? '',
      imagemUrl: imgUrl,
      categoria: json['categoriaNome'] ?? json['subCategoriaNome'] ?? 'Geral',
    );
  }

  @override
  Future<Either<Failure, List<Produto>>> getProdutos() async {
    try {
      final response = await _dio.get('produtos');
      if (response.statusCode == 200) {
        final data = response.data;
        List<dynamic> content = [];
        if (data is Map<String, dynamic> && data['content'] != null) {
          content = data['content'] as List;
        } else if (data is List) {
          content = data;
        }
        final apiProducts = content.map((item) => _mapJsonToProduto(item as Map<String, dynamic>)).toList();
        
        final merged = <String, Produto>{};
        for (final p in _produtos) {
          merged[p.id] = p;
        }
        for (final p in apiProducts) {
          merged[p.id] = p;
        }
        return Right(merged.values.toList());
      }
    } catch (e) {
      // Fallback transparente
    }
    await Future.delayed(const Duration(milliseconds: 200));
    return Right(List.from(_produtos));
  }

  @override
  Future<Either<Failure, Produto>> cadastrarProduto(Produto produto) async {
    try {
      final requestData = {
        'nome': produto.nome,
        'descricao': produto.descricao,
        'preco': produto.preco,
        'estoque': 10,
        'subCategoriaId': _getCategoryId(produto.categoria),
        'imagens': [produto.imagemUrl],
      };
      final response = await _dio.post('produtos', data: requestData);
      if (response.statusCode == 200 || response.statusCode == 201) {
        final apiProduct = _mapJsonToProduto(response.data as Map<String, dynamic>);
        _produtos.insert(0, apiProduct);
        return Right(apiProduct);
      }
    } catch (e) {
      // Fallback
    }
    await Future.delayed(const Duration(milliseconds: 300));
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
    _produtos.insert(0, newProduct);
    return Right(newProduct);
  }

  @override
  Future<Either<Failure, Produto>> atualizarProduto(Produto produto) async {
    try {
      final requestData = {
        'nome': produto.nome,
        'descricao': produto.descricao,
        'preco': produto.preco,
        'estoque': 10,
        'subCategoriaId': _getCategoryId(produto.categoria),
        'imagens': [produto.imagemUrl],
      };
      final response = await _dio.put('produtos/${produto.id}', data: requestData);
      if (response.statusCode == 200) {
        final apiProduct = _mapJsonToProduto(response.data as Map<String, dynamic>);
        final idx = _produtos.indexWhere((p) => p.id == produto.id);
        if (idx != -1) {
          _produtos[idx] = apiProduct;
        } else {
          _produtos.add(apiProduct);
        }
        return Right(apiProduct);
      }
    } catch (e) {
      // Fallback
    }
    await Future.delayed(const Duration(milliseconds: 200));
    final idx = _produtos.indexWhere((p) => p.id == produto.id);
    if (idx != -1) {
      _produtos[idx] = produto;
      return Right(produto);
    }
    _produtos.add(produto);
    return Right(produto);
  }

  @override
  Future<Either<Failure, List<PedidoCompra>>> getPedidos() async {
    try {
      final response = await _dio.get('pedidos/meus');
      if (response.statusCode == 200) {
        final List<dynamic> list = response.data as List;
        final apiOrders = list.map((item) {
          return PedidoCompra(
            id: item['id'].toString(),
            clienteNome: item['clienteNome'] ?? 'Adotante',
            produtoNome: item['itens'] != null && (item['itens'] as List).isNotEmpty 
                ? item['itens'][0]['produtoNome'] ?? 'Produto' 
                : 'Produto',
            preco: (item['valorTotal'] as num?)?.toDouble() ?? 0.0,
            data: item['dataCriacao'] ?? 'Hoje',
            status: item['status'] ?? 'Em preparação',
          );
        }).toList();

        final merged = <String, PedidoCompra>{};
        for (final o in _pedidos) {
          merged[o.id] = o;
        }
        for (final o in apiOrders) {
          merged[o.id] = o;
        }
        return Right(merged.values.toList());
      }
    } catch (e) {
      // Fallback
    }
    await Future.delayed(const Duration(milliseconds: 200));
    return Right(List.from(_pedidos));
  }

  @override
  Future<Either<Failure, PedidoCompra>> criarPedido(PedidoCompra pedido) async {
    try {
      final requestData = {
        'petshopId': 1,
        'enderecoEntrega': {
          'rua': 'Rua das Flores',
          'numero': '123',
          'cidade': 'Cidade',
          'estado': 'Estado',
          'cep': '12345-678',
        },
        'itens': [
          {
            'produtoId': 1,
            'quantidade': 1,
            'precoUnitario': pedido.preco,
          }
        ],
      };
      final response = await _dio.post('pedidos', data: requestData);
      if (response.statusCode == 200 || response.statusCode == 201) {
        final item = response.data;
        final apiOrder = PedidoCompra(
          id: item['id'].toString(),
          clienteNome: item['clienteNome'] ?? pedido.clienteNome,
          produtoNome: pedido.produtoNome,
          preco: pedido.preco,
          data: pedido.data,
          status: item['status'] ?? pedido.status,
        );
        _pedidos.insert(0, apiOrder);
        return Right(apiOrder);
      }
    } catch (e) {
      // Fallback
    }
    await Future.delayed(const Duration(milliseconds: 300));
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
    try {
      final response = await _dio.put('pedidos/$pedidoId/status', queryParameters: {'status': status});
      if (response.statusCode == 200) {
        final index = _pedidos.indexWhere((p) => p.id == pedidoId);
        if (index != -1) {
          _pedidos[index] = _pedidos[index].copyWith(status: status);
        }
        return const Right(null);
      }
    } catch (e) {
      // Fallback
    }
    await Future.delayed(const Duration(milliseconds: 200));
    final index = _pedidos.indexWhere((p) => p.id == pedidoId);
    if (index != -1) {
      _pedidos[index] = _pedidos[index].copyWith(status: status);
    }
    return const Right(null);
  }
}
