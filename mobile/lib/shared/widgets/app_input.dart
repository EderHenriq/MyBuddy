import 'package:flutter/material.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';

class AppInput extends StatefulWidget {
  final TextEditingController? controller;
  final String labelText;
  final String? hintText;
  final bool isPassword;
  final TextInputType keyboardType;
  final TextInputAction textInputAction;
  final IconData? prefixIcon;
  final Widget? suffixIcon;
  final String? Function(String?)? validator;
  final ValueChanged<String>? onChanged;
  final ValueChanged<String>? onFieldSubmitted;
  final bool autofocus;
  final bool enabled;
  final int maxLines;

  const AppInput({
    super.key,
    this.controller,
    required this.labelText,
    this.hintText,
    this.isPassword = false,
    this.keyboardType = TextInputType.text,
    this.textInputAction = TextInputAction.next,
    this.prefixIcon,
    this.suffixIcon,
    this.validator,
    this.onChanged,
    this.onFieldSubmitted,
    this.autofocus = false,
    this.enabled = true,
    this.maxLines = 1,
  });

  @override
  State<AppInput> createState() => _AppInputState();
}

class _AppInputState extends State<AppInput> {
  bool _obscureText = true;

  @override
  void initState() {
    super.initState();
    _obscureText = widget.isPassword;
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    Widget? getSuffixIcon() {
      if (widget.isPassword) {
        return IconButton(
          icon: Icon(
            _obscureText ? Icons.visibility_off_outlined : Icons.visibility_outlined,
            color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
          ),
          onPressed: () {
            setState(() {
              _obscureText = !_obscureText;
            });
          },
        );
      }
      return widget.suffixIcon;
    }

    return TextFormField(
      controller: widget.controller,
      obscureText: _obscureText,
      keyboardType: widget.keyboardType,
      textInputAction: widget.textInputAction,
      validator: widget.validator,
      onChanged: widget.onChanged,
      onFieldSubmitted: widget.onFieldSubmitted,
      autofocus: widget.autofocus,
      enabled: widget.enabled,
      maxLines: widget.isPassword ? 1 : widget.maxLines,
      style: theme.textTheme.bodyMedium?.copyWith(
        color: isDark ? AppColors.darkTextPrimary : AppColors.textPrimary,
      ),
      decoration: InputDecoration(
        labelText: widget.labelText,
        hintText: widget.hintText,
        prefixIcon: widget.prefixIcon != null
            ? Icon(
                widget.prefixIcon,
                color: isDark ? AppColors.darkTextSecondary : AppColors.textLight,
              )
            : null,
        suffixIcon: getSuffixIcon(),
      ),
    );
  }
}
