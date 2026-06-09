import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mybuddy_app/app.dart';
import 'package:mybuddy_app/core/constants/app_config.dart';
import 'package:mybuddy_app/core/di/injection_container.dart' as di;

void main() {
  setUpAll(() async {
    // Inicializa a injeção de dependência e flavors para os testes
    AppConfig.initialize(flavorType: Flavor.dev);
    await di.init();
  });

  tearDownAll(() async {
    await di.sl.reset();
  });

  testWidgets('Smoke test do aplicativo MyBuddy', (WidgetTester tester) async {
    // Constrói o app e dispara o frame inicial.
    await tester.pumpWidget(const MyBuddyApp());

    // Verifica que a tela de login (ou a primeira tela) é carregada
    expect(find.byType(MyBuddyApp), findsOneWidget);

    // Aguarda o timer da SplashPage terminar para não deixar timers pendentes
    await tester.pump(const Duration(seconds: 3));
  });

  testWidgets('Verifica configuracao de temas claro e escuro no MaterialApp', (WidgetTester tester) async {
    await tester.pumpWidget(const MyBuddyApp());
    final MaterialApp materialApp = tester.widget(find.byType(MaterialApp));
    
    expect(materialApp.theme?.brightness, Brightness.light);
    expect(materialApp.darkTheme?.brightness, Brightness.dark);
    expect(materialApp.themeMode, ThemeMode.system);
    
    await tester.pump(const Duration(seconds: 3));
  });
}

