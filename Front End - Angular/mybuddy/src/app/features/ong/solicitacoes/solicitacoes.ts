import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OngService } from '../../../core/services/ong.service';
import { SolicitacaoAdocao } from '../../../core/models/ong.model';
import { DebounceDirective } from '../../../shared/directives/debounce.directive';
import { PaginatorComponent } from '../../../shared/components/paginator/paginator.component';

@Component({
  selector: 'app-solicitacoes',
  standalone: true,
  imports: [CommonModule, DebounceDirective, PaginatorComponent],
  templateUrl: './solicitacoes.html',
  styleUrl: './solicitacoes.scss'
})
export class Solicitacoes implements OnInit {
  pedidos: SolicitacaoAdocao[] = [];
  private ongService = inject(OngService);

  currentPage = 1;
  totalPages = 5;

  ngOnInit() {
    this.ongService.getSolicitacoes().subscribe(data => {
      this.pedidos = data;
    });
  }

  onSearch(term: string) {
    console.log(`[Solicitações ONG] Pesquisando por: ${term}`);
  }

  onPageChange(page: number) {
    console.log(`[Solicitações ONG] Mudando para página: ${page}`);
    this.currentPage = page;
  }
}
