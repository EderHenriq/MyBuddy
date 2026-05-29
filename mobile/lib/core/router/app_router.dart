import 'package:go_router/go_router.dart';
import 'package:mybuddy_app/features/auth/presentation/pages/login_page.dart';

class AppRouter {
  static final router = GoRouter(
    initialLocation: '/login',
    routes: [
      GoRoute(
        path: '/login',
        builder: (context, state) => const LoginPage(),
      ),
    ],
  );
}
