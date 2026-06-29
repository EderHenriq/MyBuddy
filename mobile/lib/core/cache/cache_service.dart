import 'dart:convert';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:shared_preferences/shared_preferences.dart';

class CacheService {
  static const _petsKey = 'cached_pets';
  static const _userKey = 'cached_user';
  static const _cacheTimestampSuffix = '_timestamp';
  static const _cacheDuration = Duration(hours: 1);

  final SharedPreferences _prefs;
  final FlutterSecureStorage _secureStorage;

  CacheService(this._prefs, this._secureStorage);

  // ===================== PETS =====================

  Future<void> savePets(List<Map<String, dynamic>> pets) async {
    final json = jsonEncode(pets);
    await _prefs.setString(_petsKey, json);
    await _saveTimestamp(_petsKey);
  }

  List<Map<String, dynamic>>? getPets() {
    if (!_isCacheValid(_petsKey)) return null;

    final json = _prefs.getString(_petsKey);
    if (json == null) return null;

    final decoded = jsonDecode(json) as List;
    return decoded.cast<Map<String, dynamic>>();
  }

  Future<void> clearPets() async {
    await _prefs.remove(_petsKey);
    await _prefs.remove('$_petsKey$_cacheTimestampSuffix');
  }

  // ===================== USER (armazenamento seguro) =====================

  Future<void> saveUser(Map<String, dynamic> user) async {
    final json = jsonEncode(user);
    await _secureStorage.write(key: _userKey, value: json);
    final now = DateTime.now().millisecondsSinceEpoch.toString();
    await _secureStorage.write(key: '$_userKey$_cacheTimestampSuffix', value: now);
  }

  Future<Map<String, dynamic>?> getUser() async {
    final timestampStr = await _secureStorage.read(key: '$_userKey$_cacheTimestampSuffix');
    if (timestampStr == null) return null;

    final timestamp = int.tryParse(timestampStr);
    if (timestamp == null) return null;

    final cacheTime = DateTime.fromMillisecondsSinceEpoch(timestamp);
    if (DateTime.now().difference(cacheTime) >= _cacheDuration) return null;

    final json = await _secureStorage.read(key: _userKey);
    if (json == null) return null;

    return jsonDecode(json) as Map<String, dynamic>;
  }

  Future<void> clearUser() async {
    await _secureStorage.delete(key: _userKey);
    await _secureStorage.delete(key: '$_userKey$_cacheTimestampSuffix');
  }

  // ===================== GERAL =====================

  Future<void> setString(String key, String value) async {
    await _prefs.setString(key, value);
  }

  String? getString(String key) {
    return _prefs.getString(key);
  }

  Future<void> setStringList(String key, List<String> value) async {
    await _prefs.setStringList(key, value);
  }

  List<String>? getStringList(String key) {
    return _prefs.getStringList(key);
  }

  Future<void> clearAll() async {
    await _prefs.clear();
    await _secureStorage.deleteAll();
  }

  // ===================== HELPERS =====================

  Future<void> _saveTimestamp(String key) async {
    final now = DateTime.now().millisecondsSinceEpoch;
    await _prefs.setInt('$key$_cacheTimestampSuffix', now);
  }

  bool _isCacheValid(String key) {
    final timestamp = _prefs.getInt('$key$_cacheTimestampSuffix');
    if (timestamp == null) return false;

    final cacheTime = DateTime.fromMillisecondsSinceEpoch(timestamp);
    final now = DateTime.now();
    return now.difference(cacheTime) < _cacheDuration;
  }
}
