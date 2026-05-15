import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { PetService } from './pet.service';
import { environment } from '../../../environments/environment';
import { Pet } from '../models/pet.model';

describe('PetService', () => {
  let service: PetService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        PetService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(PetService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch pets', () => {
    const mockPets: Pet[] = [
      { id: '1', ownerId: '1', name: 'Rex', species: 'Dog' }
    ];

    service.getAll().subscribe(pets => {
      expect(pets.length).toBe(1);
      expect(pets).toEqual(mockPets);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}pets`);
    expect(req.request.method).toBe('GET');
    req.flush(mockPets);
  });
});
