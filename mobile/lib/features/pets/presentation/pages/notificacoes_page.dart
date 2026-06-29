import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:mybuddy_app/shared/widgets/app_card.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';

class NotificationModel {
  final String id;
  final String title;
  final String description;
  final String date;
  final IconData icon;
  final Color iconColor;
  bool isRead;

  NotificationModel({
    required this.id,
    required this.title,
    required this.description,
    required this.date,
    required this.icon,
    required this.iconColor,
    this.isRead = false,
  });
}

class NotificacoesPage extends StatefulWidget {
  const NotificacoesPage({super.key});

  @override
  State<NotificacoesPage> createState() => _NotificacoesPageState();
}

class _NotificacoesPageState extends State<NotificacoesPage> {
  final List<NotificationModel> _notifications = [
    NotificationModel(
      id: '1',
      title: 'Adoção Aprovada!',
      description: 'Parabéns! Sua solicitação de adoção da gatinha Mia foi pré-aprovada pela ONG Amigo Fiel. Verifique seu e-mail.',
      date: 'Hoje, 10:30',
      icon: Icons.check_circle_rounded,
      iconColor: AppColors.success,
      isRead: false,
    ),
    NotificationModel(
      id: '2',
      title: 'Nova Campanha de Doações',
      description: 'A ONG SOS Patinhas iniciou uma campanha emergencial para compra de ração. Ajude se puder!',
      date: 'Hoje, 08:15',
      icon: Icons.volunteer_activism_rounded,
      iconColor: AppColors.primary,
      isRead: false,
    ),
    NotificationModel(
      id: '3',
      title: 'Nova Mensagem Recebida',
      description: 'Você recebeu uma resposta da ONG Amigo Fiel sobre a entrevista do cão Pipoca.',
      date: 'Ontem, 16:45',
      icon: Icons.chat_bubble_rounded,
      iconColor: AppColors.info,
      isRead: true,
    ),
    NotificationModel(
      id: '4',
      title: 'Dica MyBuddy: Cuidados no Inverno',
      description: 'Veja como aquecer e proteger seu pet nas noites frias. Confira as novidades do nosso blog!',
      date: '04 de Junho',
      icon: Icons.lightbulb_outline_rounded,
      iconColor: AppColors.warning,
      isRead: true,
    ),
  ];

  void _markAllAsRead() {
    setState(() {
      for (var n in _notifications) {
        n.isRead = true;
      }
    });
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Todas as notificações foram marcadas como lidas.')),
    );
  }

  void _clearAll() {
    setState(() {
      _notifications.clear();
    });
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Histórico de notificações limpo.')),
    );
  }

  void _deleteNotification(String id) {
    setState(() {
      _notifications.removeWhere((n) => n.id == id);
    });
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Notificações'),
        leading: IconButton(
          icon: Icon(
            Icons.arrow_back_ios_new_rounded,
            color: isDark ? Colors.white : Colors.black87,
          ),
          onPressed: () => context.go('/pets'),
        ),
        actions: _notifications.isNotEmpty
            ? [
                PopupMenuButton<String>(
                  onSelected: (value) {
                    if (value == 'read') {
                      _markAllAsRead();
                    } else if (value == 'clear') {
                      _clearAll();
                    }
                  },
                  itemBuilder: (BuildContext context) => [
                    const PopupMenuItem<String>(
                      value: 'read',
                      child: Row(
                        children: [
                          Icon(Icons.mark_email_read_outlined, size: 20),
                          SizedBox(width: 8),
                          Text('Marcar lidas'),
                        ],
                      ),
                    ),
                    const PopupMenuItem<String>(
                      value: 'clear',
                      child: Row(
                        children: [
                          Icon(Icons.delete_sweep_outlined, size: 20, color: Colors.red),
                          SizedBox(width: 8),
                          Text('Limpar tudo', style: TextStyle(color: Colors.red)),
                        ],
                      ),
                    ),
                  ],
                ),
              ]
            : null,
      ),
      body: _notifications.isEmpty
          ? Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Container(
                    padding: const EdgeInsets.all(24),
                    decoration: BoxDecoration(
                      color: AppColors.primary.withAlpha(20),
                      shape: BoxShape.circle,
                    ),
                    child: const Icon(
                      Icons.notifications_off_outlined,
                      size: 64,
                      color: AppColors.primary,
                    ),
                  ),
                  const SizedBox(height: 24),
                  Text(
                    'Nenhuma notificação',
                    style: theme.textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Enviaremos novidades assim que elas surgirem.',
                    style: theme.textTheme.bodyMedium?.copyWith(
                      color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                    ),
                  ),
                ],
              ),
            )
          : ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: _notifications.length,
              itemBuilder: (context, index) {
                final notification = _notifications[index];

                return Dismissible(
                  key: Key(notification.id),
                  direction: DismissDirection.endToStart,
                  background: Container(
                    alignment: Alignment.centerRight,
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    decoration: BoxDecoration(
                      color: Colors.red.shade600,
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: const Icon(Icons.delete_outline_rounded, color: Colors.white, size: 28),
                  ),
                  onDismissed: (direction) {
                    _deleteNotification(notification.id);
                  },
                  child: Padding(
                    padding: const EdgeInsets.only(bottom: 12.0),
                    child: AppCard(
                      padding: const EdgeInsets.all(16),
                      onTap: () {
                        setState(() {
                          notification.isRead = true;
                        });
                      },
                      border: notification.isRead
                          ? null
                          : BorderSide(
                              color: AppColors.primary.withAlpha(100),
                              width: 1.5,
                            ),
                      child: Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          // Icon Indicator
                          CircleAvatar(
                            radius: 22,
                            backgroundColor: notification.iconColor.withAlpha(20),
                            child: Icon(notification.icon, color: notification.iconColor, size: 22),
                          ),
                          const SizedBox(width: 16),
                          
                          // Notification Body
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                  children: [
                                    Expanded(
                                      child: Text(
                                        notification.title,
                                        style: theme.textTheme.bodyLarge?.copyWith(
                                          fontWeight: notification.isRead ? FontWeight.w600 : FontWeight.bold,
                                          color: isDark ? Colors.white : Colors.black87,
                                        ),
                                      ),
                                    ),
                                    if (!notification.isRead)
                                      Container(
                                        width: 8,
                                        height: 8,
                                        decoration: const BoxDecoration(
                                          color: AppColors.primary,
                                          shape: BoxShape.circle,
                                        ),
                                      ),
                                  ],
                                ),
                                const SizedBox(height: 4),
                                Text(
                                  notification.description,
                                  style: theme.textTheme.bodyMedium?.copyWith(
                                    color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                                    height: 1.4,
                                  ),
                                ),
                                const SizedBox(height: 8),
                                Text(
                                  notification.date,
                                  style: theme.textTheme.bodySmall?.copyWith(
                                    color: Colors.grey,
                                    fontSize: 11,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                );
              },
            ),
    );
  }
}
