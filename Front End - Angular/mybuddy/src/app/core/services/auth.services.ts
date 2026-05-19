import { inject, Injectable, signal } from '@angular/core';
import Keycloak from 'keycloak-js';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private keycloak = inject(Keycloak);
    private http = inject(HttpClient);
    private router = inject(Router);

    private inMemoryToken: string | null = null;
    
    // Sinal reativo contendo as informações do usuário atual
    currentUser = signal<any | null>(null);

    constructor() {
        this.restoreSession();
    }

    private restoreSession() {
        const token = this.getCookie('mybuddy_session');
        if (token) {
            this.inMemoryToken = token;
            this.getProfile().subscribe({
                next: (profile) => {
                    this.currentUser.set(profile);
                },
                error: () => {
                    this.logout();
                }
            });
        }
    }

    isLoggedIn(): boolean {
        return !!this.inMemoryToken || !!this.keycloak.authenticated;
    }

    getUserRoles(): string[] {
        if (this.currentUser()) {
            const roles = this.currentUser().roles ?? [];
            return roles.map((r: any) => typeof r === 'string' ? r : r.name);
        }
        return this.keycloak.realmAccess?.roles ?? [];
    }

    getToken(): string | undefined {
        return this.inMemoryToken || this.keycloak.token;
    }

    loginWithCredentials(email: string, password: string): Observable<any> {
        const tokenUrl = `${environment.keycloak.url}/realms/${environment.keycloak.realm}/protocol/openid-connect/token`;
        
        const payload = new HttpParams()
            .set('client_id', environment.keycloak.clientId)
            .set('grant_type', 'password')
            .set('username', email)
            .set('password', password)
            .set('scope', 'openid');

        const headers = new HttpHeaders({
            'Content-Type': 'application/x-www-form-urlencoded'
        });

        return this.http.post<any>(tokenUrl, payload.toString(), { headers }).pipe(
            tap(response => {
                const token = response.access_token;
                this.inMemoryToken = token;
                
                // Salva em um cookie seguro (SameSite=Strict; max-age = 1 hora)
                this.setCookie('mybuddy_session', token, 3600);
            }),
            catchError(err => {
                console.error('Erro na autenticação:', err);
                return throwError(() => err);
            })
        );
    }

    register(payload: any): Observable<any> {
        const cadastroUrl = `${environment.apiUrl}auth/cadastro`;
        return this.http.post<any>(cadastroUrl, payload);
    }

    getProfile(): Observable<any> {
        const profileUrl = `${environment.apiUrl}usuarios/meu-perfil`;
        return this.http.get<any>(profileUrl).pipe(
            tap(profile => {
                this.currentUser.set(profile);
            })
        );
    }

    logout() {
        this.inMemoryToken = null;
        this.deleteCookie('mybuddy_session');
        this.currentUser.set(null);
        
        if (this.keycloak.authenticated) {
            this.keycloak.logout({
                redirectUri: window.location.origin
            });
        } else {
            this.router.navigate(['/']);
        }
    }

    // --- Helpers de Cookies Seguros (Proteção XSS e CSRF) ---
    private setCookie(name: string, value: string, maxAgeSeconds: number) {
        const secureFlag = window.location.protocol === 'https:' ? '; Secure' : '';
        document.cookie = `${name}=${encodeURIComponent(value)}; path=/; max-age=${maxAgeSeconds}; SameSite=Strict${secureFlag}`;
    }

    private getCookie(name: string): string | null {
        const matches = document.cookie.match(new RegExp(
            "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
        ));
        return matches ? decodeURIComponent(matches[1]) : null;
    }

    private deleteCookie(name: string) {
        document.cookie = `${name}=; path=/; max-age=0; SameSite=Strict`;
    }
}
