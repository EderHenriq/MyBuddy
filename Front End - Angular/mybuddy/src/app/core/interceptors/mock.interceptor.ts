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
      { id: '2', name: 'Usuário Teste 2', email: 'teste2@mybuddy.com' },
    ];
    return of(new HttpResponse({ status: 200, body: mockUsers })).pipe(delay(500));
  }

  if (req.url.includes('/pets') && req.method === 'GET') {
    const mockPets = [
      { id: '1', ownerId: '1', name: 'Rex', species: 'Cachorro', breed: 'Labrador' },
      { id: '2', ownerId: '2', name: 'Miau', species: 'Gato', breed: 'Siamês' },
    ];
    return of(new HttpResponse({ status: 200, body: mockPets })).pipe(delay(500));
  }

  // MOCK: Cadastro de usuário
  if (req.url.includes('/api/auth/cadastro') && req.method === 'POST') {
    console.log('[Mock API] Cadastro recebido:', req.body);
    return of(
      new HttpResponse({
        status: 200,
        body: { message: 'Usuário registrado com sucesso!' },
      }),
    ).pipe(delay(800));
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
    } else if (bodyStr.includes('username=admin') || bodyStr.includes('admin%40')) {
      roleToken = 'mock-jwt-admin';
    }

    const mockTokenResponse = {
      access_token: roleToken,
      expires_in: 3600,
      refresh_expires_in: 1800,
      refresh_token: 'mock-jwt-refresh-token',
      token_type: 'Bearer',
      scope: 'openid email profile',
    };

    return of(new HttpResponse({ status: 200, body: mockTokenResponse })).pipe(delay(600));
  }

  // MOCK: Obtenção de Perfil do Usuário
  if (req.url.includes('/api/usuarios/meu-perfil') && req.method === 'GET') {
    const authHeader = req.headers.get('Authorization') || '';

    let userProfile: Record<string, unknown> = {
      id: 1,
      nome: 'Ana Silva (Adotante)',
      email: 'adotante@mybuddy.com',
      telefone: '(11) 99999-9999',
      roles: [{ id: 1, name: 'ROLE_ADOTANTE' }],
      organizacao: null,
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
          website: 'www.caosemdono.org',
        },
      };
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
          website: 'www.petcharmoso.com',
        },
      };
    } else if (authHeader.includes('mock-jwt-admin')) {
      userProfile = {
        id: 4,
        nome: 'Administrador MyBuddy',
        email: 'admin@mybuddy.com',
        telefone: '(11) 96666-6666',
        roles: [{ id: 4, name: 'ROLE_ADMIN' }],
        organizacao: null,
      };
    }

    return of(new HttpResponse({ status: 200, body: userProfile })).pipe(delay(400));
  }

  if (req.url.includes('/api/usuarios/meu-perfil') && req.method === 'PUT') {
    const authHeader = req.headers.get('Authorization') || '';
    const body = (req.body ?? {}) as Record<string, unknown>;
    const organization = (body['organizacao'] ?? {}) as Record<string, unknown>;
    const isOng = authHeader.includes('mock-jwt-ong');
    const isPetshop = authHeader.includes('mock-jwt-petshop');
    const isAdmin = authHeader.includes('mock-jwt-admin');
    const role = isAdmin
      ? { id: 4, name: 'ROLE_ADMIN' }
      : isOng
        ? { id: 2, name: 'ROLE_ONG' }
        : isPetshop
          ? { id: 3, name: 'ROLE_PETSHOP' }
          : { id: 1, name: 'ROLE_ADOTANTE' };

    const updatedProfile: Record<string, unknown> = {
      id: isAdmin ? 4 : isOng ? 2 : isPetshop ? 3 : 1,
      nome: body['nome'],
      email: body['email'],
      telefone: body['telefone'],
      fotoPerfil: body['fotoPerfil'],
      aceitaMensagens: body['aceitaMensagens'],
      perfilPublico: body['perfilPublico'],
      notificacoesEmail: body['notificacoesEmail'],
      roles: [role],
      organizacao: null,
    };

    if (isOng || isPetshop) {
      updatedProfile['organizacao'] = {
        id: organization['id'] ?? (isOng ? 10 : 20),
        cnpj: organization['cnpj'] ?? body['cnpj'],
        nomeFantasia: organization['nomeFantasia'] ?? body['nomeFantasia'],
        emailContato: organization['emailContato'] ?? body['emailContato'],
        telefoneContato: organization['telefoneContato'] ?? body['telefoneContato'],
        endereco: organization['endereco'] ?? body['endereco'],
        descricao: organization['descricao'] ?? body['descricao'],
        website: organization['website'] ?? body['website'],
      };
    }

    return of(new HttpResponse({ status: 200, body: updatedProfile })).pipe(delay(500));
  }

  return next(req);
};
