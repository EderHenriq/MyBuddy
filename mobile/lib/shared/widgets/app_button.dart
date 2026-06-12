import 'package:flutter/material.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';

enum AppButtonType { primary, secondary, outline, text }

class AppButton extends StatefulWidget {
  final String text;
  final VoidCallback? onPressed;
  final bool isLoading;
  final bool isDisabled;
  final IconData? icon;
  final AppButtonType type;
  final double? width;
  final double height;

  const AppButton({
    super.key,
    required this.text,
    this.onPressed,
    this.isLoading = false,
    this.isDisabled = false,
    this.icon,
    this.type = AppButtonType.primary,
    this.width = double.infinity,
    this.height = 52,
  });

  @override
  State<AppButton> createState() => _AppButtonState();
}

class _AppButtonState extends State<AppButton> {
  double _scale = 1.0;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    final isButtonDisabled = widget.isDisabled || widget.isLoading || widget.onPressed == null;

    // Seleção de cores e estilos dependendo do tipo
    Color getBackgroundColor() {
      if (isButtonDisabled) {
        return isDark ? Colors.white12 : Colors.grey.shade300;
      }
      switch (widget.type) {
        case AppButtonType.primary:
          return AppColors.primary;
        case AppButtonType.secondary:
          return AppColors.secondary;
        case AppButtonType.outline:
        case AppButtonType.text:
          return Colors.transparent;
      }
    }

    Color getForegroundColor() {
      if (isButtonDisabled) {
        return isDark ? Colors.white38 : Colors.grey.shade500;
      }
      switch (widget.type) {
        case AppButtonType.primary:
        case AppButtonType.secondary:
          return Colors.white;
        case AppButtonType.outline:
        case AppButtonType.text:
          return AppColors.primary;
      }
    }

    BorderSide? getBorder() {
      if (widget.type == AppButtonType.outline) {
        final borderColor = isButtonDisabled
            ? (isDark ? Colors.white12 : Colors.grey.shade300)
            : AppColors.primary;
        return BorderSide(color: borderColor, width: 1.5);
      }
      return null;
    }

    Widget content = Row(
      mainAxisAlignment: MainAxisAlignment.center,
      mainAxisSize: MainAxisSize.min,
      children: [
        if (widget.icon != null && !widget.isLoading) ...[
          Icon(widget.icon, size: 20, color: getForegroundColor()),
          const SizedBox(width: 8),
        ],
        if (widget.isLoading) ...[
          SizedBox(
            width: 20,
            height: 20,
            child: CircularProgressIndicator(
              strokeWidth: 2,
              valueColor: AlwaysStoppedAnimation<Color>(getForegroundColor()),
            ),
          ),
          const SizedBox(width: 12),
        ],
        Text(
          widget.text,
          style: theme.textTheme.labelLarge?.copyWith(
            color: getForegroundColor(),
            fontSize: 16,
            fontWeight: FontWeight.bold,
          ),
        ),
      ],
    );

    return GestureDetector(
      onTapDown: isButtonDisabled
          ? null
          : (_) => setState(() => _scale = 0.96),
      onTapUp: isButtonDisabled
          ? null
          : (_) => setState(() => _scale = 1.0),
      onTapCancel: isButtonDisabled
          ? null
          : () => setState(() => _scale = 1.0),
      child: AnimatedScale(
        scale: _scale,
        duration: const Duration(milliseconds: 100),
        curve: Curves.easeOut,
        child: SizedBox(
          width: widget.width,
          height: widget.height,
          child: widget.type == AppButtonType.text
              ? TextButton(
                  onPressed: isButtonDisabled ? null : widget.onPressed,
                  style: TextButton.styleFrom(
                    foregroundColor: getForegroundColor(),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                  child: content,
                )
              : OutlinedButton(
                  onPressed: isButtonDisabled ? null : widget.onPressed,
                  style: OutlinedButton.styleFrom(
                    backgroundColor: getBackgroundColor(),
                    foregroundColor: getForegroundColor(),
                    side: getBorder(),
                    elevation: (widget.type == AppButtonType.outline || isButtonDisabled) ? 0 : 1,
                    shadowColor: isDark ? Colors.black26 : Colors.black12,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ).copyWith(
                    // Sobrescrever estado hover/press do Material 3 se necessário
                    overlayColor: WidgetStateProperty.resolveWith<Color?>(
                      (states) {
                        if (states.contains(WidgetState.pressed)) {
                          return getForegroundColor().withAlpha(20);
                        }
                        if (states.contains(WidgetState.hovered)) {
                          return getForegroundColor().withAlpha(10);
                        }
                        return null;
                      },
                    ),
                  ),
                  child: content,
                ),
        ),
      ),
    );
  }
}
