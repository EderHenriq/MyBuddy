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
    // Rota para a página de eventos de adoção
    path: 'eventos',
    loadComponent: () => import('./features/eventos/eventos.component').then(m => m.EventosComponent),
  },
  {
    // Rota para a página de perfil do usuário
    path: 'perfil',
    canActivate: [authGuard],
    loadComponent: () => import('./features/perfil/perfil').then(m => m.Perfil),
  },
  {
    path: 'auth/cadastro',
    loadComponent: () => import('./features/auth/cadastro-escolha-perfil/cadastro-escolha-perfil').then(m => m.CadastroEscolhaPerfil),
  },
  {
    path: 'auth/cadastro/adotante',
    redirectTo: 'auth/cadastro',
    pathMatch: 'full',
  },
  {
    path: 'auth/cadastro/ong',
    redirectTo: 'auth/cadastro',
    pathMatch: 'full',
  },
  {
    path: 'auth/cadastro/petshop',
    redirectTo: 'auth/cadastro',
    pathMatch: 'full',
  },
  {
    path: 'auth/recuperar-senha',
    loadComponent: () => import('./features/auth/recuperar-senha/recuperar-senha').then(m => m.RecuperarSenha),
  },
  {
    // Rota para a tela de login
    path: 'auth/login',
    loadComponent: () => import('./features/auth/login/login').then(m => m.Login),
  },
  {
    path: 'ong/pets',
    canActivate: [authGuard],
    loadComponent: () => import('./features/ong/meus-pets/meus-pets').then(m => m.MeusPets),
  },
  {
    path: 'ong/pets/novo',
    canActivate: [authGuard],
    loadComponent: () => import('./features/ong/cadastrar-pet/cadastrar-pet').then(m => m.CadastrarPet),
  },
  {
    // Rota para páginas não encontradas (404)
    path: '**',
    loadComponent: () => import('./shared/components/not-found/not-found').then(m => m.NotFound),
  },
];
