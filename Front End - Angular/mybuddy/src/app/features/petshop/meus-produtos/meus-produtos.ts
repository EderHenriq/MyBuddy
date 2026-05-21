import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PetshopService } from '../../../core/services/petshop.service';
import { ProdutoPetshop } from '../../../core/models/petshop.model';

@Component({
  selector: 'app-meus-produtos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './meus-produtos.html',
  styleUrl: './meus-produtos.scss'
})
export class MeusProdutos implements OnInit {
  produtos: ProdutoPetshop[] = [];
  private petshopService = inject(PetshopService);

  ngOnInit() {
    this.petshopService.getProdutos().subscribe(data => {
      this.produtos = data;
    });
  }
}
