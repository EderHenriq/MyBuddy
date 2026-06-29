import 'package:flutter/material.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';
import 'package:mybuddy_app/shared/widgets/app_card.dart';

class AdminDashboardPage extends StatefulWidget {
  const AdminDashboardPage({super.key});

  @override
  State<AdminDashboardPage> createState() => _AdminDashboardPageState();
}

class _AdminDashboardPageState extends State<AdminDashboardPage> {
  final List<Map<String, dynamic>> _usuarios = [
    {'nome': 'Eder Henrique', 'email': 'eder@mybuddy.com', 'role': 'Adotante', 'ativo': true},
    {'nome': 'ONG Amigo Fiel', 'email': 'ong@mybuddy.com', 'role': 'ONG', 'ativo': true},
    {'nome': 'Petshop Parceiro', 'email': 'petshop@mybuddy.com', 'role': 'Petshop', 'ativo': true},
    {'nome': 'Ana Clara', 'email': 'anaclara@mybuddy.com', 'role': 'Adotante', 'ativo': false},
  ];

  void _toggleUsuarioAtivo(int index) {
    setState(() {
      _usuarios[index]['ativo'] = !_usuarios[index]['ativo'];
      final status = _usuarios[index]['ativo'] ? 'ativada' : 'desativada';
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Conta de ${_usuarios[index]['nome']} $status com sucesso!'),
          backgroundColor: _usuarios[index]['ativo'] ? AppColors.success : Colors.orange,
        ),
      );
    });
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Painel Administrativo'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Grid de Métricas
            Text(
              'Métricas Gerais da Plataforma',
              style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            GridView.count(
              crossAxisCount: 2,
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              mainAxisSpacing: 12,
              crossAxisSpacing: 12,
              childAspectRatio: 1.6,
              children: [
                _buildMetricaCard('Total Usuários', '154', Icons.people_outline_rounded, Colors.blue, isDark),
                _buildMetricaCard('ONGs Parceiras', '18', Icons.volunteer_activism_outlined, Colors.purple, isDark),
                _buildMetricaCard('Petshops', '12', Icons.storefront_outlined, Colors.orange, isDark),
                _buildMetricaCard('Adoções Feitas', '34', Icons.check_circle_outline_rounded, Colors.green, isDark),
              ],
            ),
            const SizedBox(height: 32),

            // Lista de Usuários
            Text(
              'Gerenciamento de Contas',
              style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            ListView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: _usuarios.length,
              itemBuilder: (context, index) {
                final user = _usuarios[index];
                final bool ativo = user['ativo'];

                return Padding(
                  padding: const EdgeInsets.only(bottom: 12.0),
                  child: AppCard(
                    padding: const EdgeInsets.all(16),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              user['nome'],
                              style: theme.textTheme.bodyLarge?.copyWith(fontWeight: FontWeight.bold),
                            ),
                            Text(
                              '${user['email']} • ${user['role']}',
                              style: theme.textTheme.bodySmall?.copyWith(color: Colors.grey),
                            ),
                          ],
                        ),
                        Switch(
                          value: ativo,
                          activeThumbColor: AppColors.primary,
                          onChanged: (_) => _toggleUsuarioAtivo(index),
                        ),
                      ],
                    ),
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildMetricaCard(String title, String value, IconData icon, Color color, bool isDark) {
    return AppCard(
      padding: const EdgeInsets.all(12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Icon(icon, color: color, size: 24),
              const SizedBox.shrink(),
            ],
          ),
          const SizedBox(height: 8),
          Text(
            value,
            style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 20),
          ),
          Text(
            title,
            style: const TextStyle(color: Colors.grey, fontSize: 10),
          ),
        ],
      ),
    );
  }
}
