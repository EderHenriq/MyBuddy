import { inject, Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { SessionService } from './session.service';
import { Observable, tap } from 'rxjs';
import { Usuario } from '@core/models/user.model';
import { Role } from '@core/models/role.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private api = inject(ApiService);
  private sessionService = inject(SessionService);
  private readonly endpoint = 'usuarios';

  buscarTodos(): Observable<Usuario[]> {
    return this.api.get<Usuario[]>(this.endpoint);
  }

  buscarPorId(id: string): Observable<Usuario> {
    return this.api.get<Usuario>(`${this.endpoint}/${id}`);
  }

  buscarPerfil(): Observable<Usuario> {
    return this.api.get<Usuario>(`${this.endpoint}/meu-perfil`).pipe(
      tap(perfil => {
        const roles = perfil.roles ?? [];
        const role = roles.length > 0 ? (roles[0] as Role) : null;
        this.sessionService.setRole(role);
      }),
    );
  }

  atualizarPerfil(payload: Partial<Usuario>): Observable<Usuario> {
    return this.api.put<Usuario>(`${this.endpoint}/meu-perfil`, payload).pipe(
      tap(perfil => {
        const roles = perfil.roles ?? [];
        const role = roles.length > 0 ? (roles[0] as Role) : null;
        this.sessionService.setRole(role);
      }),
    );
  }

  criar(usuario: Partial<Usuario>): Observable<Usuario> {
    return this.api.post<Usuario>(this.endpoint, usuario);
  }

  atualizar(id: string, usuario: Partial<Usuario>): Observable<Usuario> {
    return this.api.put<Usuario>(`${this.endpoint}/${id}`, usuario);
  }

  deletar(id: string): Observable<void> {
    return this.api.delete<void>(`${this.endpoint}/${id}`);
  }
}
