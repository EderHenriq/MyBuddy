import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
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

  return next(req);
};
