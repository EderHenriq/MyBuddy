import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';

class CacheService {
  static const _petsKey = 'cached_pets';
  static const _userKey = 'cached_user';
  static const _cacheTimestampSuffix = '_timestamp';
  static const _cacheDuration = Duration(hours: 1);

  final SharedPreferences _prefs;

  CacheService(this._prefs);

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

  // ===================== USER =====================

  Future<void> saveUser(Map<String, dynamic> user) async {
    final json = jsonEncode(user);
    await _prefs.setString(_userKey, json);
    await _saveTimestamp(_userKey);
  }

  Map<String, dynamic>? getUser() {
    if (!_isCacheValid(_userKey)) return null;

    final json = _prefs.getString(_userKey);
    if (json == null) return null;

    return jsonDecode(json) as Map<String, dynamic>;
  }

  Future<void> clearUser() async {
    await _prefs.remove(_userKey);
    await _prefs.remove('$_userKey$_cacheTimestampSuffix');
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