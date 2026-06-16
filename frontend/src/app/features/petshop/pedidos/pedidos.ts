import { Component, OnInit, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { PetshopService } from "../../../core/services/petshop.service";
import { PedidoService } from "../../../core/services/pedido.service";
import { Pedido } from "../../../core/models/petshop.model";
import { DebounceDirective } from "../../../shared/directives/debounce.directive";
import { PaginatorComponent } from "../../../shared/components/paginator/paginator.component";

@Component({
  selector: "app-pedidos",
  standalone: true,
  imports: [CommonModule, DebounceDirective, PaginatorComponent],
  templateUrl: "./pedidos.html",
  styleUrl: "./pedidos.scss",
})
export class Pedidos implements OnInit {
  pedidos: any[] = [];
  private petshopService = inject(PetshopService);
  private pedidoService = inject(PedidoService);

  currentPage = 1;
  totalPages = 1;

  ngOnInit() {
    this.carregarPedidos();
  }

  carregarPedidos() {
    this.petshopService.buscarPedidos().subscribe((data) => {
      this.pedidos = data;
    });
  }

  alterarStatus(id: number, novoStatus: string) {
    this.pedidoService.atualizarStatus(id, novoStatus).subscribe({
      next: () => {
        this.carregarPedidos();
      },
      error: (err) => console.error(err)
    });
  }

  onSearch(term: string) {
    console.log(`[Pedidos Petshop] Pesquisando por: ${term}`);
    if (term) {
      this.pedidos = this.pedidos.filter(p => p.id.toString().includes(term));
    } else {
      this.carregarPedidos();
    }
  }

  onPageChange(page: number) {
    console.log(`[Pedidos Petshop] Mudando para página: ${page}`);
    this.currentPage = page;
  }
}
