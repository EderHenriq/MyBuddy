import 'package:flutter/material.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';

class AppCard extends StatefulWidget {
  final Widget child;
  final VoidCallback? onTap;
  final Color? color;
  final double? elevation;
  final double borderRadius;
  final EdgeInsetsGeometry padding;
  final EdgeInsetsGeometry? margin;
  final BorderSide? border;

  const AppCard({
    super.key,
    required this.child,
    this.onTap,
    this.color,
    this.elevation,
    this.borderRadius = 12.0, // --radius-md do Angular
    this.padding = const EdgeInsets.all(16.0),
    this.margin,
    this.border,
  });

  @override
  State<AppCard> createState() => _AppCardState();
}

class _AppCardState extends State<AppCard> {
  double _scale = 1.0;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    final cardColor = widget.color ?? (isDark ? AppColors.darkSurface : AppColors.surface);
    final shadowColor = isDark ? const Color(0x4D000000) : const Color(0x14000000);
    final secondaryShadowColor = isDark ? const Color(0x2E000000) : const Color(0x0C000000);
    final defaultBorder = widget.border ?? BorderSide(
      color: isDark ? AppColors.darkBorder : AppColors.border,
      width: 1,
    );

    Widget cardContent = Container(
      padding: widget.padding,
      decoration: BoxDecoration(
        color: cardColor,
        borderRadius: BorderRadius.circular(widget.borderRadius),
        border: defaultBorder != BorderSide.none ? Border(
          top: defaultBorder,
          bottom: defaultBorder,
          left: defaultBorder,
          right: defaultBorder,
        ) : null,
      ),
      child: widget.child,
    );

    if (widget.onTap != null) {
      cardContent = InkWell(
        onTap: widget.onTap,
        borderRadius: BorderRadius.circular(widget.borderRadius),
        child: cardContent,
      );
    }

    Widget result = Container(
      margin: widget.margin,
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(widget.borderRadius),
        boxShadow: [
          BoxShadow(
            color: shadowColor,
            blurRadius: 6,
            spreadRadius: -1,
            offset: const Offset(0, 4), // --shadow-md do Angular
          ),
          BoxShadow(
            color: secondaryShadowColor,
            blurRadius: 4,
            spreadRadius: -1,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Material(
        color: Colors.transparent,
        child: cardContent,
      ),
    );

    if (widget.onTap != null) {
      return GestureDetector(
        onTapDown: (_) => setState(() => _scale = 0.98),
        onTapUp: (_) => setState(() => _scale = 1.0),
        onTapCancel: () => setState(() => _scale = 1.0),
        child: AnimatedScale(
          scale: _scale,
          duration: const Duration(milliseconds: 100),
          curve: Curves.easeOut,
          child: result,
        ),
      );
    }

    return result;
  }
}
