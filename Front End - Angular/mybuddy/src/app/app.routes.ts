import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    // Redirecionamento inicial para a rota 'home'
    path: '',
    loadComponent: () => import('./features/landing-page/landing-page').then(m => m.LandingPage),
  },
  {
    // Rota para a página inicial (home)
    path: 'home',
    loadComponent: () => import('./features/home/home').then(m => m.Home),
  },
  {
    // Rota para a página de pets
    path: 'pets',
    loadComponent: () => import('./features/pets/pets').then(m => m.Pets),
  },
  {
    // Rota para páginas não encontradas (404)
    path: '**',
    loadComponent: () => import('./shared/components/not-found/not-found').then(m => m.NotFound),
  },
];
