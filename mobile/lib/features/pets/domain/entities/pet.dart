class Pet {
  final String id;
  final String nome;
  final String especie;
  final String raca;
  final int idade;
  final String sexo;
  final String porte;
  final String cor;
  final String? pelagem;
  final String? cidade;
  final String? estado;
  final String imagemUrl;
  final List<String> fotosUrls;
  final String statusAdocao;
  final String? nomeOrganizacao;
  final String? organizacaoId;
  final bool microchipado;
  final bool vacinado;
  final bool castrado;

  const Pet({
    required this.id,
    required this.nome,
    required this.especie,
    required this.raca,
    required this.idade,
    required this.sexo,
    required this.porte,
    required this.cor,
    this.pelagem,
    this.cidade,
    this.estado,
    required this.imagemUrl,
    this.fotosUrls = const [],
    required this.statusAdocao,
    this.nomeOrganizacao,
    this.organizacaoId,
    this.microchipado = false,
    this.vacinado = false,
    this.castrado = false,
  });

  bool get disponivel => statusAdocao == 'DISPONIVEL';
}