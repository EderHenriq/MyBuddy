import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { CardPetComponent } from '@shared/components/card-pet/card-pet.component';
import { Footer } from '@shared/components/footer/footer';
import { InfiniteScrollDirective } from '@shared/directives/infinite-scroll.directive';
import { DebounceDirective } from '@shared/directives/debounce.directive';
import { PetService } from '@core/services/pet.service';

interface ItemListaPet {
  nome: string;
  idade: number;
  raca: string;
  sexo: string;
  vacinado: boolean;
  castrado: boolean;
  porte: string;
  cor: string;
  pelagem: string;
  fotosUrls?: string[];
  favorito: boolean;
}

interface GrupoFiltro {
  titulo: string;
  opcoes: string[];
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

  readonly carregando = signal<boolean>(true);
  readonly animais = signal<ItemListaPet[]>([]);

  readonly gruposFiltro: GrupoFiltro[] = [
    {
      titulo: 'Espécie',
      opcoes: ['Cachorro', 'Coelho', 'Gato', 'Pássaro'],
    },
    {
      titulo: 'Sexo',
      opcoes: ['Fêmea', 'Macho'],
    },
    {
      titulo: 'Idade',
      opcoes: ['Filhote (0-1 ano)', 'Jovem (1-3 anos)', 'Adulto (3-7 anos)', 'Idoso (+8 anos)'],
    },
    {
      titulo: 'Porte',
      opcoes: ['Pequeno', 'Médio', 'Grande'],
    },
    {
      titulo: 'Características',
      opcoes: ['Vacinado', 'Castrado', 'Vive com outros pets'],
    },
  ];

  formularioFiltro: FormGroup = this.fb.group({
    pesquisa: ['']
  });

  constructor() {
    this.gruposFiltro.forEach(grupo => {
      grupo.opcoes.forEach(opcao => {
        this.formularioFiltro.addControl(opcao, this.fb.control(false));
      });
    });
  }

  ngOnInit(): void {
    this.carregarAnimais();

    this.formularioFiltro.valueChanges
      .pipe(debounceTime(500), distinctUntilChanged())
      .subscribe(valores => {
        console.log('[Página de Pets] Filtros alterados. Refazendo busca...', valores);
        this.carregando.set(true);
        setTimeout(() => {
          this.carregando.set(false);
        }, 800);
      });
  }

  private carregarAnimais(): void {
    this.carregando.set(true);
    this.petService.buscarTodos().subscribe({
      next: (dados: any) => {
        this.animais.set(dados.content || []);
        this.carregando.set(false);
      },
      error: erro => {
        console.error('Erro ao buscar pets:', erro);
        this.carregando.set(false);
      },
    });
  }

  alternarFavorito(pet: ItemListaPet): void {
    pet.favorito = !pet.favorito;
  }

  aoPesquisar(termo: string): void {
  }

  aoCarregarMais(): void {
    console.log(`[Página de Pets] Chegou ao fim da tela! Carregando mais pets...`);
  }
}
