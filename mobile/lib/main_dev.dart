import 'package:flutter/material.dart';
import 'package:mybuddy_app/app.dart';
import 'package:mybuddy_app/core/constants/app_config.dart';
import 'package:mybuddy_app/core/di/injection_container.dart' as di;

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  AppConfig.initialize(flavorType: Flavor.dev);
  await di.init();
  runApp(const MyBuddyApp());
}
