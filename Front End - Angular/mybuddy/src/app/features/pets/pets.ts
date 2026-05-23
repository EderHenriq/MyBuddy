import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { CardPetComponent } from '@shared/components/card-pet/card-pet.component';
import { Footer } from '@shared/components/footer/footer';
import { InfiniteScrollDirective } from '@shared/directives/infinite-scroll.directive';
import { DebounceDirective } from '@shared/directives/debounce.directive';
import { PetService } from '@core/services/pet.service';

interface PetListItem {
  name: string;
  age: string;
  breed: string;
  sex: string;
  vaccinated: string;
  imageUrl: string;
  isFavorite: boolean;
}

interface FilterGroup {
  title: string;
  options: string[];
}

@Component({
  selector: 'app-pets',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, CardPetComponent, Footer, InfiniteScrollDirective, DebounceDirective],
  templateUrl: './pets.html',
  styleUrl: './pets.scss',
})
export class Pets implements OnInit {
  private readonly petService = inject(PetService);
  private readonly fb = inject(FormBuilder);

  readonly isLoading = signal<boolean>(true);
  readonly pets = signal<PetListItem[]>([]);

  readonly filterGroups: FilterGroup[] = [
    {
      title: 'Espécie',
      options: ['Cachorro', 'Coelho', 'Gato', 'Pássaro'],
    },
    {
      title: 'Sexo',
      options: ['Fêmea', 'Macho'],
    },
    {
      title: 'Idade',
      options: ['Filhote (0-1 ano)', 'Jovem (1-3 anos)', 'Adulto (3-7 anos)', 'Idoso (+8 anos)'],
    },
    {
      title: 'Porte',
      options: ['Pequeno', 'Médio', 'Grande'],
    },
    {
      title: 'Características',
      options: ['Vacinado', 'Castrado', 'Vive com outros pets'],
    },
  ];

  filterForm = this.fb.group<Record<string, any>>({
    search: ['']
  });

  constructor() {
    this.filterGroups.forEach(group => {
      group.options.forEach(option => {
        this.filterForm.addControl(option, this.fb.control(false));
      });
    });
  }

  ngOnInit(): void {
    this.loadPets();

    this.filterForm.valueChanges
      .pipe(debounceTime(500), distinctUntilChanged())
      .subscribe(values => {
        console.log('[Página de Pets] Filtros alterados. Refazendo busca...', values);
        this.isLoading.set(true);
        setTimeout(() => {
          // Apenas simula o recarregamento na interface por enquanto
          this.isLoading.set(false);
        }, 800);
      });
  }

  private loadPets(): void {
    this.isLoading.set(true);
    this.petService.getAll().subscribe({
      next: data => {
        this.pets.set(data);
        this.isLoading.set(false);
      },
      error: err => {
        console.error('Erro ao buscar pets:', err);
        this.isLoading.set(false);
      },
    });
  }

  toggleFavorite(pet: PetListItem): void {
    pet.isFavorite = !pet.isFavorite;
  }

  onSearch(term: string): void {
    // Agora é controlado pelo filterForm.valueChanges
  }

  onLoadMore(): void {
    console.log(`[Página de Pets] Chegou ao fim da tela! Carregando mais pets...`);
    // Futuro: Fazer request para carregar a próxima página e fazer append no array 'pets'
  }
}
