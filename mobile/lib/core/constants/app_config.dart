enum Flavor { dev, prod }

class AppConfig {
  static late final Flavor flavor;
  static late final String apiBaseUrl;
  static late final String keycloakUrl;
  static late final bool showLogs;

  static void initialize({required Flavor flavorType}) {
    flavor = flavorType;

    switch (flavor) {
      case Flavor.dev:
        apiBaseUrl = 'http://localhost/api/';
        keycloakUrl = 'http://localhost:8080';
        showLogs = true;
      case Flavor.prod:
        apiBaseUrl = 'https://api.mybuddy.com.br/api/';
        keycloakUrl = 'https://auth.mybuddy.com.br';
        showLogs = false;
    }
  }

  static bool get isDev => flavor == Flavor.dev;
  static bool get isProd => flavor == Flavor.prod;
}
