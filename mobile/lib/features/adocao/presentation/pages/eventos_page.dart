import 'package:flutter/material.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';
import 'package:mybuddy_app/shared/widgets/app_card.dart';
import 'package:mybuddy_app/shared/widgets/app_button.dart';

class Evento {
  final int id;
  final String imageUrl;
  final String badgeText;
  final String title;
  final String dateStr;
  final String timeStr;
  final String locationStr;
  final String organizerStr;
  final String description;

  const Evento({
    required this.id,
    required this.imageUrl,
    required this.badgeText,
    required this.title,
    required this.dateStr,
    required this.timeStr,
    required this.locationStr,
    required this.organizerStr,
    required this.description,
  });
}

class EventosPage extends StatefulWidget {
  const EventosPage({super.key});

  @override
  State<EventosPage> createState() => _EventosPageState();
}

class _EventosPageState extends State<EventosPage> {
  final List<Evento> _eventos = const [
    Evento(
      id: 1,
      imageUrl: 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=800',
      badgeText: 'PRÓXIMO',
      title: "Feira de Adoção 'Amor de Pet'",
      dateStr: 'Sábado, 25 de Maio',
      timeStr: '10:00 - 17:00',
      locationStr: 'Parque do Ibirapuera, SP',
      organizerStr: 'ONG Patas Unidas',
      description: 'Vários cachorros e gatos prontos para um novo lar. Vacinação no local e orientação veterinária gratuita.',
    ),
    Evento(
      id: 2,
      imageUrl: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&q=80&w=800',
      badgeText: 'EM DESTAQUE',
      title: 'Gatoteca & Adoção de Felinos',
      dateStr: 'Domingo, 26 de Maio',
      timeStr: '11:00 - 16:00',
      locationStr: 'Centro de Eventos, Curitiba',
      organizerStr: 'ONG Felinos Felizes',
      description: 'Venha conhecer felinos especiais procurando um lar amoroso. Lojinha de produtos artesanais para gatos no local.',
    ),
    Evento(
      id: 3,
      imageUrl: 'https://images.unsplash.com/photo-1537151608804-ea6f23b7b6c5?auto=format&fit=crop&q=80&w=800',
      badgeText: 'NOVO',
      title: 'Cãominhada e Feira de Adoção',
      dateStr: 'Próximo Fim de Semana',
      timeStr: '09:00 - 18:00',
      locationStr: 'Lagoa do Taquaral, Campinas',
      organizerStr: 'Coletivo Animal',
      description: 'Caminhe com seu buddy e adote um novo amigo na nossa feira ao ar livre. Brindes especiais para os primeiros participantes.',
    ),
  ];

  final Set<int> _confirmados = {};

