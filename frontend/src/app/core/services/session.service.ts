import { isPlatformBrowser } from "@angular/common";
import { inject, Injectable, PLATFORM_ID, signal } from "@angular/core";
import { Role } from "@core/models/role.model";

@Injectable({
  providedIn: 'root',
})

export class SessionService {
  private platformId = inject(PLATFORM_ID);
  private roleSignal = signal<Role | null>(null);
  readonly userRole = this.roleSignal.asReadonly();

  constructor() {
    if (isPlatformBrowser(this.platformId)) {
      const storedRole = localStorage.getItem('mockUserRole') as Role | null;
      if (storedRole) {
        this.roleSignal.set(storedRole);
      }
    }
  }

  setRole(role: Role | null): void {
    if (isPlatformBrowser(this.platformId)) {
      if (role) {
        localStorage.setItem('mockUserRole', role);
      } else {
        localStorage.removeItem('mockUserRole');
      }
    }

    this.roleSignal.set(role);
  }

  getCurrentRole(): Role | null {
    return this.roleSignal();
  }
}