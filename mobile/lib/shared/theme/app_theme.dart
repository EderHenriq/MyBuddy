import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';

class AppTheme {
  // Configuração base do TextTheme usando Google Fonts (Inter)
  static TextTheme _buildTextTheme(TextTheme base, Color textColor) {
    return GoogleFonts.interTextTheme(base).copyWith(
      displayLarge: GoogleFonts.dmSans(
        textStyle: base.displayLarge?.copyWith(
          color: textColor,
          fontWeight: FontWeight.bold,
        ),
      ),
      displayMedium: GoogleFonts.dmSans(
        textStyle: base.displayMedium?.copyWith(
          color: textColor,
          fontWeight: FontWeight.bold,
        ),
      ),
      displaySmall: GoogleFonts.dmSans(
        textStyle: base.displaySmall?.copyWith(
          color: textColor,
          fontWeight: FontWeight.bold,
        ),
      ),
      headlineLarge: GoogleFonts.dmSans(
        textStyle: base.headlineLarge?.copyWith(
          color: textColor,
          fontWeight: FontWeight.bold,
        ),
      ),
      headlineMedium: GoogleFonts.dmSans(
        textStyle: base.headlineMedium?.copyWith(
          color: textColor,
          fontWeight: FontWeight.w600,
        ),
      ),
      headlineSmall: GoogleFonts.dmSans(
        textStyle: base.headlineSmall?.copyWith(
          color: textColor,
          fontWeight: FontWeight.w600,
        ),
      ),
      titleLarge: GoogleFonts.dmSans(
        textStyle: base.titleLarge?.copyWith(
          color: textColor,
          fontWeight: FontWeight.w600,
        ),
      ),
      titleMedium: GoogleFonts.inter(
        textStyle: base.titleMedium?.copyWith(
          color: textColor,
          fontWeight: FontWeight.w500,
        ),
      ),
      titleSmall: GoogleFonts.inter(
        textStyle: base.titleSmall?.copyWith(
          color: textColor,
          fontWeight: FontWeight.w500,
        ),
      ),
      bodyLarge: GoogleFonts.inter(
        textStyle: base.bodyLarge?.copyWith(
          color: textColor,
          fontWeight: FontWeight.normal,
        ),
      ),
      bodyMedium: GoogleFonts.inter(
        textStyle: base.bodyMedium?.copyWith(
          color: textColor,
          fontWeight: FontWeight.normal,
        ),
      ),
      bodySmall: GoogleFonts.inter(
        textStyle: base.bodySmall?.copyWith(
          color: textColor,
          fontWeight: FontWeight.normal,
        ),
      ),
      labelLarge: GoogleFonts.inter(
        textStyle: base.labelLarge?.copyWith(
          color: textColor,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }

  // TEMA CLARO (LIGHT)
  static ThemeData get light {
    final base = ThemeData.light(useMaterial3: true);
    return base.copyWith(
      colorScheme: ColorScheme.fromSeed(
        seedColor: AppColors.primary,
        primary: AppColors.primary,
        secondary: AppColors.secondary,
        tertiary: AppColors.accent,
        surface: AppColors.surface,
        error: AppColors.error,
        brightness: Brightness.light,
      ),
      scaffoldBackgroundColor: AppColors.background,
      textTheme: _buildTextTheme(base.textTheme, AppColors.textPrimary),
      
      // AppBar
      appBarTheme: const AppBarTheme(
        backgroundColor: AppColors.surface,
        foregroundColor: AppColors.textPrimary,
        elevation: 0,
        centerTitle: true,
        titleTextStyle: TextStyle(
          color: AppColors.textPrimary,
          fontSize: 20,
          fontWeight: FontWeight.bold,
        ),
      ),

      // Card
      cardTheme: CardThemeData(
        color: AppColors.surface,
        elevation: 2,
        shadowColor: const Color(0x14000000), // const Color com 8% de opacidade
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12), // --radius-md do Angular
          side: const BorderSide(color: AppColors.border, width: 1),
        ),
      ),


      // Elevated Button
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: AppColors.primary,
          foregroundColor: Colors.white,
          elevation: 1,
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
          minimumSize: const Size(double.infinity, 52),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          textStyle: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.bold,
          ),
        ),
      ),

