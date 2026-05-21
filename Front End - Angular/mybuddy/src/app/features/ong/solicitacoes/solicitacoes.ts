import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OngService } from '../../../core/services/ong.service';
import { SolicitacaoAdocao } from '../../../core/models/ong.model';

@Component({
  selector: 'app-solicitacoes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './solicitacoes.html',
  styleUrl: './solicitacoes.scss'
})
export class Solicitacoes implements OnInit {
  pedidos: SolicitacaoAdocao[] = [];
  private ongService = inject(OngService);

  ngOnInit() {
    this.ongService.getSolicitacoes().subscribe(data => {
      this.pedidos = data;
    });
  }
}
