import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../core/services/admin.service';
import { Ticket } from '../../../core/models/admin.model';
import { DebounceDirective } from '../../../shared/directives/debounce.directive';
import { PaginatorComponent } from '../../../shared/components/paginator/paginator.component';

@Component({
  selector: 'app-suporte',
  standalone: true,
  imports: [CommonModule, DebounceDirective, PaginatorComponent],
  templateUrl: './suporte.html',
  styleUrl: './suporte.scss',
})
export class Suporte implements OnInit {
  tickets: Ticket[] = [];
  private adminService = inject(AdminService);

  currentPage = 1;
  totalPages = 5;

  ngOnInit() {
    this.adminService.buscarTicketsSuporte().subscribe(data => {
      this.tickets = data;
    });
  }

  onSearch(term: string) {
    console.log(`[Suporte Admin] Pesquisando por: ${term}`);
  }

  onPageChange(page: number) {
    console.log(`[Suporte Admin] Mudando para página: ${page}`);
    this.currentPage = page;
  }
}
