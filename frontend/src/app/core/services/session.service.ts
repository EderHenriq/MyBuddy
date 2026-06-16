import { Injectable, inject, PLATFORM_ID } from "@angular/core";
import { isPlatformBrowser } from "@angular/common";
import { BehaviorSubject } from "rxjs";
import { Role } from "@core/models/role.model";

@Injectable({
  providedIn: "root",
})
export class SessionService {
  private platformId = inject(PLATFORM_ID);

  private userRoleSubject = new BehaviorSubject<Role | null>(null);
  public userRole$ = this.userRoleSubject.asObservable();

  constructor() {
    if (isPlatformBrowser(this.platformId)) {
      const storedRole = localStorage.getItem("mockUserRole") as Role;
      if (storedRole) {
        this.userRoleSubject.next(storedRole);
      }
    }
  }

  setRole(role: Role | null) {
    if (isPlatformBrowser(this.platformId)) {
      if (role) {
        localStorage.setItem("mockUserRole", role);
      } else {
        localStorage.removeItem("mockUserRole");
      }
    }
    this.userRoleSubject.next(role);
  }

  getCurrentRole(): Role | null {
    return this.userRoleSubject.getValue();
  }
}
