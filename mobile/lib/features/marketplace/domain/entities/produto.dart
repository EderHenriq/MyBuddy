import 'package:equatable/equatable.dart';

class Produto extends Equatable {
  final String id;
  final String nome;
  final double preco;
  final double? precoAntigo;
  final double? avaliacaoMedia;
  final String? nomeLoja;
  final String descricao;
  final String imagemUrl;
  final String categoria;

  const Produto({
    required this.id,
    required this.nome,
    required this.preco,
    this.precoAntigo,
    this.avaliacaoMedia,
    this.nomeLoja,
    required this.descricao,
    required this.imagemUrl,
    required this.categoria,
  });

  Produto copyWith({
    String? id,
    String? nome,
    double? preco,
    double? precoAntigo,
    double? avaliacaoMedia,
    String? nomeLoja,
    String? descricao,
    String? imagemUrl,
    String? categoria,
  }) {
    return Produto(
      id: id ?? this.id,
      nome: nome ?? this.nome,
      preco: preco ?? this.preco,
      precoAntigo: precoAntigo ?? this.precoAntigo,
      avaliacaoMedia: avaliacaoMedia ?? this.avaliacaoMedia,
      nomeLoja: nomeLoja ?? this.nomeLoja,
      descricao: descricao ?? this.descricao,
      imagemUrl: imagemUrl ?? this.imagemUrl,
      categoria: categoria ?? this.categoria,
    );
  }

  @override
  List<Object?> get props => [
        id,
        nome,
        preco,
        precoAntigo,
        avaliacaoMedia,
        nomeLoja,
        descricao,
        imagemUrl,
        categoria,
      ];
}

class PedidoCompra extends Equatable {
  final String id;
  final String clienteNome;
  final String produtoNome;
  final double preco;
  final String data;
  final String status;

  const PedidoCompra({
    required this.id,
    required this.clienteNome,
    required this.produtoNome,
    required this.preco,
    required this.data,
    required this.status,
  });

  PedidoCompra copyWith({
    String? id,
    String? clienteNome,
    String? produtoNome,
    double? preco,
    String? data,
    String? status,
  }) {
    return PedidoCompra(
      id: id ?? this.id,
      clienteNome: clienteNome ?? this.clienteNome,
      produtoNome: produtoNome ?? this.produtoNome,
      preco: preco ?? this.preco,
      data: data ?? this.data,
      status: status ?? this.status,
    );
  }

  @override
  List<Object?> get props => [
        id,
        clienteNome,
        produtoNome,
        preco,
        data,
        status,
      ];
}
