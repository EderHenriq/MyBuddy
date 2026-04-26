import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import {
    includeBearerTokenInterceptor,
    INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG
} from 'keycloak-angular';

export const authInterceptor: HttpInterceptorFn = includeBearerTokenInterceptor;