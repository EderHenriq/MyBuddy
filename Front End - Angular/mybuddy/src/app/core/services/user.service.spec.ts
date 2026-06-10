import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { UserService } from './user.service';
import { environment } from '../../../environments/environment';
import { Usuario } from '../models/user.model';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UserService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch users', () => {
    const mockUsers: Usuario[] = [{ id: 1, nome: 'Eder', email: 'eder@test.com' }];

    service.buscarTodos().subscribe((users: Usuario[]) => {
      expect(users.length).toBe(1);
      expect(users).toEqual(mockUsers);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}usuarios`);
    expect(req.request.method).toBe('GET');
    req.flush(mockUsers);
  });
});
