/* eslint-disable @typescript-eslint/no-explicit-any, no-useless-escape */
import { inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import Keycloak from 'keycloak-js';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, tap, catchError, throwError, of, delay } from 'rxjs';
import { Router } from '@angular/router';
import { SessionService } from './session.service';
import { Role } from '../models/role.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private platformId = inject(PLATFORM_ID);
  private keycloak = inject(Keycloak, { optional: true });
  private http = inject(HttpClient);
  private router = inject(Router);
  private sessionService = inject(SessionService);

  private inMemoryToken: string | null = null;

  // Sinal reativo contendo as informações do usuário atual
  currentUser = signal<any | null>(null);

  constructor() {
    if (isPlatformBrowser(this.platformId)) {
      this.restoreSession();
    }
  }

  private restoreSession() {
    const token = this.getCookie('mybuddy_session');
    if (token) {
      this.inMemoryToken = token;

      // Checa se é o token de mock para evitar requisição real
      if (token.startsWith('mock-jwt-token-')) {
        const storedRole = this.sessionService.getCurrentRole();
        if (storedRole) {
          const mockProfile = { id: 99, nome: 'Usuário Teste (' + storedRole + ')', email: 'teste@mybuddy.com', roles: [storedRole] };
          this.currentUser.set(mockProfile);
          return;
        }
      }

      this.getProfile().subscribe({
        next: profile => {
          this.currentUser.set(profile);
        },
        error: () => {
          this.logout();
        },
      });
    }
  }

  isLoggedIn(): boolean {
    return !!this.inMemoryToken || !!this.keycloak?.authenticated;
  }

  getUserRoles(): string[] {
    if (this.currentUser()) {
      const roles = this.currentUser().roles ?? [];
      return roles.map((r: any) => (typeof r === 'string' ? r : r.name));
    }
    return this.keycloak?.realmAccess?.roles ?? [];
  }

  getToken(): string | undefined {
    return this.inMemoryToken || this.keycloak?.token;
  }

  loginWithCredentials(email: string, password: string): Observable<any> {
    // --- LÓGICA DE MOCK PARA TESTES SEM BACKEND ---
    if (password === 'senha123') {
      let mockRole: Role | null = null;
      let mockProfile: any = null;

      if (email === 'admin@mybuddy.com') {
        mockRole = Role.ADMIN;
        mockProfile = { id: 1, nome: 'Admin Master', email, roles: [Role.ADMIN] };
      } else if (email === 'ong@mybuddy.com') {
        mockRole = Role.ONG;
        mockProfile = { id: 2, nome: 'ONG Anjos', email, roles: [Role.ONG] };
      } else if (email === 'petshop@mybuddy.com') {
        mockRole = Role.PETSHOP;
        mockProfile = { id: 3, nome: 'Petshop Feliz', email, roles: [Role.PETSHOP] };
      } else if (email === 'user@mybuddy.com') {
        mockRole = Role.USER;
        mockProfile = { id: 4, nome: 'Adotante João', email, roles: [Role.USER] };
      }

      if (mockRole) {
        const mockToken = 'mock-jwt-token-for-' + mockRole;
        this.inMemoryToken = mockToken;
        this.setCookie('mybuddy_session', mockToken, 3600);

        this.sessionService.setRole(mockRole);
        this.currentUser.set(mockProfile);

        return of({ access_token: mockToken, profile: mockProfile, isMock: true }).pipe(delay(800));
      }
    }
    // --- FIM DA LÓGICA DE MOCK ---

    const tokenUrl = `${environment.keycloak.url}/realms/${environment.keycloak.realm}/protocol/openid-connect/token`;

    const payload = new HttpParams()
      .set('client_id', environment.keycloak.clientId)
      .set('grant_type', 'password')
      .set('username', email)
      .set('password', password)
      .set('scope', 'openid');

    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded',
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
      }),
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
      }),
    );
  }

  updateProfile(payload: any): Observable<any> {
    const profileUrl = `${environment.apiUrl}usuarios/meu-perfil`;
    return this.http.put<any>(profileUrl, payload).pipe(
      tap(profile => {
        this.currentUser.set(profile);
      }),
    );
  }

  logout() {
    this.inMemoryToken = null;
    this.deleteCookie('mybuddy_session');
    this.currentUser.set(null);
    this.sessionService.setRole(null);

    if (this.keycloak?.authenticated) {
      if (isPlatformBrowser(this.platformId)) {
        this.keycloak.logout({
          redirectUri: window.location.origin,
        });
      }
    } else {
      this.router.navigate(['/home']);
    }
  }

  // --- Helpers de Cookies Seguros (Proteção XSS e CSRF) ---
  private setCookie(name: string, value: string, maxAgeSeconds: number) {
    if (!isPlatformBrowser(this.platformId)) return;
    const secureFlag = window.location.protocol === 'https:' ? '; Secure' : '';
    document.cookie = `${name}=${encodeURIComponent(value)}; path=/; max-age=${maxAgeSeconds}; SameSite=Strict${secureFlag}`;
  }

  private getCookie(name: string): string | null {
    if (!isPlatformBrowser(this.platformId)) return null;
    const matches = document.cookie.match(new RegExp('(?:^|; )' + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + '=([^;]*)'));
    return matches ? decodeURIComponent(matches[1]) : null;
  }

  private deleteCookie(name: string) {
    if (!isPlatformBrowser(this.platformId)) return;
    document.cookie = `${name}=; path=/; max-age=0; SameSite=Strict`;
  }
}
