import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../core/services/admin.service';
import { Parceria } from '../../../core/models/admin.model';

@Component({
  selector: 'app-ongs',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ongs.html',
  styleUrl: './ongs.scss'
})
export class Ongs implements OnInit {
  parcerias: Parceria[] = [];
  private adminService = inject(AdminService);

  ngOnInit() {
    this.adminService.getOngs().subscribe(data => {
      this.parcerias = data;
    });
  }
}
