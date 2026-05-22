import { Injectable, inject } from '@angular/core';
import { ApiService } from './api.service';
import { Pet } from '../models/pet.model';
import { Observable, of, delay } from 'rxjs';

// TODO: Remover após ligar no backend
const MOCK_PETS: any[] = [
  {
    name: 'Thor',
    age: '3 anos',
    breed: 'Border Collie',
    sex: 'Macho',
    vaccinated: 'Sim',
    imageUrl: 'https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&q=80&w=800',
    isFavorite: false,
  },
  {
    name: 'Pêssego',
    age: '5 anos',
    breed: 'SRD',
    sex: 'Macho',
    vaccinated: 'Sim',
    imageUrl: 'https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&q=80&w=800',
    isFavorite: false,
  },
  {
    name: 'Amora',
    age: '3 anos',
    breed: 'Yorkshire',
    sex: 'Fêmea',
    vaccinated: 'Sim',
    imageUrl: 'https://images.unsplash.com/photo-1588392382834-a891154bca4d?auto=format&fit=crop&q=80&w=800',
    isFavorite: false,
  },
  {
    name: 'Francesca',
    age: '4 anos',
    breed: 'SRD',
    sex: 'Fêmea',
    vaccinated: 'Sim',
    imageUrl: 'https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&q=80&w=800',
    isFavorite: false,
  },
  {
    name: 'Jade',
    age: '1 ano',
    breed: 'Mini Lop',
    sex: 'Fêmea',
    vaccinated: 'Sim',
    imageUrl: 'https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?auto=format&fit=crop&q=80&w=800',
    isFavorite: false,
  },
  {
    name: 'Armindo',
    age: '2 anos',
    breed: 'Rex',
    sex: 'Macho',
    vaccinated: 'Sim',
    imageUrl: 'https://images.unsplash.com/photo-1609151354448-c4a53450c6e9?auto=format&fit=crop&q=80&w=800',
    isFavorite: false,
  },
];

@Injectable({
  providedIn: 'root',
})
export class PetService {
  private api = inject(ApiService);
  private readonly endpoint = 'pets';

  // Usando array mockado por enquanto
  getAll(): Observable<any[]> {
    // return this.api.get<Pet[]>(this.endpoint);
    return of(MOCK_PETS).pipe(delay(800));
  }

  // Novo método para a home
  getRecentPets(): Observable<any[]> {
    return of(MOCK_PETS.slice(0, 3)).pipe(delay(800));
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
