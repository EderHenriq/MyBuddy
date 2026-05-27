import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService);
    
    let token = authService.getToken();

    if (!token) {
        const matches = document.cookie.match(/(?:^|; )mybuddy_session=([^;]*)/);
        token = matches ? decodeURIComponent(matches[1]) : undefined;
    }

    if (token) {
        const authReq = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}` 
            }
        });
        return next(authReq);
    }
    return next(req);
}
