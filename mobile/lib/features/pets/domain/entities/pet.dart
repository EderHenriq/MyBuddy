class Pet {
  final String id;
  final String nome;
  final String especie;
  final String raca;
  final int idade;
  final String sexo;
  final String porte;
  final String cidade;
  final String estado;
  final String imagemUrl;
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
    required this.cidade,
    required this.estado,
    required this.imagemUrl,
    this.vacinado = false,
    this.castrado = false,
  });
}
