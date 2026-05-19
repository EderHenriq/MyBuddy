import { Injectable, inject } from '@angular/core';
import { ApiService } from './api.service';
import { Pet } from '../models/pet.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PetService {
  private api = inject(ApiService);
  private readonly endpoint = 'pets';

  getAll(): Observable<Pet[]> {
    return this.api.get<Pet[]>(this.endpoint);
  }

  getById(id: string): Observable<Pet> {
    return this.api.get<Pet>(`${this.endpoint}/${id}`);
  }

  getByOwnerId(ownerId: string): Observable<Pet[]> {
    return this.api.get<Pet[]>(`${this.endpoint}/owner/${ownerId}`);
  }

  create(pet: Partial<Pet>): Observable<Pet> {
    return this.api.post<Pet>(this.endpoint, pet);
  }

  update(id: string, pet: Partial<Pet>): Observable<Pet> {
    return this.api.put<Pet>(`${this.endpoint}/${id}`, pet);
  }

  delete(id: string): Observable<void> {
    return this.api.delete<void>(`${this.endpoint}/${id}`);
  }
}
