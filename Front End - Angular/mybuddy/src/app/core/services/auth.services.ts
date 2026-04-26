import {inject, Injectable} from '@angular/core';
import Keycloak from 'keycloak-js';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private keycloak = inject(Keycloak);

    isLoggedIn(): boolean {
        return !!this.keycloak.authenticated;
    }

    getUserRoles(): string[] {
        return this.keycloak.realmAccess?.roles ?? [];
    }

    getToken():  string | undefined {
        return this.keycloak.token;
    }
}
