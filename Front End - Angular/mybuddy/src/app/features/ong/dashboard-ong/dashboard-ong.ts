import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard-ong',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-ong.html',
  styleUrl: './dashboard-ong.scss'
})
export class DashboardOng {
  metrics = [
    { title: 'Meus Animais', value: '45', icon: 'pets', color: '#ff7900' },
    { title: 'Adoções Realizadas', value: '128', icon: 'volunteer_activism', color: '#009688' },
    { title: 'Solicitações Pendentes', value: '12', icon: 'assignment_ind', color: '#e91e63' },
    { title: 'Eventos Ativos', value: '2', icon: 'event', color: '#3f51b5' }
  ];
}
