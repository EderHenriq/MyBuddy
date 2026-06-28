import { HttpInterceptorFn, HttpErrorResponse } from "@angular/common/http";
import { retry, timer } from "rxjs";

export const retryInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    retry({
      count: 2,
      delay: (error: HttpErrorResponse, retryCount: number) => {
        if (error.status === 0 || error.status >= 500) {
          return timer(1000 * retryCount);
        }
        throw error;
      },
    }),
  );
};
