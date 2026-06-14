import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OngService } from '../../../core/services/ong.service';
import { EventoOng } from '../../../core/models/ong.model';
import { DebounceDirective } from '../../../shared/directives/debounce.directive';
import { PaginatorComponent } from '../../../shared/components/paginator/paginator.component';

@Component({
  selector: 'app-eventos-ong',
  standalone: true,
  imports: [CommonModule, DebounceDirective, PaginatorComponent],
  templateUrl: './eventos-ong.html',
  styleUrl: './eventos-ong.scss',
})
export class EventosOng implements OnInit {
  eventos: EventoOng[] = [];
  private ongService = inject(OngService);

  currentPage = 1;
  totalPages = 13;

  ngOnInit() {
    this.ongService.buscarEventos().subscribe(data => {
      this.eventos = data;
    });
  }

  onSearch(term: string) {
    console.log(`[Eventos ONG] Pesquisando eventos por: ${term}`);
    // Futuro: Filtrar a lista de 'eventos' localmente ou via chamada API
  }

  onPageChange(page: number) {
    console.log(`[Eventos ONG] Trocando para a página: ${page}`);
    this.currentPage = page;
    // Futuro: Buscar nova página via API
  }
}