      // Outlined Button
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          foregroundColor: AppColors.primary,
          side: const BorderSide(color: AppColors.primary, width: 1.5),
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
          minimumSize: const Size(double.infinity, 52),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          textStyle: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.bold,
          ),
        ),
      ),

      // Text Button
      textButtonTheme: TextButtonThemeData(
        style: TextButton.styleFrom(
          foregroundColor: AppColors.primary,
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          textStyle: const TextStyle(
            fontSize: 15,
            fontWeight: FontWeight.w600,
          ),
        ),
      ),

      // InputDecoration (Inputs de Texto)
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: AppColors.surface,
        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 16),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: AppColors.border),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: AppColors.border),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: AppColors.primary, width: 2),
        ),
        errorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: AppColors.error, width: 1.5),
        ),
        focusedErrorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: AppColors.error, width: 2),
        ),
        labelStyle: const TextStyle(color: AppColors.textLight),
        hintStyle: const TextStyle(color: AppColors.textLight),
      ),

      // Bottom Navigation Bar
      bottomNavigationBarTheme: const BottomNavigationBarThemeData(
        backgroundColor: AppColors.surface,
        selectedItemColor: AppColors.primary,
        unselectedItemColor: AppColors.textLight,
        elevation: 8,
        type: BottomNavigationBarType.fixed,
        selectedLabelStyle: TextStyle(fontWeight: FontWeight.bold, fontSize: 12),
        unselectedLabelStyle: TextStyle(fontSize: 12),
      ),
    );
  }

  // TEMA ESCURO (DARK) - Para a tarefa MY-336
  static ThemeData get dark {
    final base = ThemeData.dark(useMaterial3: true);
    return base.copyWith(
      colorScheme: ColorScheme.fromSeed(
        seedColor: AppColors.primary,
        primary: AppColors.primary,
        secondary: AppColors.secondary,
        tertiary: AppColors.accent,
        surface: AppColors.darkSurface,
        error: AppColors.error,
        brightness: Brightness.dark,
      ),
      scaffoldBackgroundColor: AppColors.darkBackground,
      textTheme: _buildTextTheme(base.textTheme, AppColors.darkTextPrimary),
      
      // AppBar
      appBarTheme: const AppBarTheme(
        backgroundColor: AppColors.darkSurface,
        foregroundColor: AppColors.darkTextPrimary,
        elevation: 0,
        centerTitle: true,
        titleTextStyle: TextStyle(
          color: AppColors.darkTextPrimary,
          fontSize: 20,
          fontWeight: FontWeight.bold,
        ),
      ),

      // Card
      cardTheme: CardThemeData(
        color: AppColors.darkSurface,
        elevation: 2,
        shadowColor: const Color(0x4D000000), // const Color com 30% de opacidade
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12),
          side: const BorderSide(color: AppColors.darkBorder, width: 1),
        ),
      ),

      // Elevated Button
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: AppColors.primary,
          foregroundColor: Colors.white,
          elevation: 1,
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
          minimumSize: const Size(double.infinity, 52),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          textStyle: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.bold,
          ),
        ),
      ),

      // Outlined Button
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          foregroundColor: AppColors.primary,
          side: const BorderSide(color: AppColors.primary, width: 1.5),
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
          minimumSize: const Size(double.infinity, 52),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          textStyle: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.bold,
          ),
        ),
      ),

      // Text Button
      textButtonTheme: TextButtonThemeData(
        style: TextButton.styleFrom(
          foregroundColor: AppColors.primary,
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          textStyle: const TextStyle(
            fontSize: 15,
            fontWeight: FontWeight.w600,
          ),
        ),
      ),

      // InputDecoration (Inputs de Texto)
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: AppColors.darkSurface,
        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 16),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: AppColors.darkBorder),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: AppColors.darkBorder),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: AppColors.primary, width: 2),
        ),
        errorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: AppColors.error, width: 1.5),
        ),
        focusedErrorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: AppColors.error, width: 2),
        ),
        labelStyle: const TextStyle(color: AppColors.darkTextSecondary),
        hintStyle: const TextStyle(color: AppColors.darkTextSecondary),
      ),

      // Bottom Navigation Bar
      bottomNavigationBarTheme: const BottomNavigationBarThemeData(
        backgroundColor: AppColors.darkSurface,
        selectedItemColor: AppColors.primary,
        unselectedItemColor: AppColors.darkTextSecondary,
        elevation: 8,
        type: BottomNavigationBarType.fixed,
        selectedLabelStyle: TextStyle(fontWeight: FontWeight.bold, fontSize: 12),
        unselectedLabelStyle: TextStyle(fontSize: 12),
      ),
    );
  }
}

