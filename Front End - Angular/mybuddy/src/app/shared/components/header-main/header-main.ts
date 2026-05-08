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

  readonly links = [
    { path: '/home', label: 'Home' },
    { path: '/pets', label: 'Adotar' },
    { path: '/eventos', label: 'Eventos' },
    { path: '/produtos', label: 'Produtos' },
    { path: '/comunidade', label: 'Comunidade' },
  ];

  private routerSubscription!: Subscription;

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
    this.routerSubscription.unsubscribe();
  }

  @HostListener('window:resize')
  onResize(): void {
    if (!isPlatformBrowser(this.platform)) {
      return;
    }
    this.updatePillPosition();
  }

  updatePillPosition(): void {
    if (!isPlatformBrowser(this.platform)) {
      return;
    }

    const activeElement = this.navLinks.find(link => link.nativeElement.classList.contains('active'));

    if (!activeElement) {
      this.pillVisible = false;
      return;
    }

    const nav = activeElement.nativeElement.closest('ul') as HTMLElement;
    if (!nav) return;

    const navRect = nav.getBoundingClientRect();
    const linkRect = activeElement.nativeElement.getBoundingClientRect();

    this.pillStyle = {
      left: `${linkRect.left - navRect.left}px`,
      width: `${linkRect.width}px`,
    };
    this.pillVisible = true;
  }

  onLinkClick(event: HTMLElement): void {
    if (!isPlatformBrowser(this.platform)) {
      return;
    }
    const nav = event.closest('ul') as HTMLElement;
    if (!nav) return;
    const navRect = nav.getBoundingClientRect();
    const linkRect = event.getBoundingClientRect();

    this.pillStyle = {
      left: `${linkRect.left - navRect.left}px`,
      width: `${linkRect.width}px`,
    };
    this.pillVisible = true;
  }
}
