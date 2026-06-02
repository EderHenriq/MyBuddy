class Pet {
  final String id;
  final String nome;
  final String especie;
  final String raca;
  final int idade;
  final String porte;
  final String cor;
  final String? pelagem;
  final String sexo;
  final List<String> fotosUrls;
  final String statusAdocao;
  final String? nomeOrganizacao;
  final String? organizacaoId;
  final bool microchipado;
  final bool vacinado;
  final bool castrado;
  final String? cidade;
  final String? estado;

  const Pet({
    required this.id,
    required this.nome,
    required this.especie,
    required this.raca,
    required this.idade,
    required this.porte,
    required this.cor,
    this.pelagem,
    required this.sexo,
    required this.fotosUrls,
    required this.statusAdocao,
    this.nomeOrganizacao,
    this.organizacaoId,
    required this.microchipado,
    required this.vacinado,
    required this.castrado,
    this.cidade,
    this.estado,
  });

  bool get disponivel => statusAdocao == 'DISPONIVEL';
}