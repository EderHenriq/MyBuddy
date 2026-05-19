import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  // Rotas Públicas Estáticas — podem ser pre-renderizadas
  {
    path: '',
    renderMode: RenderMode.Prerender,
  },
  {
    path: 'institucional/**',
    renderMode: RenderMode.Prerender,
  },
  {
    path: 'auth/**',
    renderMode: RenderMode.Prerender,
  },
  {
    path: 'public/**',
    renderMode: RenderMode.Prerender,
  },
  {
    path: 'styleguide',
    renderMode: RenderMode.Prerender,
  },

  // Todas as outras rotas (protegidas, com parâmetros dinâmicos)
  // devem ser renderizadas no client
  {
    path: '**',
    renderMode: RenderMode.Client,
  },
];
