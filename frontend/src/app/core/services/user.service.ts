import { Injectable, inject } from '@angular/core';
import { ApiService } from './api.service';
import { Usuario } from '../models/user.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private api = inject(ApiService);
  private readonly endpoint = 'usuarios';

  buscarTodos(): Observable<Usuario[]> {
    return this.api.get<Usuario[]>(this.endpoint);
  }

  buscarPorId(id: string): Observable<Usuario> {
    return this.api.get<Usuario>(`${this.endpoint}/${id}`);
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
