import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mybuddy_app/core/cache/cache_service.dart';

class ThemeCubit extends Cubit<ThemeMode> {
  final CacheService _cacheService;
  static const _themeKey = 'theme_preference';

  ThemeCubit(this._cacheService) : super(ThemeMode.system) {
    _loadTheme();
  }

  void _loadTheme() {
    final cached = _cacheService.getString(_themeKey);
    if (cached != null) {
      final mode = ThemeMode.values.firstWhere(
        (e) => e.name == cached,
        orElse: () => ThemeMode.system,
      );
      emit(mode);
    }
  }

  void updateTheme(ThemeMode mode) {
    _cacheService.setString(_themeKey, mode.name);
    emit(mode);
  }
}
