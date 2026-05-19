import { HttpInterceptorFn, HttpResponse, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { of } from 'rxjs';
import { delay } from 'rxjs/operators';

export const mockInterceptor: HttpInterceptorFn = (req, next) => {
  // Se mockApi estiver desligado, apenas continua a requisição real
  if (!environment.mockApi) {
    return next(req);
  }

  console.log(`[Mock API] Interceptando chamada: ${req.method} ${req.url}`);

  if (req.url.includes('/users') && req.method === 'GET') {
    const mockUsers = [
      { id: '1', name: 'Usuário Teste 1', email: 'teste1@mybuddy.com' },
      { id: '2', name: 'Usuário Teste 2', email: 'teste2@mybuddy.com' }
    ];
    return of(new HttpResponse({ status: 200, body: mockUsers })).pipe(delay(500));
  }

  if (req.url.includes('/pets') && req.method === 'GET') {
    const mockPets = [
      { id: '1', ownerId: '1', name: 'Rex', species: 'Cachorro', breed: 'Labrador' },
      { id: '2', ownerId: '2', name: 'Miau', species: 'Gato', breed: 'Siamês' }
    ];
    return of(new HttpResponse({ status: 200, body: mockPets })).pipe(delay(500));
  }

  // MOCK: Cadastro de usuário
  if (req.url.includes('/api/auth/cadastro') && req.method === 'POST') {
    console.log('[Mock API] Cadastro recebido:', req.body);
    return of(new HttpResponse({ 
      status: 200, 
      body: { message: 'Usuário registrado com sucesso!' } 
    })).pipe(delay(800));
  }

  // MOCK: Obtenção de Token do Keycloak
  if (req.url.includes('/protocol/openid-connect/token') && req.method === 'POST') {
    const bodyStr = req.body instanceof HttpParams ? req.body.toString() : String(req.body);
    console.log('[Mock API] Solicitação de token recebida:', bodyStr);
    
    // Determina o token com base no e-mail recebido na requisição do form urlencoded
    let roleToken = 'mock-jwt-adotante';
    if (bodyStr.includes('username=ong') || bodyStr.includes('ong%40')) {
      roleToken = 'mock-jwt-ong';
    } else if (bodyStr.includes('username=petshop') || bodyStr.includes('petshop%40')) {
      roleToken = 'mock-jwt-petshop';
    }

    const mockTokenResponse = {
      access_token: roleToken,
      expires_in: 3600,
      refresh_expires_in: 1800,
      refresh_token: 'mock-jwt-refresh-token',
      token_type: 'Bearer',
      scope: 'openid email profile'
    };

    return of(new HttpResponse({ status: 200, body: mockTokenResponse })).pipe(delay(600));
  }

  // MOCK: Obtenção de Perfil do Usuário
  if (req.url.includes('/api/usuarios/meu-perfil') && req.method === 'GET') {
    const authHeader = req.headers.get('Authorization') || '';
    
    let userProfile = {
      id: 1,
      nome: 'Ana Silva (Adotante)',
      email: 'adotante@mybuddy.com',
      telefone: '(11) 99999-9999',
      roles: [{ id: 1, name: 'ROLE_ADOTANTE' }],
      organizacao: null
    };

    if (authHeader.includes('mock-jwt-ong')) {
      userProfile = {
        id: 2,
        nome: 'Carlos Souza (Diretor ONG)',
        email: 'ong@mybuddy.com',
        telefone: '(11) 98888-8888',
        roles: [{ id: 2, name: 'ROLE_ONG' }],
        organizacao: {
          id: 10,
          cnpj: '12.345.678/0001-99',
          nomeFantasia: 'Cão Sem Dono',
          emailContato: 'contato@caosemdono.org',
          telefoneContato: '(11) 5555-4444',
          endereco: 'Av. Paulista, 1000 - São Paulo',
          descricao: 'Associação de resgate e reabilitação de animais abandonados.',
          website: 'www.caosemdono.org'
        }
      } as any;
    } else if (authHeader.includes('mock-jwt-petshop')) {
      userProfile = {
        id: 3,
        nome: 'Marcos Almeida (Petshop)',
        email: 'petshop@mybuddy.com',
        telefone: '(11) 97777-7777',
        roles: [{ id: 3, name: 'ROLE_PETSHOP' }],
        organizacao: {
          id: 20,
          cnpj: '98.765.432/0001-11',
          nomeFantasia: 'Pet Charmoso',
          emailContato: 'gerencia@petcharmoso.com',
          telefoneContato: '(11) 4444-3333',
          endereco: 'Rua das Flores, 123 - São Paulo',
          descricao: 'O pet shop completo para o seu melhor amigo.',
          website: 'www.petcharmoso.com'
        }
      } as any;
    }

    return of(new HttpResponse({ status: 200, body: userProfile })).pipe(delay(400));
  }

  return next(req);
};
