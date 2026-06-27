import { Injectable, inject } from '@angular/core';
import { ApiService } from './api.service';
import { Pet } from '../models/pet.model';
import { Observable, of, delay, catchError, take } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PetService {
  private api = inject(ApiService);
  private readonly endpoint = 'pets';

  buscarTodos(): Observable<Pet[]> {
    return this.api.get<Pet[]>(this.endpoint);
  }

  buscarRecentes(): Observable<any> {
    return this.api.get<any>(`${this.endpoint}?sort=id,desc&size=3`).pipe(
      take(1),
      catchError(() =>
        of({
          content: [
            {
              id: '1',
              name: 'Kira',
              age: 5,
              breed: 'Vira Lata',
              gender: 'Fêmea',
              isVaccinated: true,
              imageUrl: '/assets/placeholders/pets/Kira.jpg',
            },
            {
              id: '2',
              name: 'Pêssego',
              age: 2,
              breed: 'Vira Lata',
              gender: 'Macho',
              isVaccinated: true,
              imageUrl: '/assets/placeholders/pets/Pessego.jpg',
            },
            {
              id: '3',
              name: 'Jade',
              age: 1,
              breed: 'Mini Lop',
              gender: 'Fêmea',
              isVaccinated: true,
              imageUrl: '/assets/placeholders/pets/Armindo.png',
            },
          ],
        }),
      ),
    );
  }

  buscarPorId(id: string): Observable<Pet> {
    return this.api.get<Pet>(`${this.endpoint}/${id}`);
  }

  buscarPorDono(donoId: string): Observable<Pet[]> {
    return this.api.get<Pet[]>(`${this.endpoint}/owner/${donoId}`);
  }

  criar(pet: Partial<Pet>): Observable<Pet> {
    return this.api.post<Pet>(this.endpoint, pet);
  }

  atualizar(id: string, pet: Partial<Pet>): Observable<Pet> {
    return this.api.put<Pet>(`${this.endpoint}/${id}`, pet);
  }

  deletar(id: string): Observable<void> {
    return this.api.delete<void>(`${this.endpoint}/${id}`);
  }
}
