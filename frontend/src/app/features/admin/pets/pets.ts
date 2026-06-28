import { Component, OnInit, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { AdminService } from "../../../core/services/admin.service";
import { PetDenuncia } from "../../../core/models/admin.model";
import { DebounceDirective } from "../../../shared/directives/debounce.directive";
import { PaginatorComponent } from "../../../shared/components/paginator/paginator.component";

@Component({
  selector: "app-pets",
  standalone: true,
  imports: [CommonModule, DebounceDirective, PaginatorComponent],
  templateUrl: "./pets.html",
  styleUrl: "./pets.scss",
})
export class Pets implements OnInit {
  denuncias: PetDenuncia[] = [];
  private adminService = inject(AdminService);

  currentPage = 1;
  totalPages = 1;

  ngOnInit() {
    this.adminService.buscarPetsDenunciados().subscribe((data) => {
      this.denuncias = data;
      this.totalPages = Math.ceil(data.length / 10) || 1;
    });
  }

  get denunciasPaginadas() {
    const startIndex = (this.currentPage - 1) * 10;
    return this.denuncias.slice(startIndex, startIndex + 10);
  }

  onSearch(term: string) {
    console.log(`[Pets Admin] Pesquisando por: ${term}`);
  }

  onPageChange(page: number) {
    console.log(`[Pets Admin] Mudando para página: ${page}`);
    this.currentPage = page;
  }
}
