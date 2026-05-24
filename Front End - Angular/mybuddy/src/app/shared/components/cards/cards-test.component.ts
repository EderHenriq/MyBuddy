import { Cards } from './cards';
import { Component } from '@angular/core';
import { Pet } from '../../../core/models/pet.model';

@Component({
  selector: 'app-cards-test',
  standalone: true,
  imports: [Cards], // Injete o seu componente de card aqui
  template: `
    <div
      style="padding: var(--spacing-lg); display: flex; flex-direction: column; gap: var(--spacing-md); align-items: center; background-color: var(--surface-ground); min-height: 100vh;"
    >
      <h1>Ambiente de Teste do Card</h1>

      <div style="display: flex; gap: var(--spacing-md); flex-wrap: wrap; justify-content: center;">
        <app-cards
          [pet]="petMock"
          [showFavoriteButton]="true"
          [showTopHeart]="true"
          [isFavorite]="false"
          badgeText="Adoção"
          badgeType="adoption"
        ></app-cards>

        <app-cards [pet]="petMock" [ongMode]="true" badgeText="Adotado" badgeType="adopted"></app-cards>
      </div>
    </div>
  `,
})
export class CardsTestComponent {
  petMock: Pet = {
    id: '1',
    ownerId: 'ong-1',
    name: 'Bolinha',
    species: 'Cachorro',
    breed: 'Golden Retriever',
    gender: 'Macho',
    age: 2,
    weight: 25,
    imageUrl: 'https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&q=80&w=600',
    isVaccinated: true,
  };
}
