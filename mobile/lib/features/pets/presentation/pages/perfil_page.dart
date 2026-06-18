import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:mybuddy_app/features/auth/domain/entities/user.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_event.dart';
import 'package:mybuddy_app/features/auth/presentation/bloc/auth_state.dart';
import 'package:mybuddy_app/features/marketplace/presentation/bloc/products_cubit.dart';
import 'package:mybuddy_app/shared/theme/theme_cubit.dart';
import 'package:mybuddy_app/shared/widgets/app_button.dart';
import 'package:mybuddy_app/shared/widgets/app_card.dart';
import 'package:mybuddy_app/shared/widgets/app_input.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';

class PerfilPage extends StatefulWidget {
  const PerfilPage({super.key});

  @override
  State<PerfilPage> createState() => _PerfilPageState();
}

class _PerfilPageState extends State<PerfilPage> {
  String? _localName;
  String? _localEmail;
  bool _notificationsEnabled = true;
  String _themePreference = 'Sistema';

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    // Sincronizar _themePreference com o estado real do ThemeCubit
    final currentThemeMode = context.read<ThemeCubit>().state;
    if (currentThemeMode == ThemeMode.system) {
      _themePreference = 'Sistema';
    } else if (currentThemeMode == ThemeMode.light) {
      _themePreference = 'Claro';
    } else {
      _themePreference = 'Escuro';
    }

    // Pegando dados do estado de auth se estiver disponível
    final authState = context.read<AuthBloc>().state;
    User? loggedUser;
    String userName = _localName ?? 'Adotante MyBuddy';
    String userEmail = _localEmail ?? 'user@mybuddy.com';

    if (authState is AuthAuthenticated) {
      loggedUser = authState.user;
      userName = _localName ?? authState.user.nome;
      userEmail = _localEmail ?? authState.user.email;
    }