  void _confirmarPresenca(Evento evento) {
    setState(() {
      if (_confirmados.contains(evento.id)) {
        _confirmados.remove(evento.id);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Presença cancelada no evento "${evento.title}"'),
            backgroundColor: Colors.grey.shade800,
          ),
        );
      } else {
        _confirmados.add(evento.id);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Presença confirmada no evento "${evento.title}"!'),
            backgroundColor: AppColors.success,
          ),
        );
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Scaffold(
      body: SafeArea(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Header
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 20.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Eventos de Doação',
                    style: theme.textTheme.headlineSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: isDark ? Colors.white : Colors.black87,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    'Encontre feiras, cãominhadas e eventos solidários perto de você.',
                    style: theme.textTheme.bodyMedium?.copyWith(
                      color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                    ),
                  ),
                ],
              ),
            ),

            // Events List
            Expanded(
              child: ListView.builder(
                padding: const EdgeInsets.only(left: 24.0, right: 24.0, bottom: 24.0),
                itemCount: _eventos.length,
                itemBuilder: (context, index) {
                  final evento = _eventos[index];
                  final isConfirmado = _confirmados.contains(evento.id);

                  return Container(
                    margin: const EdgeInsets.only(bottom: 24.0),
                    child: AppCard(
                      padding: EdgeInsets.zero,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.stretch,
                        children: [
                          // Image with Badge
                          Stack(
                            children: [
                              ClipRRect(
                                borderRadius: const BorderRadius.vertical(top: Radius.circular(12)),
                                child: Image.network(
                                  evento.imageUrl,
                                  height: 180,
                                  width: double.infinity,
                                  fit: BoxFit.cover,
                                  errorBuilder: (context, error, stackTrace) => Container(
                                    height: 180,
                                    color: isDark ? Colors.grey.shade800 : Colors.grey.shade200,
                                    child: const Icon(Icons.broken_image_outlined, size: 50),
                                  ),
                                ),
                              ),
                              if (evento.badgeText.isNotEmpty)
                                Positioned(
                                  top: 12,
                                  left: 12,
                                  child: Container(
                                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                                    decoration: BoxDecoration(
                                      color: AppColors.primary,
                                      borderRadius: BorderRadius.circular(20),
                                    ),
                                    child: Text(
                                      evento.badgeText,
                                      style: const TextStyle(
                                        color: Colors.white,
                                        fontSize: 10,
                                        fontWeight: FontWeight.bold,
                                      ),
                                    ),
                                  ),
                                ),
                            ],
                          ),

                          // Event Details
                          Padding(
                            padding: const EdgeInsets.all(16.0),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  evento.title,
                                  style: theme.textTheme.titleMedium?.copyWith(
                                    fontWeight: FontWeight.bold,
                                    color: isDark ? Colors.white : Colors.black87,
                                  ),
                                ),
                                const SizedBox(height: 12),

                                // Date, time, location information
                                _buildIconInfo(Icons.calendar_month_outlined, evento.dateStr, theme, isDark),
                                const SizedBox(height: 6),
                                _buildIconInfo(Icons.access_time_rounded, evento.timeStr, theme, isDark),
                                const SizedBox(height: 6),
                                _buildIconInfo(Icons.location_on_outlined, evento.locationStr, theme, isDark),
                                const SizedBox(height: 6),
                                _buildIconInfo(Icons.business_rounded, 'Organizado por: ${evento.organizerStr}', theme, isDark),
                                
                                const Padding(
                                  padding: EdgeInsets.symmetric(vertical: 12.0),
                                  child: Divider(),
                                ),

                                Text(
                                  evento.description,
                                  style: theme.textTheme.bodyMedium?.copyWith(
                                    color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
                                    height: 1.4,
                                  ),
                                ),
                                const SizedBox(height: 16),

                                // Action Buttons
                                Row(
                                  children: [
                                    Expanded(
                                      child: AppButton(
                                        text: isConfirmado ? 'Presença Confirmada!' : 'Confirmar Presença',
                                        type: isConfirmado ? AppButtonType.outline : AppButtonType.primary,
                                        onPressed: () => _confirmarPresenca(evento),
                                      ),
                                    ),
                                    const SizedBox(width: 12),
                                    Material(
                                      color: isDark ? AppColors.darkSurface : Colors.grey.shade100,
                                      shape: RoundedRectangleBorder(
                                        borderRadius: BorderRadius.circular(12),
                                        side: BorderSide(
                                          color: isDark ? AppColors.darkBorder : AppColors.border,
                                          width: 1,
                                        ),
                                      ),
                                      child: IconButton(
                                        icon: const Icon(Icons.map_outlined, color: AppColors.primary),
                                        onPressed: () {
                                          ScaffoldMessenger.of(context).showSnackBar(
                                            SnackBar(
                                              content: Text('Abrindo localização de "${evento.title}" no mapa...'),
                                            ),
                                          );
                                        },
                                        constraints: const BoxConstraints(),
                                        padding: const EdgeInsets.all(12),
                                      ),
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildIconInfo(IconData icon, String text, ThemeData theme, bool isDark) {
    return Row(
      children: [
        Icon(icon, size: 16, color: AppColors.primary),
        const SizedBox(width: 8),
        Expanded(
          child: Text(
            text,
            style: theme.textTheme.bodySmall?.copyWith(
              color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
              fontSize: 12,
            ),
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
          ),
        ),
      ],
    );
  }
}
