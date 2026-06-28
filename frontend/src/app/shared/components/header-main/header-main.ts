import {
  Component,
  ElementRef,
  QueryList,
  ViewChildren,
  AfterViewInit,
  OnDestroy,
  HostListener,
  inject,
  PLATFORM_ID,
  ChangeDetectorRef,
} from '@angular/core';
import { Router, NavigationEnd, RouterModule } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { filter, Subscription } from 'rxjs';
import { SessionService } from '../../../core/services/session.service';
import { Role } from '../../../core/models/role.model';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-header-main',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header-main.html',
  styleUrl: './header-main.scss',
})
export class HeaderMain implements AfterViewInit, OnDestroy {
  private router = inject(Router);
  private platform = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);
  private sessionService = inject(SessionService);
  private notificationService = inject(NotificationService);
  @ViewChildren('navLink') navLinks!: QueryList<ElementRef>;

  estiloPill: { left: string; width: string } = { left: '0px', width: '0px' };
  pillVisivel = false;
  notificacoesAbertas = false;

  readonly Role = Role;
  readonly papelUsuario = this.sessionService.userRole;
  readonly notificacoes = this.notificationService.notificacoes;
  readonly contadorNaoLidas = this.notificationService.totalNaoLidas;

  readonly rotaPainel = (): string => {
    const role = this.papelUsuario();
    if (role === Role.ADMIN) return '/admin/dashboard';
    if (role === Role.ONG) return '/ong-panel/dashboard';
    if (role === Role.PETSHOP) return '/petshop-panel/dashboard';

    return '';
  };

  readonly links = [
    { caminho: '/home', rotulo: 'Home' },
    { caminho: '/pets', rotulo: 'Adotar' },
    { caminho: '/eventos', rotulo: 'Eventos' },
    { caminho: '/produtos', rotulo: 'Produtos' },
    { caminho: '/servicos', rotulo: 'Serviços' },
    { caminho: '/doacoes', rotulo: 'Doações' },
  ];

  private routerSubscription!: Subscription;

  alternarNotificacoes(): void {
    this.notificacoesAbertas = !this.notificacoesAbertas;
  }

  marcarComoLida(id: string, event: Event): void {
    event.stopPropagation();
    this.notificationService.marcarComoLida(id);
  }

  marcarTodasComoLidas(event: Event): void {
    event.stopPropagation();
    this.notificationService.marcarTodasComoLidas();
  }

  @HostListener('document:click', ['$event'])
  aoClicarFora(event: Event): void {
    const targetElement = event.target as HTMLElement;
    if (this.notificacoesAbertas && !targetElement.closest('.notifications-btn')) {
      this.notificacoesAbertas = false;
    }
  }

  ngAfterViewInit(): void {
    if (!isPlatformBrowser(this.platform)) return;

    setTimeout(() => {
      this.atualizarPosicaoPill();
      this.cdr.detectChanges();
    }, 0);

    this.routerSubscription = this.router.events.pipe(filter(event => event instanceof NavigationEnd)).subscribe(() => {
      setTimeout(() => {
        this.atualizarPosicaoPill();
        this.cdr.detectChanges();
      }, 0);
    });
  }

  ngOnDestroy(): void {
    this.routerSubscription?.unsubscribe();
  }

  @HostListener('window:resize')
  aoRedimensionar(): void {
    if (!isPlatformBrowser(this.platform)) {
      return;
    }
    this.atualizarPosicaoPill();
  }

  atualizarPosicaoPill(): void {
    if (!isPlatformBrowser(this.platform)) {
      return;
    }

    const activeElement = this.navLinks.find(link => link.nativeElement.classList.contains('active'));

    if (!activeElement) {
      this.pillVisivel = false;
      this.cdr.detectChanges();
      return;
    }

    this.moverPill(activeElement.nativeElement);
  }

  aoClicarLink(event: HTMLElement): void {
    this.moverPill(event);
  }

  private moverPill(element: HTMLElement): void {
    if (!isPlatformBrowser(this.platform)) return;
    const nav = element.closest('ul') as HTMLElement;
    if (!nav) return;
    const navRect = nav.getBoundingClientRect();
    const linkRect = element.getBoundingClientRect();

    this.estiloPill = {
      left: `${linkRect.left - navRect.left}px`,
      width: `${linkRect.width}px`,
    };
    this.pillVisivel = true;
    this.cdr.detectChanges();
  }
}
