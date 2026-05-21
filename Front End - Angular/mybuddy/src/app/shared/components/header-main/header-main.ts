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
  @ViewChildren('navLink') navLinks!: QueryList<ElementRef>;

  pillStyle: { left: string; width: string } = { left: '0px', width: '0px' };
  pillVisible = false;
  showHeader = true;
  hoveredIndex = -1;

  readonly links = [
    { path: '/home', label: 'Home' },
    { path: '/pets', label: 'Adotar' },
    { path: '/eventos', label: 'Eventos' },
    { path: '/produtos', label: 'Produtos' },
    { path: '/servicos', label: 'Serviços' },
  ];

  private routerSubscription!: Subscription;

  constructor() {
    const isBrowser = isPlatformBrowser(this.platform);
    if (isBrowser) {
      this.checkRoute(this.router.url);
      this.router.events.pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd)).subscribe(event => {
        this.checkRoute(event.urlAfterRedirects || event.url);
      });
    }
  }

  checkRoute(url: string): void {
    const publicPaths = ['/auth', '/styleguide', '/institucional'];
    const isRoot = url === '/' || url === '';
    const isPublic = publicPaths.some(path => url.startsWith(path));
    this.showHeader = !isRoot && !isPublic;
  }

  ngAfterViewInit(): void {
    if (!isPlatformBrowser(this.platform)) {
      return;
    }

    this.updatePillPosition();
    this.cdr.detectChanges();

    this.routerSubscription = this.router.events.pipe(filter(event => event instanceof NavigationEnd)).subscribe(() => {
      setTimeout(() => {
        this.updatePillPosition();
        this.cdr.detectChanges();
      }, 0);
    });
  }

  ngOnDestroy(): void {
    this.routerSubscription?.unsubscribe();
  }

  @HostListener('window:resize')
  onResize(): void {
    if (!isPlatformBrowser(this.platform)) {
      return;
    }
    this.updatePillPosition();
  }

  setHoverIndex(index: number, event: HTMLElement): void {
    this.hoveredIndex = index;
    this.movePill(event);
  }

  resetPill(): void {
    this.hoveredIndex = -1;
    this.updatePillPosition();
  }

  updatePillPosition(): void {
    if (!isPlatformBrowser(this.platform)) {
      return;
    }

    if (this.hoveredIndex !== -1) {
      const hoveredElement = this.navLinks.toArray()[this.hoveredIndex];
      if (hoveredElement) {
        this.movePill(hoveredElement.nativeElement);
        return;
      }
    }

    const activeElement = this.navLinks.find(link => link.nativeElement.classList.contains('active'));

    if (!activeElement) {
      this.pillVisible = false;
      this.cdr.detectChanges();
      return;
    }

    this.movePill(activeElement.nativeElement);
  }

  onLinkClick(event: HTMLElement): void {
    this.movePill(event);
  }

  private movePill(element: HTMLElement): void {
    if (!isPlatformBrowser(this.platform)) return;
    const nav = element.closest('ul') as HTMLElement;
    if (!nav) return;
    const navRect = nav.getBoundingClientRect();
    const linkRect = element.getBoundingClientRect();

    this.pillStyle = {
      left: `${linkRect.left - navRect.left}px`,
      width: `${linkRect.width}px`,
    };
    this.pillVisible = true;
    this.cdr.detectChanges();
  }
}
