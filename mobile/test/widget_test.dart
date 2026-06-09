import 'package:flutter_test/flutter_test.dart';
import 'package:mybuddy_app/app.dart';
import 'package:mybuddy_app/core/constants/app_config.dart';
import 'package:mybuddy_app/core/di/injection_container.dart' as di;

void main() {
  setUp(() async {
    // Inicializa a injeção de dependência e flavors para os testes
    AppConfig.initialize(flavorType: Flavor.dev);
    await di.init();
  });

  tearDown(() async {
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
}

