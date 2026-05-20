import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { tap } from 'rxjs/operators';

// Armazena as requisições GET em memória
const cache = new Map<string, HttpResponse<unknown>>();

// Endpoints que contêm dados dinâmicos e que NUNCA devem ser cacheados
const EXCLUDED_CACHE_URLS = ['/usuarios/meu-perfil', '/users', '/pets', '/protocol/openid-connect', '/auth/'];

export const cacheInterceptor: HttpInterceptorFn = (req, next) => {
  // Se for uma requisição de modificação, limpa o cache preventivamente para evitar dados obsoletos
  if (req.method !== 'GET') {
    cache.clear();
    return next(req);
  }

  // Ignora o cache se a requisição for para um endpoint excluído
  const isExcluded = EXCLUDED_CACHE_URLS.some(url => req.url.includes(url));
  if (isExcluded) {
    return next(req);
  }

  const cachedResponse = cache.get(req.urlWithParams);
  if (cachedResponse) {
    return of(cachedResponse.clone());
  }

  return next(req).pipe(
    tap(event => {
      if (event instanceof HttpResponse) {
        cache.set(req.urlWithParams, event.clone());
        // Invalida o item cacheado após 30 segundos (tempo seguro de TTL)
        setTimeout(() => cache.delete(req.urlWithParams), 30 * 1000);
      }
    }),
  );
};
