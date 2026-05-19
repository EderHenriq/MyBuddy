import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guards';

export const routes: Routes = [
  // ═══════════════════════════════════════════════════════════════
  // 1. ROTAS PÚBLICAS (Acesso livre, sem necessidade de login)
  // ═══════════════════════════════════════════════════════════════

  // Landing Page — Página de apresentação do app
  {
    path: '',
    loadComponent: () => import('./features/landing-page/landing-page').then(m => m.LandingPage),
  },

  // Institucional — Páginas estáticas (Quem Somos, Contato, FAQ, etc.)
  {
    path: 'institucional',
    loadComponent: () => import('./features/institucional/institucional').then(m => m.Institucional),
    children: [
      { path: '', redirectTo: 'quem-somos', pathMatch: 'full' },
      {
        path: 'quem-somos',
        loadComponent: () => import('./features/institucional/quem-somos/quem-somos').then(m => m.QuemSomos),
      },
      {
        path: 'contato',
        loadComponent: () => import('./features/institucional/contato/contato').then(m => m.Contato),
      },
      {
        path: 'faq',
        loadComponent: () => import('./features/institucional/faq/faq').then(m => m.Faq),
      },
      {
        path: 'termos-uso',
        loadComponent: () => import('./features/institucional/termos-uso/termos-uso').then(m => m.TermosUso),
      },
      {
        path: 'politica-privacidade',
        loadComponent: () => import('./features/institucional/politica-privacidade/politica-privacidade').then(m => m.PoliticaPrivacidade),
      },
    ],
  },

  // Auth — Fluxo de autenticação / onboarding
  {
    path: 'auth/login',
    loadComponent: () => import('./features/auth/login/login').then(m => m.Login),
  },
  {
    path: 'auth/recuperar-senha',
    loadComponent: () => import('./features/auth/recuperar-senha/recuperar-senha').then(m => m.RecuperarSenha),
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

  // Public — Perfis públicos e conteúdo acessível sem login
  {
    path: 'public/eventos',
    loadComponent: () => import('./features/public/eventos-adocao/eventos-adocao').then(m => m.EventosAdocao),
  },
  {
    path: 'public/servicos',
    loadComponent: () => import('./features/public/guia-servicos/guia-servicos').then(m => m.GuiaServicos),
  },

  // ═══════════════════════════════════════════════════════════════
  // 2. ROTAS CORE (Requer login via authGuard / Keycloak)
  // ═══════════════════════════════════════════════════════════════

  // Home — Painel central com lembretes, atalhos, categorias
  {
    path: 'home',
    canActivate: [authGuard],
    loadComponent: () => import('./features/home/home').then(m => m.Home),
  },

  // Pets — Explorar e ver detalhes de pets para adoção
  {
    path: 'pets',
    canActivate: [authGuard],
    loadComponent: () => import('./features/pets/pets').then(m => m.Pets),
  },
  {
    path: 'pets/:id',
    canActivate: [authGuard],
    loadComponent: () => import('./features/pets/pet-details/pet-details').then(m => m.PetDetails),
  },

  // Marketplace — Loja de produtos e serviços
  {
    path: 'marketplace',
    canActivate: [authGuard],
    loadComponent: () => import('./features/marketplace/marketplace').then(m => m.Marketplace),
  },
  {
    path: 'marketplace/produto/:id',
    canActivate: [authGuard],
    loadComponent: () => import('./features/marketplace/detalhes-produto/detalhes-produto').then(m => m.DetalhesProduto),
  },

  // Doações — Campanhas de arrecadação
  {
    path: 'doacoes',
    canActivate: [authGuard],
    loadComponent: () => import('./features/doacoes/doacoes').then(m => m.Doacoes),
  },
  {
    path: 'doacoes/historico',
    canActivate: [authGuard],
    loadComponent: () => import('./features/doacoes/historico/historico').then(m => m.Historico),
  },

  // Checkout — Processo de pagamento (Endereço -> Pagamento -> Confirmação)
  {
    path: 'checkout',
    canActivate: [authGuard],
    loadComponent: () => import('./features/checkout/checkout').then(m => m.Checkout),
  },
  {
    path: 'checkout/endereco',
    canActivate: [authGuard],
    loadComponent: () => import('./features/checkout/endereco/endereco').then(m => m.Endereco),
  },
  {
    path: 'checkout/pagamento',
    canActivate: [authGuard],
    loadComponent: () => import('./features/checkout/pagamento/pagamento').then(m => m.Pagamento),
  },
  {
    path: 'checkout/confirmacao',
    canActivate: [authGuard],
    loadComponent: () => import('./features/checkout/confirmacao/confirmacao').then(m => m.Confirmacao),
  },

  // ═══════════════════════════════════════════════════════════════
  // 3. PAINÉIS DE USUÁRIO (Requer login + role específico)
  // ═══════════════════════════════════════════════════════════════

  // --- Adotante ---
  {
    path: 'adotante',
    canActivate: [authGuard],
    loadComponent: () => import('./features/adotante/dashboard-adotante/dashboard-adotante').then(m => m.DashboardAdotante),
  },
  {
    path: 'adotante/perfil',
    canActivate: [authGuard],
    loadComponent: () => import('./features/adotante/perfil/perfil').then(m => m.Perfil),
  },
  {
    path: 'adotante/favoritos',
    canActivate: [authGuard],
    loadComponent: () => import('./features/adotante/favoritos/favoritos').then(m => m.Favoritos),
  },
  {
    path: 'adotante/interesses',
    canActivate: [authGuard],
    loadComponent: () => import('./features/adotante/meus-interesses/meus-interesses').then(m => m.MeusInteresses),
  },

  // --- ONG ---
  {
    path: 'ong',
    canActivate: [authGuard],
    loadComponent: () => import('./features/ong/dashboard-ong/dashboard-ong').then(m => m.DashboardOng),
  },
  {
    path: 'ong/pets',
    canActivate: [authGuard],
    loadComponent: () => import('./features/ong/meus-pets/meus-pets').then(m => m.MeusPets),
  },
  {
    path: 'ong/doacoes',
    canActivate: [authGuard],
    loadComponent: () => import('./features/ong/doacoes-recebidas/doacoes-recebidas').then(m => m.DoacoesRecebidas),
  },

  // --- Petshop ---
  {
    path: 'petshop',
    canActivate: [authGuard],
    loadComponent: () => import('./features/petshop/dashboard-petshop/dashboard-petshop').then(m => m.DashboardPetshop),
  },
  {
    path: 'petshop/produtos',
    canActivate: [authGuard],
    loadComponent: () => import('./features/petshop/meus-produtos/meus-produtos').then(m => m.MeusProdutos),
  },

  // ═══════════════════════════════════════════════════════════════
  // 4. UTILITÁRIOS E TESTES
  // ═══════════════════════════════════════════════════════════════

  // Styleguide — Biblioteca de componentes (dev/admin)
  {
    path: 'styleguide',
    loadComponent: () => import('./features/styleguide/styleguide').then(m => m.Styleguide),
  },

  // Rota 404 — Deve ser sempre a última
  {
    path: '**',
    loadComponent: () => import('./shared/components/not-found/not-found').then(m => m.NotFound),
  },
];
