import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PetshopService } from '../../../core/services/petshop.service';
import { Pedido } from '../../../core/models/petshop.model';
import { DebounceDirective } from '../../../shared/directives/debounce.directive';
import { PaginatorComponent } from '../../../shared/components/paginator/paginator.component';

@Component({
  selector: 'app-pedidos',
  standalone: true,
  imports: [CommonModule, DebounceDirective, PaginatorComponent],
  templateUrl: './pedidos.html',
  styleUrl: './pedidos.scss',
})
export class Pedidos implements OnInit {
  pedidos: Pedido[] = [];
  private petshopService = inject(PetshopService);

  currentPage = 1;
  totalPages = 5;

  ngOnInit() {
    this.petshopService.buscarPedidos().subscribe(data => {
      this.pedidos = data;
    });
  }

  onSearch(term: string) {
    console.log(`[Pedidos Petshop] Pesquisando por: ${term}`);
  }

  onPageChange(page: number) {
    console.log(`[Pedidos Petshop] Mudando para página: ${page}`);
    this.currentPage = page;
  }
}
