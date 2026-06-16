import { Component, OnInit, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { PetshopService } from "../../../core/services/petshop.service";
import { Produto } from "../../../core/models/petshop.model";
import { DebounceDirective } from "../../../shared/directives/debounce.directive";
import { PaginatorComponent } from "../../../shared/components/paginator/paginator.component";

@Component({
  selector: "app-meus-produtos",
  standalone: true,
  imports: [CommonModule, DebounceDirective, PaginatorComponent],
  templateUrl: "./meus-produtos.html",
  styleUrl: "./meus-produtos.scss",
})
export class MeusProdutos implements OnInit {
  produtos: Produto[] = [];
  private petshopService = inject(PetshopService);

  currentPage = 1;
  totalPages = 5;

  ngOnInit() {
    this.petshopService.buscarProdutos().subscribe((data) => {
      this.produtos = data;
    });
  }

  onSearch(term: string) {
    console.log(`[Produtos Petshop] Pesquisando por: ${term}`);
  }

  onPageChange(page: number) {
    console.log(`[Produtos Petshop] Mudando para página: ${page}`);
    this.currentPage = page;
  }
}
