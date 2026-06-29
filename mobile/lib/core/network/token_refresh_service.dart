abstract class TokenRefreshService {
  ///tenta renovar o access token usando o refresh token
  ///rtorna true se o refresh foi bem-sucedido, false caso contrário
  Future<bool> refresh();


  Future<String?> getAccessToken();
}