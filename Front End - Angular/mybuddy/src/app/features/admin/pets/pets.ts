import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../core/services/admin.service';
import { PetDenuncia } from '../../../core/models/admin.model';

@Component({
  selector: 'app-pets',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pets.html',
  styleUrl: './pets.scss'
})
export class Pets implements OnInit {
  denuncias: PetDenuncia[] = [];
  private adminService = inject(AdminService);

  ngOnInit() {
    this.adminService.getPetsDenuncias().subscribe(data => {
      this.denuncias = data;
    });
  }
}
