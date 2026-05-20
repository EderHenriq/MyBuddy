import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guards';

export const routes: Routes = [
  {
    //Rota testes
    path: 'empty-state',
    loadComponent: () => import('./shared/components/empty-state/empty-state').then(m => m.EmptyState),
  },
  {
    // Redirecionamento inicial para a rota 'home'
    path: '',
    loadComponent: () => import('./features/landing-page/landing-page').then(m => m.LandingPage),
  },
  {
    // Rota para a página inicial (home)
    path: 'home',
    canActivate: [authGuard],
    loadComponent: () => import('./features/home/home').then(m => m.Home),
  },
  {
    // Rota para a página de pets
    path: 'pets',
    canActivate: [authGuard],
    loadComponent: () => import('./features/pets/pets').then(m => m.Pets),
  },
  {
    // Rota para a página de styleguide
    path: 'style-guide',
    loadComponent: () => import('./features/styleguide/styleguide').then(m => m.Styleguide),
  },
  {
    // Rota para a página de perfil do usuário
    path: 'perfil',
    canActivate: [authGuard],
    loadComponent: () => import('./features/perfil/perfil').then(m => m.Perfil),
  },
  {
    // Rota para a tela de login
    path: 'auth/login',
    loadComponent: () => import('./features/auth/login/login').then(m => m.Login),
  },
  {
    // Rota para páginas não encontradas (404)
    path: '**',
    loadComponent: () => import('./shared/components/not-found/not-found').then(m => m.NotFound),
  },
];
