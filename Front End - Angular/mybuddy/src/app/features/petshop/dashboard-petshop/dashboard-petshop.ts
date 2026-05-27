import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard-petshop',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-petshop.html',
  styleUrl: './dashboard-petshop.scss',
})
export class DashboardPetshop {
  metrics = [
    { title: 'Vendas do Mês', value: 'R$ 8.450', icon: 'payments', color: '#009688' },
    { title: 'Pedidos Novos', value: '14', icon: 'shopping_bag', color: '#ff7900' },
    { title: 'Produtos Ativos', value: '112', icon: 'inventory_2', color: '#3f51b5' },
    { title: 'Mensagens', value: '5', icon: 'chat', color: '#e91e63' },
  ];
}
