import 'package:flutter/material.dart';
import 'package:mybuddy_app/app.dart';
import 'package:mybuddy_app/core/di/injection_container.dart' as di;

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await di.init();
  runApp(const MyBuddyApp());
}
