import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../core/services/admin.service';
import { AdminUsuario } from '../../../core/models/admin.model';
import { DebounceDirective } from '../../../shared/directives/debounce.directive';
import { PaginatorComponent } from '../../../shared/components/paginator/paginator.component';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, DebounceDirective, PaginatorComponent],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.scss'
})
export class Usuarios implements OnInit {
  usuarios: AdminUsuario[] = [];
  private adminService = inject(AdminService);

  currentPage = 1;
  totalPages = 5;

  ngOnInit() {
    this.adminService.getUsuarios().subscribe(data => {
      this.usuarios = data;
    });
  }

  onSearch(term: string) {
    console.log(`[Usuários Admin] Pesquisando por: ${term}`);
  }

  onPageChange(page: number) {
    console.log(`[Usuários Admin] Mudando para página: ${page}`);
    this.currentPage = page;
  }
}
