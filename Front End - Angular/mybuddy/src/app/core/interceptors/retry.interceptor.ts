import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { retry, timer } from 'rxjs';

export const retryInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    retry({
      count: 2,
      delay: (error: HttpErrorResponse, retryCount: number) => {
        // Retenta requisição apenas em falhas de rede (0) ou erros de servidor (5xx)
        if (error.status === 0 || error.status >= 500) {
          return timer(1000 * retryCount); // Delay crescente: 1s, 2s
        }
        throw error; // Propaga os erros 4xx imediatamente
      }
    })
  );
};
