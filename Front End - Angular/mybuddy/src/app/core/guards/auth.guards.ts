import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import Keycloak from 'keycloak-js';

export const authGuard: CanActivateFn = () => {
    const keycloak = inject(Keycloak);
    const router = inject(Router);

    if (keycloak.authenticated){
        return true;
    }

    keycloak.login({
        redirectUri: window.location.origin + '/home',
    });

    return false;
}