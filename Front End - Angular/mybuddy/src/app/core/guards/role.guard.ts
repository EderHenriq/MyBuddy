import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Exemplo simplificado: espera-se que route.data['roles'] contenha um array de roles permitidas
  const expectedRoles: string[] = route.data['roles'];

  // Para evitar que a tela quebre antes da integração com o Back-end real:
  // Se não houver roles esperadas, libera o acesso.
  if (!expectedRoles || expectedRoles.length === 0) {
    return true;
  }

  // Lógica fictícia para simular uma role atual. Na prática, virá do authService.getCurrentUserRole()
  // const userRole = authService.getUserRole();
  const userRole = localStorage.getItem('mockUserRole') || 'ROLE_ADMIN'; // Simulando que é um ADMIN por padrão para os testes locais funcionarem

  if (expectedRoles.includes(userRole)) {
    return true;
  }

  // Se não tiver permissão, redireciona para a home ou página não autorizada
  router.navigate(['/']);
  return false;
};
