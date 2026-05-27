import { Component, inject, HostListener } from '@angular/core';
import { RouterOutlet, RouterModule, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SessionService } from '../../services/session.service';
import { AuthService } from '../../services/auth.service';
import { Role } from '../../models/role.model';
import { NotificationService } from '../../services/notification.service';
import { NotificacaoApp } from '../../models/notification.model';
import { DebounceDirective } from '../../../shared/directives/debounce.directive';

interface MenuItem {
  label: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-dashboard-layout',
  standalone: true,
  imports: [RouterOutlet, RouterModule, CommonModule, DebounceDirective],
  templateUrl: './dashboard-layout.html',
  styleUrl: './dashboard-layout.scss',
})
export class DashboardLayout {
  portalName = 'Admin';
  roleName = 'Administrador';
  menuItems: MenuItem[] = [];

  private notificationService = inject(NotificationService);
  notifications: NotificacaoApp[] = [];
  unreadCount = 0;
  isNotificationsOpen = false;

  constructor(
    private router: Router,
    private sessionService: SessionService,
  ) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.updateMenuBasedOnRoute(this.router.url);
      }
    });

    this.sessionService.userRole$.subscribe(role => {
      if (role === Role.ADMIN) this.roleName = 'Administrador Geral';
      if (role === Role.ONG) this.roleName = 'Gestor de ONG';
      if (role === Role.PETSHOP) this.roleName = 'Lojista';
      if (role === Role.USER) this.roleName = 'Adotante';
    });

    this.updateMenuBasedOnRoute(this.router.url);

    this.notificationService.notificacoes$.subscribe((notifs: any) => {
      this.notifications = notifs;
    });

    this.notificationService.buscarContagemNaoLidas().subscribe((count: any) => {
      this.unreadCount = count;
    });
  }

  toggleNotifications(): void {
    this.isNotificationsOpen = !this.isNotificationsOpen;
  }

  onSearch(term: string): void {
    console.log(`[Dashboard Layout] Buscando globalmente por: ${term}`);
  }

  markAsRead(id: string, event: Event): void {
    event.stopPropagation();
    this.notificationService.marcarComoLida(id);
  }

  markAllAsRead(event: Event): void {
    event.stopPropagation();
    this.notificationService.marcarTodasComoLidas();
  }

  @HostListener('document:click', ['$event'])
  onClickOutside(event: Event): void {
    const targetElement = event.target as HTMLElement;
    if (this.isNotificationsOpen && !targetElement.closest('.icon-btn')) {
      this.isNotificationsOpen = false;
    }
  }

  private updateMenuBasedOnRoute(url: string) {
    if (url.startsWith('/ong-panel')) {
      this.portalName = 'ONG';
      this.roleName = 'Gestor de ONG';
      this.menuItems = [
        { label: 'Visão Geral', icon: 'dashboard', route: '/ong-panel/dashboard' },
        { label: 'Meus Animais', icon: 'pets', route: '/ong-panel/pets' },
        { label: 'Solicitações de Adoção', icon: 'assignment_ind', route: '/ong-panel/solicitacoes' },
        { label: 'Eventos de Adoção', icon: 'event', route: '/ong-panel/eventos' },
      ];
    } else if (url.startsWith('/petshop-panel')) {
      this.portalName = 'Petshop';
      this.roleName = 'Lojista';
      this.menuItems = [
        { label: 'Painel de Vendas', icon: 'storefront', route: '/petshop-panel/dashboard' },
        { label: 'Meus Produtos', icon: 'inventory_2', route: '/petshop-panel/produtos' },
        { label: 'Pedidos', icon: 'shopping_bag', route: '/petshop-panel/pedidos' },
        { label: 'Chat de Clientes', icon: 'chat', route: '/petshop-panel/chat' },
      ];
    } else {
      this.portalName = 'Admin';
      this.roleName = 'Administrador Geral';
      this.menuItems = [
        { label: 'Dashboard', icon: 'dashboard', route: '/admin/dashboard' },
        { label: 'ONGs e Petshops', icon: 'storefront', route: '/admin/ongs' },
        { label: 'Usuários', icon: 'people', route: '/admin/usuarios' },
        { label: 'Moderação de Pets', icon: 'pets', route: '/admin/pets' },
        { label: 'Suporte', icon: 'support_agent', route: '/admin/suporte' },
        { label: 'Configurações', icon: 'settings', route: '/admin/configuracoes' },
      ];
    }
  }
}
