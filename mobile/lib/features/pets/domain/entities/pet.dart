import 'package:equatable/equatable.dart';

class Pet extends Equatable {
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
  final String? adotanteId;

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
    this.adotanteId,
  });

  bool get disponivel => statusAdocao == 'DISPONIVEL';

  Pet copyWith({
    String? id,
    String? nome,
    String? especie,
    String? raca,
    int? idade,
    String? sexo,
    String? porte,
    String? cor,
    String? pelagem,
    String? cidade,
    String? estado,
    String? imagemUrl,
    List<String>? fotosUrls,
    String? statusAdocao,
    String? nomeOrganizacao,
    String? organizacaoId,
    bool? microchipado,
    bool? vacinado,
    bool? castrado,
    String? adotanteId,
  }) {
    return Pet(
      id: id ?? this.id,
      nome: nome ?? this.nome,
      especie: especie ?? this.especie,
      raca: raca ?? this.raca,
      idade: idade ?? this.idade,
      sexo: sexo ?? this.sexo,
      porte: porte ?? this.porte,
      cor: cor ?? this.cor,
      pelagem: pelagem ?? this.pelagem,
      cidade: cidade ?? this.cidade,
      estado: estado ?? this.estado,
      imagemUrl: imagemUrl ?? this.imagemUrl,
      fotosUrls: fotosUrls ?? this.fotosUrls,
      statusAdocao: statusAdocao ?? this.statusAdocao,
      nomeOrganizacao: nomeOrganizacao ?? this.nomeOrganizacao,
      organizacaoId: organizacaoId ?? this.organizacaoId,
      microchipado: microchipado ?? this.microchipado,
      vacinado: vacinado ?? this.vacinado,
      castrado: castrado ?? this.castrado,
      adotanteId: adotanteId ?? this.adotanteId,
    );
  }

  @override
  List<Object?> get props => [
        id,
        nome,
        especie,
        raca,
        idade,
        sexo,
        porte,
        cor,
        pelagem,
        cidade,
        estado,
        imagemUrl,
        fotosUrls,
        statusAdocao,
        nomeOrganizacao,
        organizacaoId,
        microchipado,
        vacinado,
        castrado,
        adotanteId,
      ];
}