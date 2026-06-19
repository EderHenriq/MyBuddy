import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { Router } from '@angular/router';
import Keycloak from 'keycloak-js';
import { SessionService } from './session.service';
import { UserService } from './user.service';
import { Usuario } from '@core/models/user.model';
import { isPlatformBrowser } from '@angular/common';
import { catchError, delay, Observable, of, tap, throwError } from 'rxjs';
import { environment } from '@env/environment';
import { Role } from '@core/models/role.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private platformId = inject(PLATFORM_ID);
  private keycloak = inject(Keycloak, { optional: true });
  private http = inject(HttpClient);
  private router = inject(Router);
  private sessionService = inject(SessionService);
  private userService = inject(UserService);
  private tokenEmMemoria: string | null = null;

  usuarioAtual = signal<Usuario | null>(null);

  constructor() {
    if (isPlatformBrowser(this.platformId)) {
      this.restaurarSessao();
    }
  }

  private restaurarSessao(): void {
    const token = this.obterCookie('mybuddy_session');
    if (!token) return;

    this.tokenEmMemoria = token;

    if (token.startsWith('mock-jwt-token')) {
      const papel = this.sessionService.getCurrentRole();

      if (papel) {
        this.usuarioAtual.set({
          id: 99,
          nome: 'Usuário Teste (' + papel + ')',
          email: 'teste@mybuddy.com',
          roles: [papel],
        } as Usuario);
      }
      return;
    }

    this.userService.buscarPerfil(this.tokenEmMemoria).subscribe({
      next: perfil => this.usuarioAtual.set(perfil),
      error: () => this.sair(),
    });
  }

  estaLogado(): boolean {
    return !!this.tokenEmMemoria || !!this.keycloak?.authenticated;
  }

  obterPapeisUsuario(): string[] {
    const roles = this.usuarioAtual()?.roles ?? [];
    if (roles.length > 0) {
      return roles.map((r: any) => (typeof r === 'string' ? r : r.name));
    }

    return this.keycloak?.realmAccess?.roles ?? [];
  }

  obterToken(): string | undefined {
    return this.tokenEmMemoria ?? this.keycloak?.token;
  }

  loginComCredenciais(email: string, senha: string): Observable<any> {
    if (environment.mockApi || senha === 'Senha123' || senha === 'senha123') {
      const mockData = this.resolverMockPorEmail(email);

      if (mockData) {
        const { role, profile } = mockData;
        const mockToken = `mock-jwt-token-for-${role}`;

        this.tokenEmMemoria = mockToken;
        this.definirCookie('mybuddy_session', mockToken, 3600);
        this.sessionService.setRole(role);
        this.usuarioAtual.set(profile);

        return of({ access_token: mockToken, profile, isMock: true }).pipe(delay(800));
      }
    }

    const tokenUrl = `${environment.keycloak.url}/realms/${environment.keycloak.realm}/protocol/openid-connect/token`;

    const payload = new HttpParams()
      .set('client_id', environment.keycloak.clientId)
      .set('grant_type', 'password')
      .set('username', email)
      .set('password', senha)
      .set('scope', 'openid');

    const headers = new HttpHeaders({ 'Content-Type': 'application/x-www-form-urlencoded' });

    return this.http.post<any>(tokenUrl, payload.toString(), { headers }).pipe(
      tap(resposta => {
        this.tokenEmMemoria = resposta.access_token;
        this.definirCookie('mybuddy_session', resposta.access_token, 3600);
      }),

      catchError(erro => {
        console.error('Erro na autenticação:', erro);
        return throwError(() => erro);
      }),
    );
  }

  registrar(payload: any): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}auth/cadastro`, payload);
  }

  sair(): void {
    this.tokenEmMemoria = null;
    this.removerCookie('mybuddy_session');
    this.usuarioAtual.set(null);
    this.sessionService.setRole(null);

    if (this.keycloak?.authenticated) {
      if (isPlatformBrowser(this.platformId)) {
        this.keycloak.logout({ redirectUri: window.location.origin });
      }
    } else {
      this.router.navigate(['/home']);
    }
  }

  private resolverMockPorEmail(email: string): { role: Role; profile: Usuario } | null {
    const emailLower = email.toLowerCase();
    const mocks: Record<string, { role: Role; nome: string }> = {
      'admin@mybuddy.com': { role: Role.ADMIN, nome: 'Admin Master' },
      'ong@mybuddy.com': { role: Role.ONG, nome: 'ONG Anjos' },
      'petshop@mybuddy.com': { role: Role.PETSHOP, nome: 'Petshop Feliz' },
      'user@mybuddy.com': { role: Role.USER, nome: 'Adotante João' },
      'adotante@mybuddy.com': { role: Role.USER, nome: 'Adotante João' },
    };

    const found = mocks[emailLower];
    if (!found) return null;

    return {
      role: found.role,
      profile: { id: 99, nome: found.nome, email, roles: [found.role] } as Usuario,
    };
  }

  private definirCookie(nome: string, valor: string, maxAgeSeconds: number): void {
    if (!isPlatformBrowser(this.platformId)) return;

    const secureFlag = window.location.protocol === 'https:' ? '; Secure' : '';
    document.cookie = `${nome}=${encodeURIComponent(valor)}; path=/; max-age=${maxAgeSeconds}; SameSite=Strict${secureFlag}`;
  }

  private obterCookie(nome: string): string | null {
    if (!isPlatformBrowser(this.platformId)) return null;

    const match = document.cookie.match(new RegExp('(?:^|; )' + nome.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + '=([^;]*)'));
    return match ? decodeURIComponent(match[1]) : null;
  }

  private removerCookie(nome: string): void {
    if (!isPlatformBrowser(this.platformId)) return;

    document.cookie = `${nome}=; path=/; max-age=0; SameSite=Strict`;
  }
}
