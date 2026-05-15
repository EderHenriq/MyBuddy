import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        console.error('[Error Interceptor] Não autorizado (401). Redirecionando para login...');
      } else if (error.status === 403) {
        console.error('[Error Interceptor] Acesso negado (403).');
      } else if (error.status >= 500) {
        console.error('[Error Interceptor] Erro no servidor (500+).', error.message);
      }
      return throwError(() => error);
    })
  );
};
