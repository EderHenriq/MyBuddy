import { HttpInterceptorFn, HttpResponse } from "@angular/common/http";
import { of } from "rxjs";
import { tap } from "rxjs/operators";

const cache = new Map<string, HttpResponse<unknown>>();

const EXCLUDED_CACHE_URLS = [
  "/usuarios/meu-perfil",
  "/users",
  "/pets",
  "/protocol/openid-connect",
  "/auth/",
];

export const cacheInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.method !== "GET") {
    cache.clear();
    return next(req);
  }

  const isExcluded = EXCLUDED_CACHE_URLS.some((url) => req.url.includes(url));
  if (isExcluded) {
    return next(req);
  }

  const cachedResponse = cache.get(req.urlWithParams);
  if (cachedResponse) {
    return of(cachedResponse.clone());
  }

  return next(req).pipe(
    tap((event) => {
      if (event instanceof HttpResponse) {
        cache.set(req.urlWithParams, event.clone());

        setTimeout(() => cache.delete(req.urlWithParams), 30 * 1000);
      }
    }),
  );
};
