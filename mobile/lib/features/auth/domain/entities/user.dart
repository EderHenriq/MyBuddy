class User {
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
}
