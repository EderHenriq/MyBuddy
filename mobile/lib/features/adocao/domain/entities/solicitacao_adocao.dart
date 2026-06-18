import 'package:equatable/equatable.dart';

class SolicitacaoAdocao extends Equatable {
  final String id;
  final String petId;
  final String petNome;
  final String petImagemUrl;
  final String ongId;
  final String adotanteId;
  final String adotanteNome;
  final String adotanteEmail;
  final String adotanteTelefone;
  final String data;
  final String status;

  const SolicitacaoAdocao({
    required this.id,
    required this.petId,
    required this.petNome,
    required this.petImagemUrl,
    required this.ongId,
    required this.adotanteId,
    required this.adotanteNome,
    required this.adotanteEmail,
    required this.adotanteTelefone,
    required this.data,
    required this.status,
  });

  SolicitacaoAdocao copyWith({
    String? id,
    String? petId,
    String? petNome,
    String? petImagemUrl,
    String? ongId,
    String? adotanteId,
    String? adotanteNome,
    String? adotanteEmail,
    String? adotanteTelefone,
    String? data,
    String? status,
  }) {
    return SolicitacaoAdocao(
      id: id ?? this.id,
      petId: petId ?? this.petId,
      petNome: petNome ?? this.petNome,
      petImagemUrl: petImagemUrl ?? this.petImagemUrl,
      ongId: ongId ?? this.ongId,
      adotanteId: adotanteId ?? this.adotanteId,
      adotanteNome: adotanteNome ?? this.adotanteNome,
      adotanteEmail: adotanteEmail ?? this.adotanteEmail,
      adotanteTelefone: adotanteTelefone ?? this.adotanteTelefone,
      data: data ?? this.data,
      status: status ?? this.status,
    );
  }

  @override
  List<Object?> get props => [
        id,
        petId,
        petNome,
        petImagemUrl,
        ongId,
        adotanteId,
        adotanteNome,
        adotanteEmail,
        adotanteTelefone,
        data,
        status,
      ];
}
