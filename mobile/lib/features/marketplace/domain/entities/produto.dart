class Produto {
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
}

class PedidoCompra {
  final String id;
  final String clienteNome;
  final String produtoNome;
  final double preco;
  final String data;
  String status;

  PedidoCompra({
    required this.id,
    required this.clienteNome,
    required this.produtoNome,
    required this.preco,
    required this.data,
    required this.status,
  });
}
