import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

interface MetricCard {
  title: string;
  value: string;
  icon: string;
  trend: 'up' | 'down';
  trendValue: string;
  color: string;
}

interface PendingApproval {
  id: number;
  name: string;
  type: 'ONG' | 'Petshop';
  date: string;
  status: 'Pendente';
}

import { DebounceDirective } from '../../../shared/directives/debounce.directive';
import { PaginatorComponent } from '../../../shared/components/paginator/paginator.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, DebounceDirective, PaginatorComponent],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {
  currentPage = 1;
  totalPages = 1;

  metrics: MetricCard[] = [
    {
      title: 'Adotantes Registrados',
      value: '1,245',
      icon: 'person',
      trend: 'up',
      trendValue: '+12%',
      color: '#3f51b5',
    },
    {
      title: 'ONGs Parceiras',
      value: '84',
      icon: 'volunteer_activism',
      trend: 'up',
      trendValue: '+3%',
      color: '#009688',
    },
    {
      title: 'Pets Adotados',
      value: '342',
      icon: 'pets',
      trend: 'up',
      trendValue: '+18%',
      color: '#ff7900',
    },
    {
      title: 'Vendas no Marketplace',
      value: 'R$ 15.4K',
      icon: 'storefront',
      trend: 'down',
      trendValue: '-2%',
      color: '#e91e63',
    },
  ];

  pendingApprovals: PendingApproval[] = [
    {
      id: 101,
      name: 'Abrigo Amigos de Pata',
      type: 'ONG',
      date: '21 Mai, 2026',
      status: 'Pendente',
    },
    {
      id: 102,
      name: 'Boutique Animal Centro',
      type: 'Petshop',
      date: '20 Mai, 2026',
      status: 'Pendente',
    },
    {
      id: 103,
      name: 'Coração Peludo Resgates',
      type: 'ONG',
      date: '19 Mai, 2026',
      status: 'Pendente',
    },
    {
      id: 104,
      name: 'Petz Avenida Sul',
      type: 'Petshop',
      date: '19 Mai, 2026',
      status: 'Pendente',
    },
  ];

  ngOnInit() {
    this.recalcularTotalPages();
  }

  recalcularTotalPages() {
    this.totalPages = Math.ceil(this.pendingApprovals.length / 10) || 1;
  }

  get pendingApprovalsPaginados() {
    const startIndex = (this.currentPage - 1) * 10;
    return this.pendingApprovals.slice(startIndex, startIndex + 10);
  }

  approve(item: PendingApproval) {
    console.log(`Aprovando ${item.name}`);
    this.pendingApprovals = this.pendingApprovals.filter(p => p.id !== item.id);
    this.recalcularTotalPages();
  }

  reject(item: PendingApproval) {
    console.log(`Rejeitando ${item.name}`);
    this.pendingApprovals = this.pendingApprovals.filter(p => p.id !== item.id);
    this.recalcularTotalPages();
  }

  onSearch(term: string) {
    console.log(`[Dashboard Admin] Pesquisando por: ${term}`);
  }

  onPageChange(page: number) {
    console.log(`[Dashboard Admin] Mudando para página: ${page}`);
    this.currentPage = page;
  }
}
