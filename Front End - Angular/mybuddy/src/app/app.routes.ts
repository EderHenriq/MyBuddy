import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guards';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    //Rota de testes de componentes
    path: 'cards',
    loadComponent: () => import('./shared/components/cards/cards').then(m => m.Cards),
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
    // Rota para a página de serviços pet
    path: 'servicos',
    loadComponent: () => import('./features/servicos/servicos.component').then(m => m.ServicosComponent),
  },
  {
    // Rota para o Marketplace (Produtos)
    path: 'produtos',
    loadComponent: () => import('./features/marketplace/marketplace').then(m => m.Marketplace),
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
    path: 'admin',
    canActivate: [roleGuard],
    data: { roles: ['ROLE_ADMIN'] },
    loadComponent: () => import('./core/layout/dashboard-layout/dashboard-layout').then(m => m.DashboardLayout),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./features/admin/dashboard/dashboard').then(m => m.Dashboard) },
      { path: 'ongs', loadComponent: () => import('./features/admin/ongs/ongs').then(m => m.Ongs) },
      { path: 'usuarios', loadComponent: () => import('./features/admin/usuarios/usuarios').then(m => m.Usuarios) },
      { path: 'pets', loadComponent: () => import('./features/admin/pets/pets').then(m => m.Pets) },
      { path: 'suporte', loadComponent: () => import('./features/admin/suporte/suporte').then(m => m.Suporte) },
      { path: 'configuracoes', loadComponent: () => import('./features/admin/configuracoes/configuracoes').then(m => m.Configuracoes) },
    ],
  },
  {
    path: 'ong-panel',
    canActivate: [roleGuard],
    data: { roles: ['ROLE_ONG', 'ROLE_ADMIN'] },
    loadComponent: () => import('./core/layout/dashboard-layout/dashboard-layout').then(m => m.DashboardLayout),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./features/ong/dashboard-ong/dashboard-ong').then(m => m.DashboardOng) },
      { path: 'pets', loadComponent: () => import('./features/ong/meus-pets/meus-pets').then(m => m.MeusPets) },
      { path: 'solicitacoes', loadComponent: () => import('./features/ong/solicitacoes/solicitacoes').then(m => m.Solicitacoes) },
      { path: 'eventos', loadComponent: () => import('./features/ong/eventos-ong/eventos-ong').then(m => m.EventosOng) },
    ],
  },
  {
    path: 'petshop-panel',
    canActivate: [roleGuard],
    data: { roles: ['ROLE_PETSHOP', 'ROLE_ADMIN'] },
    loadComponent: () => import('./core/layout/dashboard-layout/dashboard-layout').then(m => m.DashboardLayout),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./features/petshop/dashboard-petshop/dashboard-petshop').then(m => m.DashboardPetshop) },
      { path: 'produtos', loadComponent: () => import('./features/petshop/meus-produtos/meus-produtos').then(m => m.MeusProdutos) },
      { path: 'pedidos', loadComponent: () => import('./features/petshop/pedidos/pedidos').then(m => m.Pedidos) },
      { path: 'chat', loadComponent: () => import('./features/petshop/chat-clientes/chat-clientes').then(m => m.ChatClientes) },
    ],
  },
  {
    // Rota para páginas não encontradas (404)
    path: '**',
    loadComponent: () => import('./shared/components/not-found/not-found').then(m => m.NotFound),
  },
];
