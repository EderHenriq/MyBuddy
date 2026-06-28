import { inject, Injectable, PLATFORM_ID, signal } from "@angular/core";
import { isPlatformBrowser } from "@angular/common";
import Keycloak from "keycloak-js";
import { HttpClient, HttpParams, HttpHeaders } from "@angular/common/http";
import { environment } from "../../../environments/environment";
import { Observable, tap, catchError, throwError} from "rxjs";
import { Router } from "@angular/router";
import { SessionService } from "./session.service";
import { Role } from "../models/role.model";
import { Usuario } from "../models/user.model";

@Injectable({
  providedIn: "root",
})
export class AuthService {
  private platformId = inject(PLATFORM_ID);
  private keycloak = inject(Keycloak, { optional: true });
  private http = inject(HttpClient);
  private router = inject(Router);
  private sessionService = inject(SessionService);

  private tokenEmMemoria: string | null = null;

  usuarioAtual = signal<Usuario | null>(null);

  constructor() {
    if (isPlatformBrowser(this.platformId)) {
      this.restaurarSessao();
    }
  }

  private restaurarSessao() {
    const token = this.obterCookie("mybuddy_session");
    if (token) {
      this.tokenEmMemoria = token;

      this.obterPerfil().subscribe({
        next: (perfil) => {
          this.usuarioAtual.set(perfil);
        },
        error: () => {
          this.sair();
        },
      });
    }
  }

  estaLogado(): boolean {
    return !!this.tokenEmMemoria || !!this.keycloak?.authenticated;
  }

  obterPapeisUsuario(): string[] {

    const usuario = this.usuarioAtual();
    if (usuario) {
      const papeis = usuario.roles ?? [];
      return papeis.map((r: any) => (typeof r === "string" ? r : r.name));
    }
    return this.keycloak?.realmAccess?.roles ?? [];
    
  }

  obterToken(): string | undefined {
    return this.tokenEmMemoria || this.keycloak?.token;
  }

  loginComCredenciais(email: string, senha: string): Observable<any> {

    const tokenUrl = `${environment.keycloak.url}/realms/${environment.keycloak.realm}/protocol/openid-connect/token`;

    const payload = new HttpParams()
      .set("client_id", environment.keycloak.clientId)
      .set("grant_type", "password")
      .set("username", email)
      .set("password", senha)
      .set("scope", "openid");

    const headers = new HttpHeaders({
      "Content-Type": "application/x-www-form-urlencoded",
    });

    return this.http.post<any>(tokenUrl, payload.toString(), { headers }).pipe(
      tap((resposta) => {
        const token = resposta.access_token;
        this.tokenEmMemoria = token;
        this.definirCookie("mybuddy_session", token, 3600);
      }),
      catchError((erro) => {
        console.error("Erro na autenticação:", erro);
        return throwError(() => erro);
      }),
    );
  }

  registrar(payload: any): Observable<any> {
    const cadastroUrl = `${environment.apiUrl}auth/cadastro`;
    return this.http.post<any>(cadastroUrl, payload);
  }

  obterPerfil(): Observable<Usuario> {

    const profileUrl = `${environment.apiUrl}usuarios/meu-perfil`;
    return this.http.get<Usuario>(profileUrl).pipe(
      tap((perfil) => {
        this.usuarioAtual.set(perfil);
        const roles = this.getUserRoles();
        const role = roles.length > 0 ? (roles[0] as Role) : null;
        this.sessionService.setRole(role);
      }),
      catchError((erro) => {
        console.error("Erro ao obter perfil:", erro);
        this.sair();
        return throwError(() => erro);
      }),
    );

  }

  atualizarPerfil(payload: any): Observable<any> {

    const profileUrl = `${environment.apiUrl}usuarios/meu-perfil`;
    return this.http.put<any>(profileUrl, payload).pipe(
      tap((perfil) => {
        this.usuarioAtual.set(perfil);
        const roles = this.getUserRoles();
        const role = roles.length > 0 ? (roles[0] as Role) : null;
        this.sessionService.setRole(role);
      }),
    );

  }

  sair() {
    this.tokenEmMemoria = null;
    this.removerCookie("mybuddy_session");
    this.usuarioAtual.set(null);
    this.sessionService.setRole(null);

    if (this.keycloak?.authenticated) {
      if (isPlatformBrowser(this.platformId)) {
        this.keycloak.logout({
          redirectUri: window.location.origin,
        });
      }
    } else {
      this.router.navigate(["/home"]);
    }
  }

  getUserRoles(): string[] {
    const roles = this.usuarioAtual()?.roles || [];
    return roles.map((r: any) => (typeof r === "string" ? r : r.name || ""));
  }

  private definirCookie(nome: string, valor: string, maxAgeSeconds: number) {
    if (!isPlatformBrowser(this.platformId)) return;
    const secureFlag = window.location.protocol === "https:" ? "; Secure" : "";
    document.cookie = `${nome}=${encodeURIComponent(valor)}; path=/; max-age=${maxAgeSeconds}; SameSite=Strict${secureFlag}`;
  }

  private obterCookie(nome: string): string | null {
    if (!isPlatformBrowser(this.platformId)) return null;
    const regexMatch = document.cookie.match(
      new RegExp(
        "(?:^|; )" +
          nome.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, "\\$1") +
          "=([^;]*)",
      ),
    );
    return regexMatch ? decodeURIComponent(regexMatch[1]) : null;
  }

  private removerCookie(nome: string) {
    if (!isPlatformBrowser(this.platformId)) return;
    document.cookie = `${nome}=; path=/; max-age=0; SameSite=Strict`;
  }
}
