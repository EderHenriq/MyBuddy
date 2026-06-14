import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const expectedRoles: string[] = route.data['roles'];

  if (!expectedRoles || expectedRoles.length === 0) {
    return true;
  }

  const userRoles = authService.getUserRoles();

  const hasRole = expectedRoles.some(role => userRoles.includes(role));

  if (hasRole) {
    return true;
  }

  router.navigate(['/']);
  return false;
};
