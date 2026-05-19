import { Injectable, inject } from '@angular/core';
import { ApiService } from './api.service';
import { User } from '../models/user.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private api = inject(ApiService);
  private readonly endpoint = 'users';

  getAll(): Observable<User[]> {
    return this.api.get<User[]>(this.endpoint);
  }

  getById(id: string): Observable<User> {
    return this.api.get<User>(`${this.endpoint}/${id}`);
  }

  create(user: Partial<User>): Observable<User> {
    return this.api.post<User>(this.endpoint, user);
  }

  update(id: string, user: Partial<User>): Observable<User> {
    return this.api.put<User>(`${this.endpoint}/${id}`, user);
  }

  delete(id: string): Observable<void> {
    return this.api.delete<void>(`${this.endpoint}/${id}`);
  }
}
