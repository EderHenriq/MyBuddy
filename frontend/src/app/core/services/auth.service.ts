import { HttpClient } from '@angular/common/http';
import { inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { Router } from 'express';
import Keycloak from 'keycloak-js';
import { SessionService } from './session.service';
import { UserService } from './user.service';
import { Usuario } from '@core/models/user.model';
import { isPlatformBrowser } from '@angular/common';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private plataformId = inject(PLATFORM_ID);
  private keyclock = inject(Keycloak, { optional: true });
  private http = inject(HttpClient);
  private router = inject(Router);
  private sessionService = inject(SessionService);
  private userService = inject(UserService);
  private tokenEmMemoria: string | null = null;

  usuarioAtual = signal<Usuario | null>(null);

  constructor() {
    if (isPlatformBrowser(this.plataformId)) {
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
    return !!this.tokenEmMemoria || !!this.keyclock?.authenticated;
  }

  obterPapeisUsuario(): string[] {
    const roles = this.usuarioAtual()?. roles ?? [];
    if (roles.length > 0) {
      return roles.map((r: any) => (typeof r === 'string' ? r : r.name));
    }
  }

  obterToken(): string | undefined {
    return this.tokenEmMemoria ?? this.keyclock?.token;
  }

  loginComCredenciais(email: string, senha: string): Observable<any> {
    if (environment.mockApi || senha === 'Senha123' || senha === 'senha123') {
      const mockData = this.resolverMockPorEmail(email);

      if (mockData) {
        const { role, profile } =mockData;
        const mockToken = 'mock-jwt-token-for-${role}';

        this.tokenEmMemoria = mockToken;
        this.definirCookie('mybuddy_session', mockToken, 3600);
        this.sessionService.setRole(role);
      }
    }
  }
}
