import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterModule, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SessionService } from '../../services/session.service';
import { AuthService } from '../../services/auth.service';
import { Role } from '../../models/role.model';

interface MenuItem {
  label: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-dashboard-layout',
  standalone: true,
  imports: [RouterOutlet, RouterModule, CommonModule],
  templateUrl: './dashboard-layout.html',
  styleUrl: './dashboard-layout.scss'
})
export class DashboardLayout {
  portalName: string = 'Admin';
  roleName: string = 'Administrador';
  menuItems: MenuItem[] = [];

  constructor(private router: Router, private sessionService: SessionService) {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.updateMenuBasedOnRoute(this.router.url);
      }
    });

    this.sessionService.userRole$.subscribe(role => {
      // Aqui poderíamos forçar o menu baseado apenas na role, 
      // mas mantemos baseado na URL para permitir que o ADMIN consiga visualizar e testar 
      // os outros painéis (como ong-panel e petshop-panel) tranquilamente.
      // O roleName do header agora reflete a role real:
      if (role === Role.ADMIN) this.roleName = 'Administrador Geral';
      if (role === Role.ONG) this.roleName = 'Gestor de ONG';
      if (role === Role.PETSHOP) this.roleName = 'Lojista';
      if (role === Role.USER) this.roleName = 'Adotante';
    });
    
    // Config inicial
    this.updateMenuBasedOnRoute(this.router.url);
  }

  private updateMenuBasedOnRoute(url: string) {
    if (url.startsWith('/ong-panel')) {
      this.portalName = 'ONG';
      this.roleName = 'Gestor de ONG';
      this.menuItems = [
        { label: 'Visão Geral', icon: 'dashboard', route: '/ong-panel/dashboard' },
        { label: 'Meus Animais', icon: 'pets', route: '/ong-panel/pets' },
        { label: 'Solicitações de Adoção', icon: 'assignment_ind', route: '/ong-panel/solicitacoes' },
        { label: 'Eventos de Adoção', icon: 'event', route: '/ong-panel/eventos' }
      ];
    } else if (url.startsWith('/petshop-panel')) {
      this.portalName = 'Petshop';
      this.roleName = 'Lojista';
      this.menuItems = [
        { label: 'Painel de Vendas', icon: 'storefront', route: '/petshop-panel/dashboard' },
        { label: 'Meus Produtos', icon: 'inventory_2', route: '/petshop-panel/produtos' },
        { label: 'Pedidos', icon: 'shopping_bag', route: '/petshop-panel/pedidos' },
        { label: 'Chat de Clientes', icon: 'chat', route: '/petshop-panel/chat' }
      ];
    } else {
      // Default to Admin
      this.portalName = 'Admin';
      this.roleName = 'Administrador Geral';
      this.menuItems = [
        { label: 'Dashboard', icon: 'dashboard', route: '/admin/dashboard' },
        { label: 'ONGs e Petshops', icon: 'storefront', route: '/admin/ongs' },
        { label: 'Usuários', icon: 'people', route: '/admin/usuarios' },
        { label: 'Moderação de Pets', icon: 'pets', route: '/admin/pets' },
        { label: 'Suporte', icon: 'support_agent', route: '/admin/suporte' },
        { label: 'Configurações', icon: 'settings', route: '/admin/configuracoes' }
      ];
    }
  }
}
