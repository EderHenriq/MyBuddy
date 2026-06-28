import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { Router } from '@angular/router';
import Keycloak from 'keycloak-js';
import { SessionService } from './session.service';
import { UserService } from './user.service';
import { Usuario } from '@core/models/user.model';
import { isPlatformBrowser } from '@angular/common';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { environment } from '@env/environment';

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

    this.userService.buscarPerfil().subscribe({
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
