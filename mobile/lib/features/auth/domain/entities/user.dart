import 'package:equatable/equatable.dart';

class User extends Equatable {
  final String id;
  final String email;
  final String nome;
  final String? telefone;
  final List<String> roles;

  const User({
    required this.id,
    required this.email,
    required this.nome,
    this.telefone,
    required this.roles,
  });

  bool get isAdmin => roles.contains('ROLE_ADMIN');
  bool get isOng => roles.contains('ROLE_ONG');
  bool get isAdotante => roles.contains('ROLE_ADOTANTE');
  bool get isPetshop => roles.contains('ROLE_PETSHOP');

  User copyWith({
    String? id,
    String? email,
    String? nome,
    String? telefone,
    List<String>? roles,
  }) {
    return User(
      id: id ?? this.id,
      email: email ?? this.email,
      nome: nome ?? this.nome,
      telefone: telefone ?? this.telefone,
      roles: roles ?? this.roles,
    );
  }

  @override
  List<Object?> get props => [id, email, nome, telefone, roles];
}
