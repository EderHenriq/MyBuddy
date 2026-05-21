import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Role } from '../models/role.model';

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  // Inicializando como null
  private userRoleSubject = new BehaviorSubject<Role | null>(null);
  public userRole$ = this.userRoleSubject.asObservable();

  constructor() {
    // Ao iniciar, podemos tentar recuperar do localStorage (mockado por enquanto)
    const storedRole = localStorage.getItem('mockUserRole') as Role;
    if (storedRole) {
      this.userRoleSubject.next(storedRole);
    }
  }

  setRole(role: Role | null) {
    if (role) {
      localStorage.setItem('mockUserRole', role);
    } else {
      localStorage.removeItem('mockUserRole');
    }
    this.userRoleSubject.next(role);
  }

  getCurrentRole(): Role | null {
    return this.userRoleSubject.getValue();
  }
}
