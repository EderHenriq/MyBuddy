import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../core/services/admin.service';
import { AdminUsuario } from '../../../core/models/admin.model';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.scss'
})
export class Usuarios implements OnInit {
  usuarios: AdminUsuario[] = [];
  private adminService = inject(AdminService);

  ngOnInit() {
    this.adminService.getUsuarios().subscribe(data => {
      this.usuarios = data;
    });
  }
}