    // Modal para editar dados
    void showEditProfileModal() {
      final nameController = TextEditingController(text: userName);
      final emailController = TextEditingController(text: userEmail);
      final formKey = GlobalKey<FormState>();

      showModalBottomSheet(
        context: context,
        isScrollControlled: true,
        backgroundColor: Colors.transparent,
        builder: (context) {
          return Padding(
            padding: EdgeInsets.only(bottom: MediaQuery.of(context).viewInsets.bottom),
            child: Container(
              decoration: BoxDecoration(
                color: isDark ? AppColors.darkSurface : Colors.white,
                borderRadius: const BorderRadius.vertical(top: Radius.circular(24)),
              ),
              padding: const EdgeInsets.all(32),
              child: Form(
                key: formKey,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    Text(
                      'Editar Perfil',
                      style: theme.textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 24),
                    AppInput(
                      controller: nameController,
                      labelText: 'Nome Completo',
                      prefixIcon: Icons.person_outline_rounded,
                      validator: (value) {
                        if (value == null || value.trim().isEmpty) return 'Informe seu nome';
                        return null;
                      },
                    ),
                    const SizedBox(height: 16),
                    AppInput(
                      controller: emailController,
                      labelText: 'Email',
                      prefixIcon: Icons.email_outlined,
                      keyboardType: TextInputType.emailAddress,
                      validator: (value) {
                        if (value == null || value.isEmpty) return 'Informe o email';
                        return null;
                      },
                    ),
                    const SizedBox(height: 32),
                    AppButton(
                      text: 'Salvar Alterações',
                      onPressed: () {
                        if (formKey.currentState?.validate() ?? false) {
                          setState(() {
                            _localName = nameController.text.trim();
                            _localEmail = emailController.text.trim();
                          });
                          Navigator.pop(context);
                          ScaffoldMessenger.of(context).showSnackBar(
                            const SnackBar(
                              content: Text('Perfil atualizado com sucesso!'),
                              backgroundColor: AppColors.success,
                            ),
                          );
                        }
                      },
                    ),
                  ],
                ),
              ),
            ),
          );
        },
      );
    }

    // Modal de Histórico de Doações
    void showDonationHistoryModal() {
      showModalBottomSheet(
        context: context,
        backgroundColor: Colors.transparent,
        builder: (context) {
          return Container(
            decoration: BoxDecoration(
              color: isDark ? AppColors.darkSurface : Colors.white,
              borderRadius: const BorderRadius.vertical(top: Radius.circular(24)),
            ),
            padding: const EdgeInsets.all(24),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Text(
                  'Histórico de Doações',
                  style: theme.textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 20),
                Expanded(
                  child: ListView(
                    children: [
                      _buildHistoryItem('Doação Mensal', 'R\$ 50,00', '10/05/2026', 'ONG Amigo Fiel', isDark),
                      _buildHistoryItem('Doação Emergencial', 'R\$ 100,00', '22/04/2026', 'SOS Patinhas', isDark),
                      _buildHistoryItem('Doação Única', 'R\$ 30,00', '05/03/2026', 'Ajuda Animal', isDark),
                    ],
                  ),
                ),
              ],
            ),
          );
        },
      );
    }



    // Modal de Pedidos Realizados (Reativo com ProductsCubit)
    void showPurchasesModal() {
      showModalBottomSheet(
        context: context,
        backgroundColor: Colors.transparent,
        builder: (context) {
          return Container(
            decoration: BoxDecoration(
              color: isDark ? AppColors.darkSurface : Colors.white,
              borderRadius: const BorderRadius.vertical(top: Radius.circular(24)),
            ),
            padding: const EdgeInsets.all(24),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Text(
                  'Minhas Compras',
                  style: theme.textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 20),
                Expanded(
                  child: BlocBuilder<ProductsCubit, ProductsState>(
                    builder: (context, state) {
                      if (state is ProductsLoading) {
                        return const Center(child: CircularProgressIndicator());
                      }
                      if (state is ProductsLoaded) {
                        final minhasCompras = state.pedidos.where((p) => p.clienteNome == userName).toList();

                        if (minhasCompras.isEmpty) {
                          return const Center(
                            child: Padding(
                              padding: EdgeInsets.all(16.0),
                              child: Text('Nenhuma compra realizada no Marketplace.'),
                            ),
                          );
                        }

                        return ListView.builder(
                          itemCount: minhasCompras.length,
                          itemBuilder: (context, idx) {
                            final comp = minhasCompras[idx];
                            Color statusColor = Colors.orange;
                            if (comp.status == 'Enviado') statusColor = Colors.blue;
                            if (comp.status == 'Entregue') statusColor = AppColors.success;

                            return Container(
                              margin: const EdgeInsets.only(bottom: 12),
                              padding: const EdgeInsets.all(16),
                              decoration: BoxDecoration(
                                color: isDark ? AppColors.darkBackground : AppColors.background,
                                borderRadius: BorderRadius.circular(12),
                              ),
                              child: Row(
                                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                children: [
                                  Expanded(
                                    child: Column(
                                      crossAxisAlignment: CrossAxisAlignment.start,
                                      children: [
                                        Text(comp.produtoNome, style: const TextStyle(fontWeight: FontWeight.bold), maxLines: 1, overflow: TextOverflow.ellipsis),
                                        const SizedBox(height: 4),
                                        Text('Status: ${comp.status}', style: TextStyle(color: statusColor, fontWeight: FontWeight.bold, fontSize: 12)),
                                      ],
                                    ),
                                  ),
                                  Text(
                                    'R\$ ${comp.preco.toStringAsFixed(2)}',
                                    style: const TextStyle(fontWeight: FontWeight.bold),
                                  ),
                                ],
                              ),
                            );
                          },
                        );
                      }
                      return const Center(child: Text('Erro ao carregar compras.'));
                    },
                  ),
                ),
              ],
            ),
          );
        },
      );
    }

    // Modal de Configurações
    void showSettingsModal() {
      showModalBottomSheet(
        context: context,
        backgroundColor: Colors.transparent,
        builder: (context) {
          return StatefulBuilder(
            builder: (context, setModalState) {
              return Container(
                decoration: BoxDecoration(
                  color: isDark ? AppColors.darkSurface : Colors.white,
                  borderRadius: const BorderRadius.vertical(top: Radius.circular(24)),
                ),
                padding: const EdgeInsets.all(24),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Text(
                      'Configurações',
                      style: theme.textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 24),
                    SwitchListTile(
                      title: const Text('Notificações Push'),
                      subtitle: const Text('Receba avisos de adoções e ofertas'),
                      activeThumbColor: AppColors.primary,
                      activeTrackColor: AppColors.primary.withAlpha(100),
                      value: _notificationsEnabled,
                      onChanged: (value) {
                        setModalState(() {
                          _notificationsEnabled = value;
                        });
                        setState(() {
                          _notificationsEnabled = value;
                        });
                      },
                    ),
                    const Divider(),
                    ListTile(
                      title: const Text('Tema do Aplicativo'),
                      trailing: DropdownButton<String>(
                        value: _themePreference,
                        items: ['Sistema', 'Claro', 'Escuro'].map((String val) {
                          return DropdownMenuItem<String>(
                            value: val,
                            child: Text(val),
                          );
                        }).toList(),
                        onChanged: (value) {
                          if (value != null) {
                            setModalState(() {
                              _themePreference = value;
                            });
                            setState(() {
                              _themePreference = value;
                            });

                            ThemeMode mode;
                            if (value == 'Sistema') {
                              mode = ThemeMode.system;
                            } else if (value == 'Claro') {
                              mode = ThemeMode.light;
                            } else {
                              mode = ThemeMode.dark;
                            }
                            context.read<ThemeCubit>().updateTheme(mode);

                            ScaffoldMessenger.of(context).showSnackBar(
                              SnackBar(
                                content: Text('Preferência de tema alterada para: $value'),
                                duration: const Duration(seconds: 1),
                              ),
                            );
                          }
                        },
                      ),
                    ),
                  ],
                ),
              );
            },
          );
        },
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('Meu Perfil'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            const SizedBox(height: 20),
            // Avatar
            CircleAvatar(
              radius: 60,
              backgroundColor: AppColors.primary.withAlpha(20),
              child: const Icon(
                Icons.person_rounded,
                size: 70,
                color: AppColors.primary,
              ),
            ),
            const SizedBox(height: 20),
            // Nome do usuário
            Text(
              userName,
              style: theme.textTheme.headlineSmall?.copyWith(
                fontWeight: FontWeight.bold,
                color: isDark ? Colors.white : Colors.black87,
              ),
            ),
            const SizedBox(height: 4),
            // Email do usuário
            Text(
              userEmail,
              style: theme.textTheme.bodyMedium?.copyWith(
                color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
              ),
            ),
            const SizedBox(height: 32),

            // Exibir badge do perfil
            if (loggedUser != null) ...[
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                decoration: BoxDecoration(
                  color: AppColors.primary.withAlpha(20),
                  borderRadius: BorderRadius.circular(20),
                  border: Border.all(color: AppColors.primary.withAlpha(80)),
                ),
                child: Text(
                  loggedUser.isOng 
                      ? 'PERFIL ONG' 
                      : (loggedUser.isPetshop 
                          ? 'PERFIL PETSHOP' 
                          : (loggedUser.isAdmin ? 'ADMINISTRADOR' : 'PERFIL ADOTANTE')),
                  style: const TextStyle(
                    color: AppColors.primary,
                    fontWeight: FontWeight.bold,
                    fontSize: 11,
                  ),
                ),
              ),
              const SizedBox(height: 24),
            ],

            // Itens interativos do perfil de acordo com a Role
            if (loggedUser != null && loggedUser.isOng) ...[
              _buildProfileItem(context, Icons.edit_outlined, 'Editar Dados', showEditProfileModal, isDark),
              _buildProfileItem(context, Icons.add_circle_outline_rounded, 'Cadastrar Novo Pet', () => context.push('/cadastrar-pet'), isDark),
              _buildProfileItem(context, Icons.pets_outlined, 'Meus Pets Cadastrados', () => context.push('/meus-pets'), isDark),
              _buildProfileItem(context, Icons.receipt_long_outlined, 'Solicitações de Adoção', () => context.push('/solicitacoes-ong'), isDark),
              _buildProfileItem(context, Icons.settings_outlined, 'Configurações', showSettingsModal, isDark),
            ] else if (loggedUser != null && loggedUser.isPetshop) ...[
              _buildProfileItem(context, Icons.edit_outlined, 'Editar Dados', showEditProfileModal, isDark),
              _buildProfileItem(context, Icons.add_business_outlined, 'Cadastrar Novo Produto', () => context.push('/cadastrar-produto'), isDark),
              _buildProfileItem(context, Icons.storefront_outlined, 'Meus Produtos à Venda', () => context.push('/meus-produtos'), isDark),
              _buildProfileItem(context, Icons.receipt_long_outlined, 'Pedidos Recebidos', () => context.push('/pedidos-petshop'), isDark),
              _buildProfileItem(context, Icons.settings_outlined, 'Configurações', showSettingsModal, isDark),
            ] else if (loggedUser != null && loggedUser.isAdmin) ...[
              _buildProfileItem(context, Icons.edit_outlined, 'Editar Dados', showEditProfileModal, isDark),
              _buildProfileItem(context, Icons.dashboard_outlined, 'Painel Administrativo', () => context.push('/admin-dashboard'), isDark),
              _buildProfileItem(context, Icons.settings_outlined, 'Configurações', showSettingsModal, isDark),
            ] else ...[
              // Adotante (ou Padrão)
              _buildProfileItem(context, Icons.edit_outlined, 'Editar Dados', showEditProfileModal, isDark),
              _buildProfileItem(context, Icons.favorite_outline_rounded, 'Meus Favoritos', () => context.push('/favoritos'), isDark),
              _buildProfileItem(context, Icons.pets_outlined, 'Meus Processos de Adoção', () => context.push('/meus-processos-adocao'), isDark),
              _buildProfileItem(context, Icons.shopping_bag_outlined, 'Minhas Compras', showPurchasesModal, isDark),
              _buildProfileItem(context, Icons.history_rounded, 'Histórico de Doações', showDonationHistoryModal, isDark),
              _buildProfileItem(context, Icons.settings_outlined, 'Configurações', showSettingsModal, isDark),
            ],

            const SizedBox(height: 40),
            // Botão de Sair (Logout)
            AppButton(
              text: 'Sair da Conta',
              type: AppButtonType.outline,
              onPressed: () {
                context.read<AuthBloc>().add(LogoutRequested());
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildProfileItem(
      BuildContext context, IconData icon, String title, VoidCallback onTap, bool isDark) {
    final theme = Theme.of(context);
    return Padding(
      padding: const EdgeInsets.only(bottom: 16.0),
      child: AppCard(
        padding: EdgeInsets.zero,
        onTap: onTap,
        child: ListTile(
          leading: Icon(icon, color: AppColors.primary),
          title: Text(
            title,
            style: theme.textTheme.bodyLarge?.copyWith(
              fontWeight: FontWeight.w600,
              color: isDark ? Colors.white : Colors.black87,
            ),
          ),
          trailing: Icon(
            Icons.chevron_right_rounded,
            color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
          ),
        ),
      ),
    );
  }

  Widget _buildHistoryItem(String title, String value, String date, String ong, bool isDark) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: isDark ? AppColors.darkBackground : AppColors.background,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(title, style: const TextStyle(fontWeight: FontWeight.bold)),
              const SizedBox(height: 4),
              Text('$ong • $date', style: const TextStyle(color: Colors.grey, fontSize: 12)),
            ],
          ),
          Text(
            value,
            style: const TextStyle(
              fontWeight: FontWeight.bold,
              color: AppColors.primary,
              fontSize: 16,
            ),
          ),
        ],
      ),
    );
  }


}
