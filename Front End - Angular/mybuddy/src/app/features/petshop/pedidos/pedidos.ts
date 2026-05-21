import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PetshopService } from '../../../core/services/petshop.service';
import { PedidoPetshop } from '../../../core/models/petshop.model';

@Component({
  selector: 'app-pedidos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pedidos.html',
  styleUrl: './pedidos.scss'
})
export class Pedidos implements OnInit {
  pedidos: PedidoPetshop[] = [];
  private petshopService = inject(PetshopService);

  ngOnInit() {
    this.petshopService.getPedidos().subscribe(data => {
      this.pedidos = data;
    });
  }
}
