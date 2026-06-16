import { Component, OnInit, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { OngService } from "../../../core/services/ong.service";
import { MeuPetOng } from "../../../core/models/ong.model";
import { DebounceDirective } from "../../../shared/directives/debounce.directive";
import { PaginatorComponent } from "../../../shared/components/paginator/paginator.component";

@Component({
  selector: "app-meus-pets",
  standalone: true,
  imports: [CommonModule, DebounceDirective, PaginatorComponent],
  templateUrl: "./meus-pets.html",
  styleUrl: "./meus-pets.scss",
})
export class MeusPets implements OnInit {
  pets: MeuPetOng[] = [];
  private ongService = inject(OngService);

  currentPage = 1;
  totalPages = 5;

  ngOnInit() {
    this.ongService.buscarMeusPets().subscribe((data) => {
      this.pets = data;
    });
  }

  onSearch(term: string) {
    console.log(`[Meus Pets ONG] Pesquisando por: ${term}`);
  }

  onPageChange(page: number) {
    console.log(`[Meus Pets ONG] Mudando para página: ${page}`);
    this.currentPage = page;
  }
}
