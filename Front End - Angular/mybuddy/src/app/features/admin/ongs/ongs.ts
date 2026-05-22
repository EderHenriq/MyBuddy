import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../core/services/admin.service';
import { Parceria } from '../../../core/models/admin.model';
import { DebounceDirective } from '../../../shared/directives/debounce.directive';
import { PaginatorComponent } from '../../../shared/components/paginator/paginator.component';

@Component({
  selector: 'app-ongs',
  standalone: true,
  imports: [CommonModule, DebounceDirective, PaginatorComponent],
  templateUrl: './ongs.html',
  styleUrl: './ongs.scss',
})
export class Ongs implements OnInit {
  parcerias: Parceria[] = [];
  private adminService = inject(AdminService);

  currentPage = 1;
  totalPages = 5;

  ngOnInit() {
    this.adminService.getOngs().subscribe(data => {
      this.parcerias = data;
    });
  }

  onSearch(term: string) {
    console.log(`[ONGs Admin] Pesquisando por: ${term}`);
  }

  onPageChange(page: number) {
    console.log(`[ONGs Admin] Mudando para página: ${page}`);
    this.currentPage = page;
  }
}
