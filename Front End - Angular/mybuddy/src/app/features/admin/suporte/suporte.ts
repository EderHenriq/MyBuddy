import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../core/services/admin.service';
import { Ticket } from '../../../core/models/admin.model';

@Component({
  selector: 'app-suporte',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './suporte.html',
  styleUrl: './suporte.scss'
})
export class Suporte implements OnInit {
  tickets: Ticket[] = [];
  private adminService = inject(AdminService);

  ngOnInit() {
    this.adminService.getTicketsSuporte().subscribe(data => {
      this.tickets = data;
    });
  }
}
