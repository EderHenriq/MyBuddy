import { Component, OnInit, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { AdminService } from "../../../core/services/admin.service";
import { Ticket } from "../../../core/models/admin.model";
import { DebounceDirective } from "../../../shared/directives/debounce.directive";
import { PaginatorComponent } from "../../../shared/components/paginator/paginator.component";

@Component({
  selector: "app-suporte",
  standalone: true,
  imports: [CommonModule, DebounceDirective, PaginatorComponent],
  templateUrl: "./suporte.html",
  styleUrl: "./suporte.scss",
})
export class Suporte implements OnInit {
  tickets: Ticket[] = [];
  private adminService = inject(AdminService);

  currentPage = 1;
  totalPages = 1;

  ngOnInit() {
    this.adminService.buscarTicketsSuporte().subscribe((data) => {
      this.tickets = data;
      this.totalPages = Math.ceil(data.length / 10) || 1;
    });
  }

  get ticketsPaginados() {
    const startIndex = (this.currentPage - 1) * 10;
    return this.tickets.slice(startIndex, startIndex + 10);
  }

  onSearch(term: string) {
    console.log(`[Suporte Admin] Pesquisando por: ${term}`);
  }

  onPageChange(page: number) {
    console.log(`[Suporte Admin] Mudando para página: ${page}`);
    this.currentPage = page;
  }
}
